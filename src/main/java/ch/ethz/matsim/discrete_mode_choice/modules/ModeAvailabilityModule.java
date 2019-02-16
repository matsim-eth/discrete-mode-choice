package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.google.inject.Provider;
import com.google.inject.Provides;

import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.CarModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.DefaultModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ModeAvailabilityConfigGroup;

public class ModeAvailabilityModule extends AbstractDiscreteModeChoiceExtension {
	public static final String DEFAULT = "Default";
	public static final String CAR = "Car";

	public static final Collection<String> COMPONENTS = Arrays.asList(DEFAULT, CAR);

	@Override
	public void installExtension() {
		bindModeAvailability(DEFAULT).to(DefaultModeAvailability.class);
		bindModeAvailability(CAR).to(CarModeAvailability.class);
	}

	@Provides
	public DefaultModeAvailability provideDefaultModeAvailability(DiscreteModeChoiceConfigGroup dmcConfig) {
		ModeAvailabilityConfigGroup config = dmcConfig.getDefaultModeAvailabilityConfig();
		return new DefaultModeAvailability(config.getAvailableModes());
	}

	@Provides
	public CarModeAvailability provideCarModeAvailability(DiscreteModeChoiceConfigGroup dmcConfig) {
		ModeAvailabilityConfigGroup config = dmcConfig.getCarModeAvailabilityConfig();
		return new CarModeAvailability(config.getAvailableModes());
	}

	@Provides
	public ModeAvailability provideModeAvailability(DiscreteModeChoiceConfigGroup dmcConfig,
			Map<String, Provider<ModeAvailability>> components) {
		Provider<ModeAvailability> provider = components.get(dmcConfig.getModeAvailability());

		if (provider != null) {
			return provider.get();
		} else {
			throw new IllegalStateException(String.format("There is no ModeAvailability component called '%s',",
					dmcConfig.getModeAvailability()));
		}
	}
}
