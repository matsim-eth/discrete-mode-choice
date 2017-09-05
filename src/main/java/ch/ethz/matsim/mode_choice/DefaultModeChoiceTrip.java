package ch.ethz.matsim.mode_choice;

import org.matsim.api.core.v01.network.Link;

public class DefaultModeChoiceTrip implements ModeChoiceTrip {
	final private Link originLink;
	final private Link destinationLink;
	
	public DefaultModeChoiceTrip(Link originLink, Link destinationLink) {
		this.originLink = originLink;
		this.destinationLink = destinationLink;
	}
	
	@Override
	public Link getOriginLink() {
		return originLink;
	}

	@Override
	public Link getDestinationLink() {
		return destinationLink;
	}

}
