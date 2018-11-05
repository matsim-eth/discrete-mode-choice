package ch.ethz.matsim.mode_choice.constraints;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.framework.tour_based.constraints.TourConstraint;
import ch.ethz.matsim.mode_choice.framework.tour_based.constraints.TourConstraintFactory;

public class CompositeTourConstraintFactory implements TourConstraintFactory {
	final private List<TourConstraintFactory> factories = new LinkedList<>();

	public CompositeTourConstraintFactory() {
	}

	public CompositeTourConstraintFactory(List<TourConstraintFactory> factories) {
		this.factories.addAll(factories);
	}

	public void addFactory(TourConstraintFactory factory) {
		this.factories.add(factory);
	}

	@Override
	public TourConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes) {
		return new CompositeTourConstraint(
				factories.stream().map(f -> f.createConstraint(trips, availableModes)).collect(Collectors.toList()));
	}
}
