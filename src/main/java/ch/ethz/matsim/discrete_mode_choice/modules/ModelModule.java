package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.matsim.core.controler.AbstractModule;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.TourFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.filters.CompositeTourFilter;
import ch.ethz.matsim.discrete_mode_choice.model.filters.CompositeTripFilter;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.DefaultModeChainGenerator;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.ModeChainGeneratorFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourFilter;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TripFilter;
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

		bind(ModeChainGeneratorFactory.class).to(DefaultModeChainGenerator.Factory.class);
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
	public TourBasedModel provideTourBasedModel(ModeAvailability modeAvailability, TourFilter tourFilter,
			TourEstimator tourEstimator, TourConstraintFactory tourConstraintFactory, TourFinder tourFinder,
			UtilitySelectorFactory<TourCandidate> selectorFactory, ModeChainGeneratorFactory modeChainGeneratorFactory,
			DiscreteModeChoiceConfigGroup dmcConfig) {
		return new TourBasedModel(tourEstimator, modeAvailability, tourConstraintFactory, tourFinder, tourFilter,
				selectorFactory, modeChainGeneratorFactory, dmcConfig.getFallbackBehaviour());
	}

	@Provides
	public TripBasedModel provideTripBasedModel(TripEstimator estimator, TripFilter tripFilter,
			ModeAvailability modeAvailability, TripConstraintFactory constraintFactory,
			UtilitySelectorFactory<TripCandidate> selectorFactory, DiscreteModeChoiceConfigGroup dmcConfig) {
		return new TripBasedModel(estimator, tripFilter, modeAvailability, constraintFactory, selectorFactory,
				dmcConfig.getFallbackBehaviour());
	}

	@Provides
	@Singleton
	public DefaultModeChainGenerator.Factory provideDefaultModeChainGeneratorFactory() {
		return new DefaultModeChainGenerator.Factory();
	}

	@Provides
	public TripFilter provideTripFilter(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<TripFilter>> providers) {
		Collection<String> names = dmcConfig.getTripFilters();
		Collection<TripFilter> filters = new ArrayList<>(names.size());

		for (String name : names) {
			if (!providers.containsKey(name)) {
				throw new IllegalStateException(String.format("TripFilter '%s' does not exist.", name));
			} else {
				filters.add(providers.get(name).get());
			}
		}

		return new CompositeTripFilter(filters);
	}

	@Provides
	public TourFilter provideTourFilter(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<TourFilter>> providers) {
		Collection<String> names = dmcConfig.getTourFilters();
		Collection<TourFilter> filters = new ArrayList<>(names.size());

		for (String name : names) {
			if (!providers.containsKey(name)) {
				throw new IllegalStateException(String.format("TourFilter '%s' does not exist.", name));
			} else {
				filters.add(providers.get(name).get());
			}
		}

		return new CompositeTourFilter(filters);
	}
}
