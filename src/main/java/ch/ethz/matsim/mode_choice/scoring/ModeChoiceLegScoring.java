/* *********************************************************************** *
 * project: org.matsim.*
 * CharyparNagelOpenTimesScoringFunctionFactory.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package ch.ethz.matsim.mode_choice.scoring;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.pt.PtConstants;

public class ModeChoiceLegScoring implements org.matsim.core.scoring.SumScoringFunction.LegScoring, org.matsim.core.scoring.SumScoringFunction.ArbitraryEventScoring {

	protected double score;

	/** The parameters used for scoring */
	private Person person;
	private double distancept = 0.0;
	private ScoringModes utilityScoring;
	private int transfers;

	private double traveltimept;
	private boolean ptleg = false;
	
	public ModeChoiceLegScoring(ScoringModes utilityScoring, Person person) {
		
		this.person = person;
		this.utilityScoring = utilityScoring;
	}

	@Override
	public void finish() {

	}

	@Override
	public double getScore() {
		return this.score;
	}

	protected double calcLegScore(final double departureTime, final double arrivalTime, final Leg leg) {
		
		double tmpScore = 0.0;
		double travelTime = arrivalTime - departureTime; // travel time in seconds	
		
		if (!(leg.getMode().equals(TransportMode.pt) || leg.getMode().equals(TransportMode.transit_walk))) {
			
			if (TransportMode.bike.equals(leg.getMode()))
				utilityScoring.scoreBikeTrip(leg.getRoute().getDistance(), travelTime, person);
			if (TransportMode.walk.equals(leg.getMode()))
				utilityScoring.scoreWalkTrip(leg.getRoute().getDistance(), person);
		//	ModeChoiceTrip trip = new ModeChoiceTrip();
		//	tmpScore += modeChoiceAlternative.estimateUtility(trip);
		}
		else {
			
			if (leg.getMode().equals(TransportMode.pt)) {
				this.distancept += leg.getRoute().getDistance();
				this.traveltimept += leg.getRoute().getTravelTime(); 
			}
			if (leg.getMode().equals(TransportMode.transit_walk))
				this.traveltimept += leg.getRoute().getTravelTime();
		}
		// (yyyy once we have multiple legs without "real" activities in between, this will produce wrong results.  kai, dec'12)
		// (yy NOTE: the constant is added for _every_ pt leg.  This is not how such models are estimated.  kai, nov'12)
		return tmpScore;
	}
	
	@Override
	public void handleEvent(Event event) {
		
		if (event instanceof ActivityEndEvent) {
			
			if (!PtConstants.TRANSIT_ACTIVITY_TYPE.equals( ((ActivityEndEvent)event).getActType())) {
				
				this.resetpt();
				
			}
			else {
				this.transfers++;
			}
		}		
		if (event instanceof ActivityStartEvent) {
			if (!PtConstants.TRANSIT_ACTIVITY_TYPE.equals( ((ActivityEndEvent)event).getActType())) {
				if (ptleg)
					score += utilityScoring.scorePtTrip(distancept, traveltimept, transfers, person);
			}
		}
		
	}

	private void resetpt() {

		this.transfers = -1;
		this.distancept = 0.0;
		this.traveltimept = 0.0;
		ptleg = false;
	}

	@Override
	public void handleLeg(Leg leg) {
		double legScore = calcLegScore(leg.getDepartureTime(), leg.getDepartureTime() + leg.getTravelTime(), leg);
		this.score += legScore;
	}


}
