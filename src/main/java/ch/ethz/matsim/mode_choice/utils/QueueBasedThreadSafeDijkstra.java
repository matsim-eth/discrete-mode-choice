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
import org.matsim.core.router.DijkstraFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.Vehicle;

public class QueueBasedThreadSafeDijkstra implements ThreadSafeLeastCostPathCalculator {
	final private ExecutorService executor;
	final private BlockingQueue<Dijkstra> instanceQueue = new LinkedBlockingQueue<>();

	final private Network network;

	public QueueBasedThreadSafeDijkstra(int numberOfInstances, Network network, TravelDisutility travelDisutility,
			TravelTime travelTime) {
		this.network = network;
		executor = Executors.newFixedThreadPool(numberOfInstances);

		try {
			for (int i = 0; i < numberOfInstances; i++) {
				DijkstraFactory factory = new DijkstraFactory();
				instanceQueue.put((Dijkstra) factory.createPathCalculator(network, travelDisutility, travelTime));
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

		boolean fromNodeIdNotFound = !network.getNodes().containsKey(fromNode.getId());
		boolean toNodeIdNotFound = !network.getNodes().containsKey(toNode.getId());

		if (fromNodeIdNotFound || toNodeIdNotFound) {
			throw new IllegalStateException();
		}

		try {
			return executor.submit(new Callable<Path>() {
				@Override
				public Path call() throws Exception {
					Dijkstra instance = instanceQueue.take();

					// Recover the nodes here, because they may be different from the network that
					// is used for routing (full network vs. road network)
					Path result = instance.calcLeastCostPath(network.getNodes().get(fromNode.getId()),
							network.getNodes().get(toNode.getId()), starttime, person, vehicle);

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
