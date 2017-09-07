package ch.ethz.matsim.mode_choice.scoring;

import org.matsim.api.core.v01.population.Person;

public interface ScoringModes {

	double scoreCarTrip(double distance, double traveltime, Person person);

	double scoreWalkTrip(double traveltime, Person person);

	double scoreBikeTrip(double distance, double traveltime, Person person);

	double scorePtTrip(double distance, double traveltime, int transfers, Person person);

}