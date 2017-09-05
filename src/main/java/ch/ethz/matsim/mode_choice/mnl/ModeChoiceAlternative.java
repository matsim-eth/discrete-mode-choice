package ch.ethz.matsim.mode_choice.mnl;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

public interface ModeChoiceAlternative {
	double estimateUtility(Person person, Link originLink, Link destinationLink);
	boolean isChainMode();
}
