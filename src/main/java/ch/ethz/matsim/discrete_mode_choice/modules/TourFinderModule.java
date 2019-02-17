package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.ActivityTourFinder;
import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.PlanTourFinder;
import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.TourFinder;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ActivityTourFinderConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

/**
 * Internal module that manages all built-in TourFinder implementations.
 * 
 * @author sebhoerl
 *
 */
public class TourFinderModule extends AbstractDiscreteModeChoiceExtension {
	public static final String PLAN_BASED = "PlanBased";
	public static final String ACTIVITY_BASED = "ActivityBased";

	public static final Collection<String> COMPONENTS = Arrays.asList(PLAN_BASED, ACTIVITY_BASED);

	@Override
	public void installExtension() {
		bindTourFinder(PLAN_BASED).to(PlanTourFinder.class);
		bindTourFinder(ACTIVITY_BASED).to(ActivityTourFinder.class);
	}

	@Provides
	@Singleton
	public PlanTourFinder providePlanTourFinder() {
		return new PlanTourFinder();
	}

	@Provides
	@Singleton
	public ActivityTourFinder provideActivityBasedTourFinder(DiscreteModeChoiceConfigGroup dmcConfig) {
		ActivityTourFinderConfigGroup config = dmcConfig.getActivityTourFinderConfigGroup();
		return new ActivityTourFinder(config.getActivityType());
	}

	@Provides
	@Singleton
	public TourFinder provideTourFinder(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<TourFinder>> components) {
		Provider<TourFinder> provider = components.get(dmcConfig.getTourFinder());

		if (provider != null) {
			return provider.get();
		} else {
			throw new IllegalStateException(
					String.format("There is no TourFinder component called '%s',", dmcConfig.getTourFinder()));
		}
	}
}
