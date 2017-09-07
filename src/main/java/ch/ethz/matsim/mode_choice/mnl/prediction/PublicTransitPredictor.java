package ch.ethz.matsim.mode_choice.mnl.prediction;

import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.facilities.Facility;
import org.matsim.pt.router.TransitRouter;
import org.matsim.pt.routes.ExperimentalTransitRoute;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class PublicTransitPredictor implements TripPredictor {
	final private TransitSchedule schedule;
	final private TransitRouter router;
	final private double walkDistanceFactor;

	public PublicTransitPredictor(TransitRouter router, TransitSchedule schedule, double walkDistanceFactor) {
		this.router = router;
		this.schedule = schedule;
		this.walkDistanceFactor = walkDistanceFactor;
	}

	private double getWaitingTime(double legDepartureTime, ExperimentalTransitRoute route) {
		TransitLine transitLine = schedule.getTransitLines().get(route.getLineId());
		TransitRoute transitRoute = transitLine.getRoutes().get(route.getRouteId());

		TransitStopFacility stopFacility = schedule.getFacilities().get(route.getAccessStopId());
		TransitRouteStop stop = transitRoute.getStop(stopFacility);

		double departureOffset = stop.getDepartureOffset();

		for (Departure departure : transitRoute.getDepartures().values()) {
			double departureTime = departure.getDepartureTime() + departureOffset;

			if (departureTime > legDepartureTime) {
				return legDepartureTime - departureTime;
			}
		}

		throw new IllegalStateException();
	}

	private double getWalkDistance(double travelTime) {
		return travelTime * walkDistanceFactor;
	}

	@Override
	public PublicTransitTripPrediction predictTrip(ModeChoiceTrip trip) {
		List<Leg> legs = router.calcRoute(new LinkFacility(trip.getOriginLink()),
				new LinkFacility(trip.getDestinationLink()), trip.getDepartureTime(), trip.getPerson());

		double transferTravelTime = 0.0;
		double transferDistance = 0.0;

		double vehicleTravelTime = 0.0;
		double vehicleDistance = 0.0;

		double waitingTime = 0.0;

		int numberOfLineSwitches = legs.size() - 1;
		boolean isOnlyTransitWalk = legs.size() == 1 && legs.get(0).getMode().contains("walk");

		if (!isOnlyTransitWalk) {
			for (Leg leg : legs) {
				if (leg.getMode().equals("pt")) {
					ExperimentalTransitRoute route = (ExperimentalTransitRoute) leg.getRoute();

					double legWaitingTime = getWaitingTime(leg.getDepartureTime(), route);
					waitingTime += legWaitingTime;

					vehicleTravelTime += route.getTravelTime() - legWaitingTime;
					vehicleDistance += route.getDistance();
				} else if (leg.getMode().contains("walk")) {
					transferTravelTime += leg.getTravelTime();
					transferDistance += getWalkDistance(leg.getTravelTime());
				} else {
					throw new IllegalStateException();
				}
			}
		} else {
			transferTravelTime = legs.get(0).getTravelTime();
			transferDistance = getWalkDistance(legs.get(0).getTravelTime());
		}

		return new PublicTransitTripPrediction(vehicleTravelTime, vehicleDistance, transferTravelTime, transferDistance,
				waitingTime, numberOfLineSwitches, isOnlyTransitWalk);
	}

	private class LinkFacility implements Facility {
		final private Link link;

		public LinkFacility(Link link) {
			this.link = link;
		}

		@Override
		public Coord getCoord() {
			return link.getCoord();
		}

		@Override
		public Id<Link> getId() {
			return link.getId();
		}

		@Override
		public Map<String, Object> getCustomAttributes() {
			return null;
		}

		@Override
		public Id<Link> getLinkId() {
			return link.getId();
		}
	}
}
