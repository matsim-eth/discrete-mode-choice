package ch.ethz.matsim.mode_choice.utils;

import java.util.List;

import javax.inject.Provider;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.facilities.Facility;
import org.matsim.pt.router.TransitRouter;

public class BlockingThreadSafeTransitRouter implements TransitRouter {
	final private TransitRouter delegate;

	public BlockingThreadSafeTransitRouter(Provider<TransitRouter> factory) {
		this.delegate = factory.get();
	}

	@Override
	public List<Leg> calcRoute(Facility<?> fromFacility, Facility<?> toFacility, double departureTime, Person person) {
		synchronized(delegate) {
			return delegate.calcRoute(fromFacility, toFacility, departureTime, person);
		}
	}
}
