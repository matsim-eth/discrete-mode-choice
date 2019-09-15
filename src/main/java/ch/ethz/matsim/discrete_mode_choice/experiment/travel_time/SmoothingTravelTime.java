package ch.ethz.matsim.discrete_mode_choice.experiment.travel_time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.misc.Time;
import org.matsim.vehicles.Vehicle;

public class SmoothingTravelTime implements TravelTime, LinkEnterEventHandler, LinkLeaveEventHandler,
		VehicleLeavesTrafficEventHandler, AfterMobsimListener {
	private final double startTime;
	private final double interval;

	private final double alpha;

	private final int numberOfTimeBins;
	private final int numberOfLinks;

	private final Map<Id<Link>, Integer> id2index = new HashMap<>();
	private final Id<Link>[] index2id;

	private final double[][] cumulativeTravelTimes;
	private final double[][] travelTimeCounts;
	private final double[][] estimates;
	private final double[] defaults;

	private final List<Map<Id<Vehicle>, Double>> enterTimes;
	
	private final Random random = new Random(0);

	// Setup part: Handling indices and making space

	public SmoothingTravelTime(double startTime, double endTime, double interval, double alpha, Network network) {
		this.alpha = alpha;

		this.startTime = startTime;
		this.interval = interval;

		// TODO: Check if this is the correct number or if it should be one less
		this.numberOfTimeBins = 1 + (int) Math.floor((endTime - startTime) / interval);
		this.numberOfLinks = network.getLinks().size();

		this.cumulativeTravelTimes = new double[numberOfLinks][numberOfTimeBins];
		this.travelTimeCounts = new double[numberOfLinks][numberOfTimeBins];
		this.estimates = new double[numberOfLinks][numberOfTimeBins];
		this.defaults = new double[numberOfLinks];
		this.enterTimes = new ArrayList<>(numberOfLinks);
		this.index2id = new Id[numberOfLinks];

		int linkIndex = 0;

		for (Link link : network.getLinks().values()) {
			id2index.put(link.getId(), linkIndex);
			index2id[linkIndex] = link.getId();

			double defaultValue = Math.floor(link.getLength() / link.getFreespeed()) + 1.0;
			this.defaults[linkIndex] = defaultValue;

			for (int t = 0; t < numberOfTimeBins; t++) {
				this.estimates[linkIndex][t] = defaultValue;
			}

			enterTimes.add(new HashMap<>());

			linkIndex++;
		}
	}

	private int getLinkIndex(Id<Link> linkId) {
		Integer index = id2index.get(linkId);

		if (index == null) {
			throw new IllegalStateException("Requested link for which we don't have an index");
		}

		return index;
	}

	private int getTimeIndex(double time) {
		return Math.min(Math.max((int) Math.floor((time - startTime) / interval), 0), numberOfTimeBins - 1);
	}

	// TravelTime part: Providing travel times from the estimates

	@Override
	public double getLinkTravelTime(Link link, double time, Person person, Vehicle vehicle) {
		int linkIndex = getLinkIndex(link.getId());

		if (Time.isUndefinedTime(time)) {
			// Special case when OnlyTimeDependencyTravelDisutility requests the lower bound
			return defaults[linkIndex];
		}

		int timeIndex = getTimeIndex(time);
		
		return Math.max(estimates[linkIndex][timeIndex], defaults[linkIndex]); // + random.nextDouble() * 1e-9;
		// TODO: Is this max necessary? Can this ever happen?
	}

	// Estimation part: After Mobsim, consolidate travel times

	@Override
	public void notifyAfterMobsim(AfterMobsimEvent event) {
		for (int l = 0; l < numberOfLinks; l++) {
			for (int t = 0; t < numberOfTimeBins; t++) {
				double updatedTravelTime = defaults[l];
				double observedCount = travelTimeCounts[l][t];

				if (observedCount > 0.0) {
					updatedTravelTime = cumulativeTravelTimes[l][t] / observedCount;
				}

				estimates[l][t] = (1.0 - alpha) * estimates[l][t] + alpha * updatedTravelTime;
				// estimates[l][t] = Math.floor(estimates[l][t]) + 1.0;

				if (index2id[l].toString().equals("A") || index2id[l].toString().equals("B")) {
					if (t == 0) {
						System.err.print(index2id[l] + " " + updatedTravelTime + "   ");
					}
				}

				travelTimeCounts[l][t] = 0.0;
				cumulativeTravelTimes[l][t] = 0.0;
			}
		}

		System.err.println("");
		
		enterTimes.forEach(Map::clear);
	}

	// Tracking part: Read events and save the data

	@Override
	public void handleEvent(LinkEnterEvent event) {
		int linkIndex = getLinkIndex(event.getLinkId());
		enterTimes.get(linkIndex).put(event.getVehicleId(), event.getTime());
	}

	private void handleLeaveLink(Id<Vehicle> vehicleId, Id<Link> linkId, double time) {
		int linkIndex = getLinkIndex(linkId);
		Double enterTime = enterTimes.get(linkIndex).get(vehicleId);

		if (enterTime != null) {
			enterTimes.get(linkIndex).remove(vehicleId);

			int timeIndex = getTimeIndex(enterTime);
			cumulativeTravelTimes[linkIndex][timeIndex] += time - enterTime;
			travelTimeCounts[linkIndex][timeIndex] += 1.0;
		}
	}

	@Override
	public void handleEvent(VehicleLeavesTrafficEvent event) {
		handleLeaveLink(event.getVehicleId(), event.getLinkId(), event.getTime());
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		handleLeaveLink(event.getVehicleId(), event.getLinkId(), event.getTime());
	}
}
