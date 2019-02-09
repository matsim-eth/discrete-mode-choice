package ch.ethz.matsim.discrete_mode_choice.framework.tour_based;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Trip;

import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.ActivityTourFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class ActivityTourFinderTest {
	private List<DiscreteModeChoiceTrip> createFixture(String... activityTypes) {
		Plan plan = PopulationUtils.createPlan();
		boolean isFirst = true;

		for (String activityType : activityTypes) {
			if (!isFirst) {
				PopulationUtils.createAndAddLeg(plan, "");
			}

			PopulationUtils.createAndAddActivity(plan, activityType);
			isFirst = false;
		}

		List<Trip> trips = TripStructureUtils.getTrips(plan, new StageActivityTypesImpl());
		List<DiscreteModeChoiceTrip> modeChoiceTrips = new LinkedList<>();

		for (Trip trip : trips) {
			String initialMode = trip.getLegsOnly().get(0).getMode();
			modeChoiceTrips.add(new DiscreteModeChoiceTrip(trip.getOriginActivity(), trip.getDestinationActivity(),
					initialMode, 0.0));
		}

		return modeChoiceTrips;
	}

	@Test
	public void testActivityTourFinder() {
		ActivityTourFinder finder = new ActivityTourFinder("home");

		List<DiscreteModeChoiceTrip> trips;
		List<List<DiscreteModeChoiceTrip>> result;

		trips = createFixture("home", "work", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(2, result.stream().mapToInt(List::size).sum());

		trips = createFixture("other", "home", "work", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(3, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(1, result.get(0).size());
		Assert.assertEquals(2, result.get(1).size());

		trips = createFixture("home", "work", "home", "other");
		result = finder.findTours(trips);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(3, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(2, result.get(0).size());
		Assert.assertEquals(1, result.get(1).size());

		trips = createFixture("home", "work", "shop", "home", "other", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(5, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(3, result.get(0).size());
		Assert.assertEquals(2, result.get(1).size());

		trips = createFixture("home", "work", "home", "home", "work", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(3, result.size());
		Assert.assertEquals(5, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(2, result.get(0).size());
		Assert.assertEquals(1, result.get(1).size());
		Assert.assertEquals(2, result.get(2).size());
	}
}
