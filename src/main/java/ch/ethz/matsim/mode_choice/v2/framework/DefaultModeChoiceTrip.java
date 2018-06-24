package ch.ethz.matsim.mode_choice.v2.framework;

import java.util.List;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.TripStructureUtils.Trip;

public class DefaultModeChoiceTrip implements ModeChoiceTrip {
	final private Person person;
	final private List<Trip> plan;
	final private Trip trip;
	final private String initialMode;

	public DefaultModeChoiceTrip(Person person, List<Trip> plan, Trip trip, String initialMode) {
		this.person = person;
		this.plan = plan;
		this.trip = trip;
		this.initialMode = initialMode;
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public List<Trip> getPlan() {
		return plan;
	}

	@Override
	public Trip getTripInformation() {
		return trip;
	}

	@Override
	public String getInitialMode() {
		return initialMode;
	}
}
