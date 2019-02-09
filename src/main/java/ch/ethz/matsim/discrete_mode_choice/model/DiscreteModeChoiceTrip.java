package ch.ethz.matsim.discrete_mode_choice.model;

import org.matsim.api.core.v01.population.Activity;

public final class DiscreteModeChoiceTrip {
	private final Activity originActivity;
	private final Activity destinationActivity;
	private final String initialMode;
	private final double departureTime;

	private final int hashCode;

	public DiscreteModeChoiceTrip(Activity originActivity, Activity destinationActivity, String initialMode,
			double departureTime, int personIndex, int tripIndex) {
		this.originActivity = originActivity;
		this.destinationActivity = destinationActivity;
		this.initialMode = initialMode;
		this.departureTime = departureTime;

		int hashCode = 12;
		hashCode += 37 * (int) (personIndex ^ (personIndex >>> 32));
		hashCode += 37 * (int) (tripIndex ^ (tripIndex >>> 32));
		this.hashCode = hashCode;

	}

	public DiscreteModeChoiceTrip(Activity originActivity, Activity destinationActivity, String initialMode,
			double departureTime) {
		this(originActivity, destinationActivity, initialMode, departureTime, initialMode.hashCode(),
				(int) departureTime);
	}

	public Activity getOriginActivity() {
		return originActivity;
	}

	public Activity getDestinationActivity() {
		return destinationActivity;
	}

	public double getDepartureTime() {
		return departureTime;
	}

	public String getInitialMode() {
		return initialMode;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
