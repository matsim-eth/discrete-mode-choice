package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.MaximumSelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.MultinomialLogitSelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.RandomSelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.MultinomialLogitSelectorConfigGroup;

public class SelectorModule extends AbstractDiscreteModeChoiceExtension {
	public final static String MAXIMUM = "Maximum";
	public final static String MULTINOMIAL_LOGIT = "MultinomialLogit";
	public final static String RANDOM = "Random";

	public final static Collection<String> COMPONENTS = Arrays.asList(MAXIMUM, MULTINOMIAL_LOGIT, RANDOM);

	@Override
	public void installExtension() {
		bindTripSelectorFactory(MAXIMUM).to(new TypeLiteral<MaximumSelector.Factory<TripCandidate>>() {
		});
		bindTourSelectorFactory(MAXIMUM).to(new TypeLiteral<MaximumSelector.Factory<TourCandidate>>() {
		});

		bindTripSelectorFactory(MULTINOMIAL_LOGIT)
				.to(new TypeLiteral<MultinomialLogitSelector.Factory<TripCandidate>>() {
				});
		bindTourSelectorFactory(MULTINOMIAL_LOGIT)
				.to(new TypeLiteral<MultinomialLogitSelector.Factory<TourCandidate>>() {
				});

		bindTripSelectorFactory(RANDOM).to(new TypeLiteral<RandomSelector.Factory<TripCandidate>>() {
		});
		bindTourSelectorFactory(RANDOM).to(new TypeLiteral<RandomSelector.Factory<TourCandidate>>() {
		});
	}

	@Provides
	public UtilitySelectorFactory<TourCandidate> provideTourSelectorFactory(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<UtilitySelectorFactory<TourCandidate>>> components) {
		Provider<UtilitySelectorFactory<TourCandidate>> provider = components.get(dmcConfig.getSelector());

		if (provider != null) {
			return provider.get();
		} else {
			throw new IllegalStateException(String
					.format("There is no UtilitySelector component for tours called '%s',", dmcConfig.getSelector()));
		}
	}

	@Provides
	public UtilitySelectorFactory<TripCandidate> provideTripSelectorFactory(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<UtilitySelectorFactory<TripCandidate>>> components) {
		Provider<UtilitySelectorFactory<TripCandidate>> provider = components.get(dmcConfig.getSelector());

		if (provider != null) {
			return provider.get();
		} else {
			throw new IllegalStateException(String
					.format("There is no UtilitySelector component for trips called '%s',", dmcConfig.getSelector()));
		}
	}

	@Provides
	@Singleton
	public MaximumSelector.Factory<TripCandidate> provideMaximumTripSelector() {
		return new MaximumSelector.Factory<>();
	}

	@Provides
	@Singleton
	public MaximumSelector.Factory<TourCandidate> provideMaximumTourSelector() {
		return new MaximumSelector.Factory<>();
	}

	@Provides
	@Singleton
	public MultinomialLogitSelector.Factory<TripCandidate> provideMultinomialLogitTripSelector(
			DiscreteModeChoiceConfigGroup dmcConfig) {
		MultinomialLogitSelectorConfigGroup config = dmcConfig.getMultinomialLogitSelectorConfig();
		return new MultinomialLogitSelector.Factory<>(config.getMinimumUtility(), config.getMaximumUtility(),
				config.getConsiderMinimumUtility());
	}

	@Provides
	@Singleton
	public MultinomialLogitSelector.Factory<TourCandidate> provideMultinomialLogitTourSelector(
			DiscreteModeChoiceConfigGroup dmcConfig) {
		MultinomialLogitSelectorConfigGroup config = dmcConfig.getMultinomialLogitSelectorConfig();
		return new MultinomialLogitSelector.Factory<>(config.getMinimumUtility(), config.getMaximumUtility(),
				config.getConsiderMinimumUtility());
	}

	@Provides
	@Singleton
	public RandomSelector.Factory<TripCandidate> provideRandomTripSelector() {
		return new RandomSelector.Factory<>();
	}

	@Provides
	@Singleton
	public RandomSelector.Factory<TourCandidate> provideRandomTourSelector() {
		return new RandomSelector.Factory<>();
	}
}
