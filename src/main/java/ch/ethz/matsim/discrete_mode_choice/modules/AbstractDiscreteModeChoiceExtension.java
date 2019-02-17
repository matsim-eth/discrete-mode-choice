package ch.ethz.matsim.discrete_mode_choice.modules;

import org.matsim.core.controler.AbstractModule;

import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.TourFinder;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;

/**
 * Base class for extensions to Discrete Mode Choice. It provides some helper
 * methods to easily bind new estimators, constraints, and more.
 * 
 * @author sebhoerl
 *
 */
public abstract class AbstractDiscreteModeChoiceExtension extends AbstractModule {
	protected MapBinder<String, TourEstimator> tourEstimatorBinder;
	protected MapBinder<String, TripEstimator> tripEstimatorBinder;

	protected MapBinder<String, TourConstraintFactory> tourConstraintFactoryBinder;
	protected MapBinder<String, TripConstraintFactory> tripConstraintFactoryBinder;

	protected MapBinder<String, UtilitySelectorFactory<TourCandidate>> tourSelectorFactory;
	protected MapBinder<String, UtilitySelectorFactory<TripCandidate>> tripSelectorFactory;

	protected MapBinder<String, ModeAvailability> modeAvailabilityBinder;
	protected MapBinder<String, TourFinder> tourFinderBinder;

	@Override
	public final void install() {
		tourEstimatorBinder = MapBinder.newMapBinder(binder(), String.class, TourEstimator.class);
		tripEstimatorBinder = MapBinder.newMapBinder(binder(), String.class, TripEstimator.class);

		tourConstraintFactoryBinder = MapBinder.newMapBinder(binder(), String.class, TourConstraintFactory.class);
		tripConstraintFactoryBinder = MapBinder.newMapBinder(binder(), String.class, TripConstraintFactory.class);

		tourSelectorFactory = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {
		}, new TypeLiteral<UtilitySelectorFactory<TourCandidate>>() {
		});
		tripSelectorFactory = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {
		}, new TypeLiteral<UtilitySelectorFactory<TripCandidate>>() {
		});

		modeAvailabilityBinder = MapBinder.newMapBinder(binder(), String.class, ModeAvailability.class);
		tourFinderBinder = MapBinder.newMapBinder(binder(), String.class, TourFinder.class);

		installExtension();
	}

	protected final LinkedBindingBuilder<TourEstimator> bindTourEstimator(String name) {
		return tourEstimatorBinder.addBinding(name);
	}

	protected final LinkedBindingBuilder<TripEstimator> bindTripEstimator(String name) {
		return tripEstimatorBinder.addBinding(name);
	}

	protected final LinkedBindingBuilder<TourConstraintFactory> bindTourConstraintFactory(String name) {
		return tourConstraintFactoryBinder.addBinding(name);
	}

	protected final LinkedBindingBuilder<TripConstraintFactory> bindTripConstraintFactory(String name) {
		return tripConstraintFactoryBinder.addBinding(name);
	}

	protected final LinkedBindingBuilder<UtilitySelectorFactory<TourCandidate>> bindTourSelectorFactory(String name) {
		return tourSelectorFactory.addBinding(name);
	}

	protected final LinkedBindingBuilder<UtilitySelectorFactory<TripCandidate>> bindTripSelectorFactory(String name) {
		return tripSelectorFactory.addBinding(name);
	}

	protected final LinkedBindingBuilder<ModeAvailability> bindModeAvailability(String name) {
		return modeAvailabilityBinder.addBinding(name);
	}

	protected final LinkedBindingBuilder<TourFinder> bindTourFinder(String name) {
		return tourFinderBinder.addBinding(name);
	}

	abstract protected void installExtension();
}
