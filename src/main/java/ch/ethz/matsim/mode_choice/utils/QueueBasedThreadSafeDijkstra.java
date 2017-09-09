package ch.ethz.matsim.mode_choice.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.Vehicle;

public class QueueBasedThreadSafeDijkstra implements LeastCostPathCalculator {
	final private ExecutorService executor;
	final private BlockingQueue<Dijkstra> instanceQueue = new LinkedBlockingQueue<>();

	public QueueBasedThreadSafeDijkstra(int numberOfInstances, Network network, TravelDisutility travelDisutility,
			TravelTime travelTime) {
		executor = Executors.newFixedThreadPool(numberOfInstances);

		try {
			for (int i = 0; i < numberOfInstances; i++) {
				instanceQueue.put(new Dijkstra(network, travelDisutility, travelTime));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		executor.shutdownNow();
	}

	@Override
	public Path calcLeastCostPath(Node fromNode, Node toNode, double starttime, Person person, Vehicle vehicle) {
		if (executor.isShutdown()) {
			throw new IllegalStateException();
		}
		
		try {
			return executor.submit(new Callable<Path>() {
				@Override
				public Path call() throws Exception {
					Dijkstra instance = instanceQueue.take();
					Path result = instance.calcLeastCostPath(fromNode, toNode, starttime, person, vehicle);
					instanceQueue.put(instance);
					return result;
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
