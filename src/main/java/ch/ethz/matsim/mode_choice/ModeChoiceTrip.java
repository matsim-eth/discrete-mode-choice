package ch.ethz.matsim.mode_choice;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

public interface ModeChoiceTrip {
	Person getPerson();
	Link getOriginLink();
	Link getDestinationLink();
	double getDepartureTime();
}
