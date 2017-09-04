package ch.ethz.matsim.mode_choice.mnl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
	
	public String chooseMode(Person person, Trip trip) {
		List<Double> exp = alternatives.stream().map(a -> a.estimateUtility(person, trip)).collect(Collectors.toList());
		
		double total = exp.stream().mapToDouble(Double::doubleValue).sum();
		List<Double> probabilities = exp.stream().map(d -> d / total).collect(Collectors.toList());
		
		List<Double> cumulativeProbabilities = new ArrayList<>(probabilities.size());
		cumulativeProbabilities.add(probabilities.get(0));
		
		for (int i = 1; i < probabilities.size(); i++) {
			cumulativeProbabilities.set(i, cumulativeProbabilities.get(i - 1) + probabilities.get(i));
		}
		
		double selector = random.nextDouble();
		return null; // TODO
	}

	public void addModeAlternative(String mode, ModeChoiceAlternative alternative) {
		if (modes.contains(mode)) {
			throw new IllegalArgumentException(String.format("Alternative '%s' already exists", mode));
		}

		alternatives.add(alternative);
		modes.add(mode);
	}
}
