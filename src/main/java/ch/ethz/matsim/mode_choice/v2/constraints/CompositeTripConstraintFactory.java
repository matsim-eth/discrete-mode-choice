package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraintFactory;

public class CompositeTripConstraintFactory implements TripConstraintFactory {
	final private List<TripConstraintFactory> factories = new LinkedList<>();

	public CompositeTripConstraintFactory() {
	}

	public CompositeTripConstraintFactory(List<TripConstraintFactory> factories) {
		this.factories.addAll(factories);
	}

	public void addFactory(TripConstraintFactory factory) {
		this.factories.add(factory);
	}

	@Override
	public TripConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes) {
		List<TripConstraint> constraints = new ArrayList<>(factories.size());
		factories.forEach(f -> constraints.add(f.createConstraint(trips, availableModes)));
		return new CompositeTripConstraint(constraints);
	}
}
