package ch.ethz.matsim.discrete_mode_choice.modules;

import org.matsim.core.controler.AbstractModule;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.TourFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.DefaultModeChainGenerator;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.ModeChainGeneratorFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

/**
 * Internal module that sets up the acutal choice models according to
 * configuration.
 * 
 * @author sebhoerl
 *
 */
public class ModelModule extends AbstractModule {
	@Override
	public void install() {
		install(new ModeAvailabilityModule());
		install(new EstimatorModule());
		install(new TourFinderModule());
		install(new SelectorModule());
		install(new ConstraintModule());
		install(new ModeChainGeneratorModule());

	}

	public enum ModelType {
		Trip, Tour
	}

	@Provides
	public DiscreteModeChoiceModel provideDiscreteModeChoiceModel(DiscreteModeChoiceConfigGroup dmcConfig,
			Provider<TourBasedModel> tourBasedProvider, Provider<TripBasedModel> tripBasedProvider) {
		switch (dmcConfig.getModelType()) {
		case Tour:
			return tourBasedProvider.get();
		case Trip:
			return tripBasedProvider.get();
		default:
			throw new IllegalStateException();
		}
	}

	@Provides
	public TourBasedModel provideTourBasedModel(ModeAvailability modeAvailability, TourEstimator tourEstimator,
			TourConstraintFactory tourConstraintFactory, TourFinder tourFinder,
			UtilitySelectorFactory<TourCandidate> selectorFactory, ModeChainGeneratorFactory modeChainGeneratorFactory,
			DiscreteModeChoiceConfigGroup dmcConfig) {
		return new TourBasedModel(tourEstimator, modeAvailability, tourConstraintFactory, tourFinder, selectorFactory,
				modeChainGeneratorFactory, dmcConfig.getFallbackBehaviour());
	}

	@Provides
	public TripBasedModel provideTripBasedModel(TripEstimator estimator, ModeAvailability modeAvailability,
			TripConstraintFactory constraintFactory, UtilitySelectorFactory<TripCandidate> selectorFactory,
			DiscreteModeChoiceConfigGroup dmcConfig) {
		return new TripBasedModel(estimator, modeAvailability, constraintFactory, selectorFactory,
				dmcConfig.getFallbackBehaviour());
	}

	
}
