package ch.ethz.matsim.mode_choice.framework;

import java.util.List;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.TripStructureUtils.Trip;

public interface ModeChoiceTrip {
	Person getPerson();

	List<Trip> getPlan();

	Trip getTripInformation();
	
	String getInitialMode();
}
