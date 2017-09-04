package ch.ethz.matsim.mode_choice;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.TripStructureUtils.Trip;

public interface ModeChoiceModel {
	String chooseMode(Person person, Trip trip);
}
