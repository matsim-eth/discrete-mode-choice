package ch.ethz.matsim.mode_choice.run;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Scenario;
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
import org.matsim.pt.PtConstants;

import ch.ethz.matsim.mode_choice.alternatives.TripChainAlternatives;

public class CompareSubtourModeChoiceSet {
	private static List<String> getModeChain(Plan plan) {
		return TripStructureUtils
				.getTrips(plan.getPlanElements(), new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE))
				.stream().map(t -> t.getLegsOnly().iterator().next().getMode()).collect(Collectors.toList());
	}

	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		new PopulationReader(scenario).readFile(args[0]);

		String[] availableModes = new String[] { "car", "pt", "bike", "walk" };
		String[] chainBasedModes = new String[] { "car", "bike" };
		String[] nonChainBasedModes = new String[] { "pt", "walk" };

		Random random = new Random(0);

		final ChooseRandomLegModeForSubtour algorithm = new ChooseRandomLegModeForSubtour(
				new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE), new MainModeIdentifierImpl(),
				new PermissibleModesCalculatorImpl(availableModes, false), availableModes, chainBasedModes, random);

		TripChainAlternatives tripChainAlternatives = new TripChainAlternatives(false);

		for (Person person : scenario.getPopulation().getPersons().values()) {
			Set<String> matsimChains = new HashSet<>();
			Set<String> balacChains = new HashSet<>();

			Plan plan = person.getSelectedPlan();
			
			if (plan.getPlanElements().size() > 1) {
				for (int i = 0; i < 100000; i++) {
					algorithm.run(plan);
					matsimChains.add(String.join(":", getModeChain(plan)));
				}

				for (List<String> alternative : tripChainAlternatives.getTripChainAlternatives(plan,
						Arrays.asList(chainBasedModes), Arrays.asList(nonChainBasedModes))) {
					balacChains.add(String.join(":", alternative));
				}
				
				System.out.println(String.format("%s : %d : %d", person.getId().toString(), matsimChains.size(),
						balacChains.size()));
			}
		}
	}
}
