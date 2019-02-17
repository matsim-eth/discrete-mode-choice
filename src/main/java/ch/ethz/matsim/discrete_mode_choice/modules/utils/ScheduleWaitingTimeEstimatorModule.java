package ch.ethz.matsim.discrete_mode_choice.modules.utils;

import org.matsim.pt.transitSchedule.api.TransitSchedule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.discrete_mode_choice.components.utils.PTWaitingTimeEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.utils.ScheduleWaitingTimeEstimator;

/**
 * Module that is added if a TransitSchedule is available. It binds a
 * PTWaitingTimeEstimator that is able to estimate a waiting time from the
 * schedule.
 * 
 * @author sebhoerl
 *
 */
public class ScheduleWaitingTimeEstimatorModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(PTWaitingTimeEstimator.class).to(ScheduleWaitingTimeEstimator.class);
	}

	@Provides
	@Singleton
	public ScheduleWaitingTimeEstimator provideScheduleWaitingTimeEstimator(TransitSchedule transitSchedule) {
		return new ScheduleWaitingTimeEstimator(transitSchedule);
	}
}
