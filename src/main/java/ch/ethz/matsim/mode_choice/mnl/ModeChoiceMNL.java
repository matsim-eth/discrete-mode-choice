package ch.ethz.matsim.mode_choice.mnl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.TripStructureUtils.Trip;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ModeChoiceMNL implements ModeChoiceModel {
	final private Random random;
	final private List<String> modes = new LinkedList<>();
	final private List<ModeChoiceAlternative> alternatives = new LinkedList<>();
	
	public ModeChoiceMNL(Random random) {
		this.random = random;
	}
	
	public String chooseMode(Person person, Link originLink, Link destinationLink) {
		List<Double> exp = alternatives.stream().map(a -> Math.exp(a.estimateUtility(person, originLink, destinationLink))).collect(Collectors.toList());
		
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

	public void addModeAlternative(String mode, ModeChoiceAlternative alternative) {
		if (modes.contains(mode)) {
			throw new IllegalArgumentException(String.format("Alternative '%s' already exists", mode));
		}

		alternatives.add(alternative);
		modes.add(mode);
	}
}
