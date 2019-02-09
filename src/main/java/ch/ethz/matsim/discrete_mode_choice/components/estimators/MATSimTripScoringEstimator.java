package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.List;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.core.router.TripRouter;
import org.matsim.core.scoring.functions.ModeUtilityParameters;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.Facility;
import org.matsim.pt.routes.ExperimentalTransitRoute;

import ch.ethz.matsim.discrete_mode_choice.components.utils.PublicTransitWaitingTimeEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This trip estimator tries to resemble the MATSim scoring functions as closely
 * as possible. The utility parameters are taken directly from the config file.
 * 
 * @author sebhoerl
 *
 */
public class MATSimTripScoringEstimator implements TripEstimator {
	private final Network network;
	private final ActivityFacilities facilities;
	private final TripRouter tripRouter;
	private final ScoringParametersForPerson scoringParametersForPerson;
	private final PublicTransitWaitingTimeEstimator waitingTimeEstimator;

	public MATSimTripScoringEstimator(Network network, ActivityFacilities facilities, TripRouter tripRouter,
			PublicTransitWaitingTimeEstimator waitingTimeEstimator,
			ScoringParametersForPerson scoringParametersForPerson) {
		this.network = network;
		this.facilities = facilities;
		this.tripRouter = tripRouter;
		this.waitingTimeEstimator = waitingTimeEstimator;
		this.scoringParametersForPerson = scoringParametersForPerson;
	}

	@Override
	public TripCandidate estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips) {
		// I) Find the correct origin and destination facilities
		Facility originFacility = facilities.getFacilities().get(trip.getOriginActivity().getFacilityId());

		if (originFacility == null) {
			originFacility = new LinkWrapperFacility(network.getLinks().get(trip.getOriginActivity().getLinkId()));
		}

		Facility destinationFacility = facilities.getFacilities().get(trip.getDestinationActivity().getFacilityId());

		if (destinationFacility == null) {
			destinationFacility = new LinkWrapperFacility(
					network.getLinks().get(trip.getDestinationActivity().getLinkId()));
		}

		// II) Perform the routing
		List<? extends PlanElement> elements = tripRouter.calcRoute(mode, originFacility, destinationFacility,
				trip.getDepartureTime(), person);

		// III) Perform the scoring, although we can only do that approximatively

		ComputationResult result = null;
		ScoringParameters parameters = scoringParametersForPerson.getScoringParameters(person);

		switch (mode) {
		case TransportMode.car:
		case TransportMode.bike:
		case TransportMode.walk:
			result = computeStandardLeg(parameters, elements);
			break;
		case TransportMode.pt:
			result = computePtLeg(parameters, elements, trip.getDepartureTime());
			break;
		default:
			throw new IllegalStateException("Only car, bike, walk, pt supported at the moment.");
		}

		return new MATSimTripCandidate(result.utility, mode, elements, result.travelTime);
	}

	private class ComputationResult {
		double travelTime;
		double utility;

		ComputationResult(double travelTime, double utility) {
			this.travelTime = travelTime;
			this.utility = utility;
		}
	}

	private double computeLegUtility(ScoringParameters parameters, String mode, double travelTime,
			double travelDistance) {
		ModeUtilityParameters modeParams = parameters.modeParams.get(mode);

		double utility = modeParams.constant;
		utility += modeParams.marginalUtilityOfTraveling_s * travelTime;
		utility += modeParams.marginalUtilityOfDistance_m * travelDistance;
		utility += parameters.marginalUtilityOfMoney * modeParams.monetaryDistanceCostRate * travelDistance;
		return utility;
	}

	private ComputationResult computeStandardLeg(ScoringParameters parameters, List<? extends PlanElement> elements) {
		double utility = 0.0;
		double travelTime = 0.0;

		for (PlanElement element : elements) {
			if (element instanceof Leg) {
				Leg leg = (Leg) element;

				travelTime += leg.getTravelTime();
				utility += computeLegUtility(parameters, leg.getMode(), leg.getTravelTime(),
						leg.getRoute().getDistance());
			}
		}

		return new ComputationResult(travelTime, utility);
	}

	private ComputationResult computePtLeg(ScoringParameters parameters, List<? extends PlanElement> elements,
			double departureTime) {
		ComputationResult result = computeStandardLeg(parameters, elements);

		int numberOfVehicularLegs = 0;
		double totalWaitingTime = 0.0;

		double time = departureTime;

		for (PlanElement element : elements) {
			if (element instanceof Leg) {
				Leg leg = (Leg) element;

				if (leg.getMode().equals(TransportMode.pt)) {
					ExperimentalTransitRoute route = (ExperimentalTransitRoute) leg.getRoute();
					totalWaitingTime += waitingTimeEstimator.estimateWaitingTime(time, route);

					numberOfVehicularLegs++;
				}

				time += leg.getTravelTime();
			}
		}

		result.utility += parameters.marginalUtilityOfWaitingPt_s * totalWaitingTime;
		result.travelTime += totalWaitingTime;

		if (numberOfVehicularLegs > 0) {
			result.utility += parameters.utilityOfLineSwitch * (numberOfVehicularLegs - 1);
		}

		return result;
	}
}
