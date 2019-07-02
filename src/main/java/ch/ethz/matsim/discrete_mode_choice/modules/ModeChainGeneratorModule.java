package ch.ethz.matsim.discrete_mode_choice.modules;

import com.google.inject.Provider;
import com.google.inject.Provides;

import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.TourFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.DefaultModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.DefaultModeChainGenerator;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.FilterRandomThresholdModeChainGenerator;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.ModeChainGeneratorFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.DefaultModeChainGenerator;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ModeAvailabilityConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ModeChainFilterRandomThresholdConfigGroup;


public class ModeChainGeneratorModule extends AbstractDiscreteModeChoiceExtension{
	
	public final static String ALL_COMBINATIONS = "allCombinations";
	public final static String FILTER_RANDOM_THRESHOLD = "filterRandomThreshold";

	@Override
	public void installExtension() {
		bindModeChainGenerator(ALL_COMBINATIONS).to(DefaultModeChainGenerator.Factory.class);
		bindModeChainGenerator(FILTER_RANDOM_THRESHOLD).to(FilterRandomThresholdModeChainGenerator.Factory.class);
	}
	
	@Provides
	public FilterRandomThresholdModeChainGenerator.Factory provideFilterRandomThresholdModeChainGeneratorFactory(DiscreteModeChoiceConfigGroup dmcConfig) {
		return new FilterRandomThresholdModeChainGenerator.Factory(dmcConfig);
	}
	@Provides
	public DefaultModeChainGenerator.Factory provideDefaultModeChainGeneratorFactory(DiscreteModeChoiceConfigGroup dmcConfig) {
		return new DefaultModeChainGenerator.Factory(dmcConfig);
	}
	
	@Provides
	public ModeChainGeneratorFactory provideModeChainGenerator(DiscreteModeChoiceConfigGroup dmcConfig) {
		switch (dmcConfig.getModeChainGeneratorAsString()) {
		case ALL_COMBINATIONS:
			return new DefaultModeChainGenerator.Factory(dmcConfig);
		case FILTER_RANDOM_THRESHOLD:
			return new FilterRandomThresholdModeChainGenerator.Factory(dmcConfig);
		default:
			throw new IllegalStateException("The param modeChainGenerator in the module DiscreteModeChoice is not allowed.");
		}
	}
	
}