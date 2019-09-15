package ch.ethz.matsim.discrete_mode_choice.experiment.travel_time;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.groups.TravelTimeCalculatorConfigGroup;
import org.matsim.core.controler.AbstractModule;

import com.google.inject.Provides;
import com.google.inject.Singleton;

public class SmoothingTravelTimeModule extends AbstractModule {
	private final double alpha;

	public SmoothingTravelTimeModule(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public void install() {
		addEventHandlerBinding().to(SmoothingTravelTime.class);
		addControlerListenerBinding().to(SmoothingTravelTime.class);
		addTravelTimeBinding("car").to(SmoothingTravelTime.class);
	}

	@Provides
	@Singleton
	public SmoothingTravelTime provideSmoothingTravelTime(Network network, TravelTimeCalculatorConfigGroup config) {
		double startTime = 0.0;
		double endTime = config.getMaxTime();
		double interval = config.getTraveltimeBinSize();

		return new SmoothingTravelTime(startTime, endTime, interval, alpha, network);
	}
}
