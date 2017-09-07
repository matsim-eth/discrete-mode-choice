package ch.ethz.matsim.mode_choice.scoring;

import java.util.Map;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;

public class ModeScoringUtil implements ScoringModes {
	
	Map<String, ModeChoiceScoringParameters> parameters;
	Scenario scenario;
	
	
	
	public ModeScoringUtil(Map<String, ModeChoiceScoringParameters> parameters, Scenario scenario) {
		this.parameters = parameters;
		this.scenario = scenario;
	}

	@Override
	public double scoreCarTrip(double distance, double traveltime, Person person) {
		
		ModeChoiceScoringParameters modeParam = parameters.get("car");
		
		return modeParam.constant + modeParam.betatraveltime * traveltime +
				modeParam.betadistance * distance;
	}
	
	@Override
	public double scoreWalkTrip(double traveltime, Person person) {
		ModeChoiceScoringParameters modeParam = parameters.get("walk");
		int age = (int) scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "age");
		int man = scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "sex").equals("m") ? 1 : 0;
		int caravail = scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "caravail").equals("always") ? 1 : 0;
		
		return modeParam.constant + modeParam.betatraveltime * traveltime + modeParam.betaage * age + 
				modeParam.betagender * man + modeParam.betacarAvail * caravail;

	}
	
	@Override
	public double scoreBikeTrip(double distance, double traveltime, Person person) {
		ModeChoiceScoringParameters modeParam = parameters.get("bike");
		int age = (int) scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "age");
		int man = scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "sex").equals("m") ? 1 : 0;
		int caravail = scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "caravail").equals("always") ? 1 : 0;
		
		return modeParam.constant + modeParam.betatraveltime * traveltime + modeParam.betaage * age + 
				modeParam.betagender * man + modeParam.betacarAvail * caravail;
	}
	
	@Override
	public double scorePtTrip(double distance, double traveltime, int transfers, Person person) {
		ModeChoiceScoringParameters modeParam = parameters.get("pt");
		int age = (int) scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "age");
		int man = scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "sex").equals("m") ? 1 : 0;
		int caravail = scenario.getPopulation().getPersonAttributes().getAttribute(person.getId().toString(), "caravail").equals("always") ? 1 : 0;
		
		return modeParam.constant + modeParam.betatraveltime * traveltime + modeParam.betaage * age + 
				modeParam.betagender * man + modeParam.betacarAvail * caravail + modeParam.betadistance * distance +
				modeParam.betatransfers * transfers;
	}

}
