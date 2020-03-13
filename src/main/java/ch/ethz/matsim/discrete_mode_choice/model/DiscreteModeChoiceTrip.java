package ch.ethz.matsim.discrete_mode_choice.model;

import java.util.List;

import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.PlanElement;

/**
 * This class represents an agent's trip. It contains structural information
 * such as the origin and destination activity and the mode that has initially
 * been used to cover the trip.
 * 
 * @author sebhoerl
 */
public final class DiscreteModeChoiceTrip {
	private final Activity originActivity;
	private final Activity destinationActivity;
	private final String initialMode;
	private double departureTime;
	private final List<? extends PlanElement> initialElements;

	private final int hashCode;

	public DiscreteModeChoiceTrip(Activity originActivity, Activity destinationActivity, String initialMode,
			List<? extends PlanElement> initialElements, double departureTime, int personHash, int tripHash) {
		this.originActivity = originActivity;
		this.destinationActivity = destinationActivity;
		this.initialMode = initialMode;
		this.departureTime = departureTime;
		this.initialElements = initialElements;

		int hashCode = 12;
		hashCode += 37 * (int) (personHash ^ (personHash >>> 32));
		hashCode += 37 * (int) (tripHash ^ (tripHash >>> 32));
		this.hashCode = hashCode;
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
	
	void setDepartureTime(double departureTime) {
		this.departureTime = departureTime;
	}

	public String getInitialMode() {
		return initialMode;
	}

	public List<? extends PlanElement> getInitialElements() {
		return initialElements;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
