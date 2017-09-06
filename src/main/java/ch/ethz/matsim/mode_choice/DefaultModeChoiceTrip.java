package ch.ethz.matsim.mode_choice;

import org.matsim.api.core.v01.network.Link;

public class DefaultModeChoiceTrip implements ModeChoiceTrip {
	final private Link originLink;
	final private Link destinationLink;
	final private double departureTime;
	
	public DefaultModeChoiceTrip(Link originLink, Link destinationLink, double departureTime) {
		this.originLink = originLink;
		this.destinationLink = destinationLink;
		this.departureTime = departureTime;
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

}
