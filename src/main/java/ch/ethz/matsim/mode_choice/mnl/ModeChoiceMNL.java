package ch.ethz.matsim.mode_choice.mnl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.pt.PtConstants;

import ch.ethz.matsim.mode_choice.DefaultModeChoiceTrip;
import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.alternatives.ChainAlternatives;

public class ModeChoiceMNL implements ModeChoiceModel {
	final private Random random;

	final private List<String> modes = new LinkedList<>();
	final private List<ModeChoiceAlternative> alternatives = new LinkedList<>();

	final private MNLDistribution distribution = new MNLDistribution();

	final private ChainAlternatives chainAlternatives;
	final private Network network;
	
	final static private double defaultChainPrior = 1e-12;
	final private double chainPrior;

	public enum Mode {
		SAMPLING, BEST_RESPONSE
	}

	final private Mode modelMode;

	public ModeChoiceMNL(Random random, ChainAlternatives tripChainAlternatives, Network network, Mode modelMode, double chainPrior) {
		this.chainAlternatives = tripChainAlternatives;
		this.random = random;
		this.network = network;
		this.modelMode = modelMode;
		this.chainPrior = chainPrior;
	}

	public ModeChoiceMNL(Random random, ChainAlternatives tripChainAlternatives, Network network, Mode modelMode) {
		this(random, tripChainAlternatives, network, modelMode, defaultChainPrior);
	}

	public String chooseMode(ModeChoiceTrip trip) {
		List<Double> exp = alternatives.stream().map(a -> Math.exp(a.estimateUtility(trip)))
				.collect(Collectors.toList());

		double total = exp.stream().mapToDouble(Double::doubleValue).sum();
		List<Double> probabilities = exp.stream().map(d -> d / total).collect(Collectors.toList());

		List<Double> cumulativeProbabilities = new ArrayList<>(probabilities.size());
		cumulativeProbabilities.add(probabilities.get(0));

		for (int i = 1; i < probabilities.size(); i++) {
			cumulativeProbabilities.add(cumulativeProbabilities.get(i - 1) + probabilities.get(i));
		}

		double selector = random.nextDouble();

		for (int i = 0; i < cumulativeProbabilities.size(); i++) {
			if (selector < cumulativeProbabilities.get(i)) {
				return modes.get(i);
			}
		}

		throw new IllegalStateException();
	}

	private List<String> chainModes = new LinkedList<>();
	private List<String> nonChainModes = new LinkedList<>();

	public void addModeAlternative(String mode, ModeChoiceAlternative alternative) {
		if (modes.contains(mode)) {
			throw new IllegalArgumentException(String.format("Alternative '%s' already exists", mode));
		}

		alternatives.add(alternative);
		modes.add(mode);

		distribution.addAlternative(mode, alternative);

		if (alternative.isChainMode()) {
			chainModes.add(mode);
		} else {
			nonChainModes.add(mode);
		}
	}

	@Override
	public List<String> chooseModes(Plan plan) {
		boolean debug = false;

		List<String> individualChainModes = chainModes;

		if (PersonUtils.getCarAvail(plan.getPerson()).equals("never")) {// || !PersonUtils.hasLicense(plan.getPerson()))
																		// {
			individualChainModes = chainModes.stream().filter(m -> !m.equals("car")).collect(Collectors.toList());
		}

		List<List<String>> feasibleTripChains = chainAlternatives.getTripChainAlternatives(plan, individualChainModes,
				nonChainModes);
		List<Double> chainProbabilities = new ArrayList<>(feasibleTripChains.size());

		// System.gc();

		List<TripStructureUtils.Trip> tt = TripStructureUtils.getTrips(plan,
				new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE));

		if (tt.size() == 0) {
			return Collections.emptyList();
		}

		Id<Link> firstLinkId = tt.get(0).getOriginActivity().getLinkId();

		List<ModeChoiceTrip> trips = new LinkedList<>();

		for (TripStructureUtils.Trip trip : tt) {
			trips.add(new DefaultModeChoiceTrip(network.getLinks().get(trip.getOriginActivity().getLinkId()),
					network.getLinks().get(trip.getDestinationActivity().getLinkId()),
					trip.getOriginActivity().getEndTime(), plan.getPerson()));
		}

		if (debug) {
			System.err.println(String.format("%s is choosing modes [%s, %s] ...", plan.getPerson().getId().toString(),
					individualChainModes, nonChainModes));
			System.err.println(String.format("   he has %d chain alternatives", feasibleTripChains.size()));
			System.err.println(String.format("   with %d trips", trips.size()));
		}

		for (List<String> tripChain : feasibleTripChains) {
			double logsum = 0.0;

			for (int j = 0; j < trips.size(); j++) {
				double tripProbability = distribution.getProbability(tripChain.get(j), trips.get(j));

				if (tripProbability == 0.0) {
					logsum = Double.NEGATIVE_INFINITY;
					break;
				}

				logsum += Math.log(tripProbability);
			}
			
			double item = Math.exp(logsum);
			if (Double.isInfinite(item) || Double.isNaN(item)) item = 1e-12;
			
			chainProbabilities.add(item + chainPrior);
		}

		if (debug) {
			System.err.println("");
			System.err.println(String.format("   chain probabilities: min %f max %f",
					chainProbabilities.stream().mapToDouble(Double::doubleValue).min().getAsDouble(),
					chainProbabilities.stream().mapToDouble(Double::doubleValue).max().getAsDouble()) + chainProbabilities);
		}

		double total = chainProbabilities.stream().mapToDouble(Double::doubleValue).sum();

		List<Double> cumulativeProbabilities = new ArrayList<>(chainProbabilities.size());
		cumulativeProbabilities.add(chainProbabilities.get(0) / total);

		for (int i = 1; i < chainProbabilities.size(); i++) {
			cumulativeProbabilities.add(cumulativeProbabilities.get(i - 1) + chainProbabilities.get(i) / total);
		}

		if (modelMode.equals(Mode.SAMPLING)) {
			double selector = random.nextDouble();

			for (int i = 0; i < cumulativeProbabilities.size(); i++) {
				if (selector < cumulativeProbabilities.get(i)) {

					if (debug) {
						System.err.println("");
						System.err.println(String.format("   Choice: %d (prob %f)", i, chainProbabilities.get(i)));
						System.err.print("   Chain: ");

						for (int k = 0; k < feasibleTripChains.get(i).size(); k++) {
							if (tt.get(k).getOriginActivity().getLinkId().equals(firstLinkId)) {
								System.err.print("* ");
							}

							System.err.print(feasibleTripChains.get(i).get(k) + " ");
						}

						if (tt.get(feasibleTripChains.get(i).size() - 1).getDestinationActivity().getLinkId()
								.equals(firstLinkId)) {
							System.err.print("*");
						}

						System.err.println("");
						System.err.println("END");
					}

					return feasibleTripChains.get(i);
				}
			}
			
			throw new IllegalStateException();
		}

		if (modelMode.equals(Mode.BEST_RESPONSE)) {
			double maximumProbability = Double.NEGATIVE_INFINITY;
			int maximumIndex = 0;

			for (int i = 0; i < chainProbabilities.size(); i++) {
				if (chainProbabilities.get(i) > maximumProbability) {
					maximumIndex = i;
					maximumProbability = chainProbabilities.get(i);
				}
			}

			if (debug) {
				System.err.println("");
				System.err.println(
						String.format("   Choice: %d (prob %f)", maximumIndex, chainProbabilities.get(maximumIndex)));
				System.err.print("   Chain: ");

				for (int k = 0; k < feasibleTripChains.get(maximumIndex).size(); k++) {
					if (tt.get(k).getOriginActivity().getLinkId().equals(firstLinkId)) {
						System.err.print("* ");
					}

					System.err.print(feasibleTripChains.get(maximumIndex).get(k) + " ");
				}

				if (tt.get(feasibleTripChains.get(maximumIndex).size() - 1).getDestinationActivity().getLinkId()
						.equals(firstLinkId)) {
					System.err.print("*");
				}

				System.err.println("");
				System.err.println("END");
			}

			return feasibleTripChains.get(maximumIndex);
		}

		throw new IllegalStateException();
	}
}
