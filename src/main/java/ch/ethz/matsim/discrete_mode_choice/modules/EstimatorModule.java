package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.router.TripRouter;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

import ch.ethz.matsim.discrete_mode_choice.components.estimators.CumulativeTourEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.estimators.MATSimDayScoringEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.estimators.MATSimTripScoringEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.estimators.NullTourEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.estimators.NullTripEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.utils.PublicTransitWaitingTimeEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

public class EstimatorModule extends AbstractModule {
	static public LinkedBindingBuilder<TourEstimator> bindTourEstimator(Binder binder, String name) {
		return MapBinder.newMapBinder(binder, String.class, TourEstimator.class).addBinding(name);
	}

	static public LinkedBindingBuilder<TripEstimator> bindTripEstimator(Binder binder, String name) {
		return MapBinder.newMapBinder(binder, String.class, TripEstimator.class).addBinding(name);
	}

	public static final String MATSIM_TRIP_SCORING = "MATSimTripScoring";
	public static final String MATSIM_DAY_SCORING = "MATSimDayScoring";
	public static final String CUMULATIVE = "Cumulative";
	public static final String NULL = "Null";

	public static final Collection<String> ESTIMATORS = Arrays.asList(MATSIM_TRIP_SCORING, MATSIM_DAY_SCORING,
			CUMULATIVE, NULL);

	@Override
	public void install() {
		bindTripEstimator(binder(), MATSIM_TRIP_SCORING).to(MATSimTripScoringEstimator.class);
		bindTripEstimator(binder(), NULL).to(NullTripEstimator.class);

		bindTourEstimator(binder(), MATSIM_DAY_SCORING).to(MATSimDayScoringEstimator.class);
		bindTourEstimator(binder(), CUMULATIVE).to(CumulativeTourEstimator.class);
		bindTourEstimator(binder(), NULL).to(NullTourEstimator.class);
	}

	@Provides
	public TourEstimator provideTourEstimator(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<TourEstimator>> components) {
		Provider<TourEstimator> provider = components.get(dmcConfig.getTourEstimatorComponent());

		if (provider != null) {
			return provider.get();
		} else {
			throw new IllegalStateException(String.format("There is no TourEstimator component called '%s',",
					dmcConfig.getModeAvailabilityComponent()));
		}
	}

	@Provides
	public TripEstimator provideTripEstimator(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<TripEstimator>> components) {
		Provider<TripEstimator> provider = components.get(dmcConfig.getTripEstimatorComponent());

		if (provider != null) {
			return provider.get();
		} else {
			throw new IllegalStateException(String.format("There is no TripEstimator component called '%s',",
					dmcConfig.getModeAvailabilityComponent()));
		}
	}

	@Provides
	@Singleton
	public NullTripEstimator provideNullTripEstimator() {
		return new NullTripEstimator();
	}

	@Provides
	@Singleton
	public NullTourEstimator proideNullTourEstimator() {
		return new NullTourEstimator();
	}

	@Provides
	@Singleton
	public PublicTransitWaitingTimeEstimator provideWaitingTimeEstimator(TransitSchedule transitSchedule) {
		return new PublicTransitWaitingTimeEstimator(transitSchedule);
	}

	@Provides
	@Singleton
	public MATSimTripScoringEstimator provideMATSimTripScoringEstimator(Network network, ActivityFacilities facilities,
			TripRouter tripRouter, PublicTransitWaitingTimeEstimator waitingTimeEstimator,
			ScoringParametersForPerson scoringParametersForPerson) {
		return new MATSimTripScoringEstimator(network, facilities, tripRouter, waitingTimeEstimator,
				scoringParametersForPerson);
	}

	@Provides
	@Singleton
	public MATSimDayScoringEstimator provideMATSimDayScoringEstimator(MATSimTripScoringEstimator tripEstimator,
			ScoringParametersForPerson scoringParametersForPerson) {
		return new MATSimDayScoringEstimator(tripEstimator, scoringParametersForPerson);
	}

	@Provides
	public CumulativeTourEstimator provideCumulativeTourEstimator(TripEstimator tripEstimator) {
		return new CumulativeTourEstimator(tripEstimator);
	}
}
