package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.matsim.core.controler.AbstractModule;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.CarModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.DefaultModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ModeAvailabilityConfigGroup;

public class ModeAvailabilityModule extends AbstractModule {
	static public LinkedBindingBuilder<ModeAvailability> bindModeAvailability(Binder binder, String name) {
		return MapBinder.newMapBinder(binder, String.class, ModeAvailability.class).addBinding(name);
	}

	public static final String DEFAULT = "Default";
	public static final String CAR = "Car";

	public static Collection<String> MODE_AVAILABILITIES = Arrays.asList(DEFAULT, CAR);

	@Override
	public void install() {
		bindModeAvailability(binder(), DEFAULT).to(DefaultModeAvailability.class);
		bindModeAvailability(binder(), CAR).to(CarModeAvailability.class);
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
		Provider<ModeAvailability> provider = components.get(dmcConfig.getModeAvailabilityComponent());

		if (provider != null) {
			return provider.get();
		} else {
			throw new IllegalStateException(String.format("There is no ModeAvailability component called '%s',",
					dmcConfig.getModeAvailabilityComponent()));
		}
	}
}
