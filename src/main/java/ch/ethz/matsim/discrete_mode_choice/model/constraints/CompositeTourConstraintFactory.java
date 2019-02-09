package ch.ethz.matsim.discrete_mode_choice.model.constraints;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraint;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;

/**
 * Creates a CompositeTourConstraint.
 * 
 * @author sebhoerl
 */
public class CompositeTourConstraintFactory implements TourConstraintFactory {
	private final List<TourConstraintFactory> factories = new LinkedList<>();

	public CompositeTourConstraintFactory() {
	}

	public CompositeTourConstraintFactory(List<TourConstraintFactory> factories) {
		this.factories.addAll(factories);
	}

	public void addFactory(TourConstraintFactory factory) {
		this.factories.add(factory);
	}

	@Override
	public TourConstraint createConstraint(Person person, List<DiscreteModeChoiceTrip> trips,
			Collection<String> availableModes) {
		return new CompositeTourConstraint(factories.stream()
				.map(f -> f.createConstraint(person, trips, availableModes)).collect(Collectors.toList()));
	}
}
