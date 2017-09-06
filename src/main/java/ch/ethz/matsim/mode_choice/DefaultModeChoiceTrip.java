package ch.ethz.matsim.mode_choice;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

public class DefaultModeChoiceTrip implements ModeChoiceTrip {
	final private Link originLink;
	final private Link destinationLink;
	final private double departureTime;
	final private Person person;
	
	public DefaultModeChoiceTrip(Link originLink, Link destinationLink, double departureTime, Person person) {
		this.originLink = originLink;
		this.destinationLink = destinationLink;
		this.departureTime = departureTime;
		this.person = person;
	}
	
	@Override
	public Link getOriginLink() {
		return originLink;
	}

	@Override
	public Link getDestinationLink() {
		return destinationLink;
	}

	@Override
	public double getDepartureTime() {
		return departureTime;
	}

	@Override
	public Person getPerson() {
		return person;
	}
}
