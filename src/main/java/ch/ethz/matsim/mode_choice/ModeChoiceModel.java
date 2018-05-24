package ch.ethz.matsim.mode_choice;

import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.population.Plan;

public interface ModeChoiceModel {
	String chooseMode(ModeChoiceTrip trip, Random random);

	List<String> chooseModes(Plan plan, Random random);
}
