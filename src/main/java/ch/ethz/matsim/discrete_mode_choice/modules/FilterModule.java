package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collection;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.discrete_mode_choice.components.filters.TourLengthFilter;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.TourLengthFilterConfigGroup;

/**
 * Internal module that manages all built-in constraints.
 * 
 * @author sebhoerl
 *
 */
public class FilterModule extends AbstractDiscreteModeChoiceExtension {
	public final static String TOUR_LENGTH = "TourLength";

	public final static Collection<String> TRIP_COMPONENTS = Arrays.asList();

	public final static Collection<String> TOUR_COMPONENTS = Arrays.asList(TOUR_LENGTH);

	@Override
	public void installExtension() {
		bindTourFilter(TOUR_LENGTH).to(TourLengthFilter.class);
	}

	@Provides
	@Singleton
	public TourLengthFilter provideTourLengthFilter(DiscreteModeChoiceConfigGroup dmcConfig) {
		TourLengthFilterConfigGroup config = dmcConfig.getTourLengthFilterConfigGroup();
		return new TourLengthFilter(config.getMaximumLength());
	}
}
