package ch.ethz.matsim.mode_choice.mnl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

/* deliberately not public */
class MNLDistribution {
	final private List<String> modes = new LinkedList<>();
	final private List<ModeChoiceAlternative> alternatives = new LinkedList<>();

	public void addAlternative(String mode, ModeChoiceAlternative alternative) {
		this.modes.add(mode);
		this.alternatives.add(alternative);
	}

	public double getProbability(String mode, Person person, ModeChoiceTrip trip) {
		List<Double> logits = new ArrayList<>(modes.size());

		for (int i = 0; i < modes.size(); i++) {
			logits.add(Math
					.exp(alternatives.get(i).estimateUtility(person, trip)));
		}

		return logits.get(modes.indexOf(mode)) / logits.stream().mapToDouble(Double::doubleValue).sum();
	}
}
