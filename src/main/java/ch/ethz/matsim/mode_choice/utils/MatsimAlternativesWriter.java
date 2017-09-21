package ch.ethz.matsim.mode_choice.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.algorithms.ChooseRandomLegModeForSubtour;
import org.matsim.core.population.algorithms.PermissibleModesCalculatorImpl;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.Counter;
import org.matsim.pt.PtConstants;

public class MatsimAlternativesWriter {
	private static List<String> getModeChain(Plan plan) {
		return TripStructureUtils
				.getTrips(plan.getPlanElements(), new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE))
				.stream().map(t -> t.getLegsOnly().iterator().next().getMode()).collect(Collectors.toList());
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		final BufferedWriter outLink = IOUtils.getBufferedWriter(args[1]);

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		new PopulationReader(scenario).readFile(args[0]);

		String[] availableModes = new String[] { "car", "pt", "bike", "walk" };
		String[] chainBasedModes = new String[] { "car", "bike" };
		//String[] nonChainBasedModes = new String[] { "pt", "walk" };

		Random random = new Random(0);

		final ChooseRandomLegModeForSubtour algorithm = new ChooseRandomLegModeForSubtour(
				new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE), new MainModeIdentifierImpl(),
				new PermissibleModesCalculatorImpl(availableModes, false), availableModes, chainBasedModes, random);
		
		final Counter counter = new Counter("", " persons");
		
		ExecutorService executor = Executors.newFixedThreadPool(80);
		
		List<Future<String>> futures = new LinkedList<>();
		
		for (Person person : scenario.getPopulation().getPersons().values()) {	
			
			Set<String> matsimChains = new HashSet<>();

			Plan plan = person.getSelectedPlan();
			
			TripStructureUtils.getLegs(plan).forEach(leg -> leg.setMode("walk"));
			
			if (!((Activity) plan.getPlanElements().get(0)).getType().contains("home")) {
				throw new IllegalStateException(((Activity) plan.getPlanElements().get(0)).getType());
			}
			
			if (plan.getPlanElements().size() > 1) {
				futures.add(executor.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						for (int i = 0; i < 100000; i++) {
							algorithm.run(plan);					
							matsimChains.add(String.join(",", getModeChain(plan)));
						}	
						
						synchronized(counter) {
							counter.incCounter();
						}
						
						return person.getId().toString() + ";" + String.join(";", matsimChains);
					}
				}));
			}
		}
		
		for (Future<String> future : futures) {
			String result = future.get();
			outLink.write(result);
			outLink.newLine();
		}
		
		outLink.flush();
		outLink.close();
		
		executor.shutdown();
		
	}

}
