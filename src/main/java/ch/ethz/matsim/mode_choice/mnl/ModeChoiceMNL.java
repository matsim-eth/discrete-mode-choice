package ch.ethz.matsim.mode_choice.mnl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.pt.PtConstants;

import ch.ethz.matsim.mode_choice.DefaultModeChoiceTrip;
import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class ModeChoiceMNL implements ModeChoiceModel {
	final private Random random;

	final private List<String> modes = new LinkedList<>();
	final private List<ModeChoiceAlternative> alternatives = new LinkedList<>();

	final private MNLDistribution distribution = new MNLDistribution();
	
	final private TripChainAlternatives tripChainAlternatives;
	final private Network network;

	public ModeChoiceMNL(Random random, TripChainAlternatives tripChainAlternatives, Network network) {
		this.tripChainAlternatives = tripChainAlternatives;
		this.random = random;
		this.network = network;
	}

	public String chooseMode(Person person, Link originLink, Link destinationLink) {
		List<Double> exp = alternatives.stream()
				.map(a -> Math.exp(a.estimateUtility(person, originLink, destinationLink)))
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
	public List<String> chooseModes(Person person, Plan plan) {		
		List<List<String>> feasibleTripChains = tripChainAlternatives.getTripChainAlternatives(plan, chainModes, nonChainModes);
		List<Double> chainProbabilities = new ArrayList<>(feasibleTripChains.size());
		
		List<TripStructureUtils.Trip> tt = TripStructureUtils.getTrips(plan, new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE));
		List<ModeChoiceTrip> trips = new LinkedList<>();
		
		for (TripStructureUtils.Trip trip : tt) {
			trips.add(new DefaultModeChoiceTrip(
					network.getLinks().get(trip.getOriginActivity().getLinkId()), 
					network.getLinks().get(trip.getDestinationActivity().getLinkId())));
		}
		
		for (List<String> tripChain : feasibleTripChains) {
			double logsum = 0.0;

			for (int j = 0; j < trips.size(); j++) {
				logsum += Math.log(distribution.getProbability(tripChain.get(j), person, trips.get(j)));
			}

			chainProbabilities.add(Math.exp(logsum));
		}

		List<Double> cumulativeProbabilities = new ArrayList<>(chainProbabilities.size());
		cumulativeProbabilities.add(chainProbabilities.get(0));

		for (int i = 1; i < chainProbabilities.size(); i++) {
			cumulativeProbabilities.add(cumulativeProbabilities.get(i - 1) + chainProbabilities.get(i));
		}

		double selector = random.nextDouble();

		for (int i = 0; i < cumulativeProbabilities.size(); i++) {
			if (selector < cumulativeProbabilities.get(i)) {
				return feasibleTripChains.get(i);
			}
		}

		throw new IllegalStateException();
	}
}
