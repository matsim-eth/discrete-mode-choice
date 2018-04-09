package ch.ethz.matsim.mode_choice.utils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Provider;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.facilities.Facility;
import org.matsim.pt.router.TransitRouter;

public class QueueBasedThreadSafeTransitRouter implements ThreadSafeTransitRouter {
	final private ExecutorService executor;
	final private BlockingQueue<TransitRouter> instanceQueue = new LinkedBlockingQueue<>();

	public QueueBasedThreadSafeTransitRouter(int numberOfInstances, Provider<TransitRouter> factory) {
		executor = Executors.newFixedThreadPool(numberOfInstances);

		try {
			for (int i = 0; i < numberOfInstances; i++) {
				instanceQueue.put(factory.get());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Leg> calcRoute(Facility<?> fromFacility, Facility<?> toFacility, double departureTime, Person person) {
		try {
			return executor.submit(new Callable<List<Leg>>() {
				@Override
				public List<Leg> call() throws Exception {
					TransitRouter instance = instanceQueue.take();
					List<Leg> result = instance.calcRoute(fromFacility, toFacility, departureTime, person);
					instanceQueue.put(instance);
					return result;
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public void close() {
		this.executor.shutdownNow();
	}
}
