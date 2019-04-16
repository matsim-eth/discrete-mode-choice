package ch.ethz.matsim.discrete_mode_choice.run;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;
import org.matsim.analysis.LegHistogram;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.config.groups.SubtourModeChoiceConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import com.google.inject.Inject;

import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

public class RunIT {

	public static Node createNode(Network network, Id<Node> id, Coord coord) {
		return NetworkUtils.createAndAddNode(network, id, coord);
	}

	public static Link createLink(Network network, Id<Link> id, Node fromNode, Node toNode, double length) {
		return NetworkUtils.createAndAddLink(network, id, fromNode, toNode, length, 20.0, 1000, 1);
	}

	public static Config createConfig(boolean considerCarAvailability) {
		Config config = ConfigUtils.createConfig();
		SubtourModeChoiceConfigGroup smcConfig = config.subtourModeChoice();
		smcConfig.setConsiderCarAvailability(considerCarAvailability);
		StrategySettings stratSets = new StrategySettings();
		stratSets.setStrategyName("SubtourModeChoice");
		stratSets.setWeight(0.8);
		((StrategyConfigGroup) config.getModules().get("strategy")).addStrategySettings(stratSets);
		StrategySettings stratSets2 = new StrategySettings();
		stratSets2.setStrategyName("BestScore");
		stratSets2.setWeight(0.0);
		((StrategyConfigGroup) config.getModules().get("strategy")).addStrategySettings(stratSets2);

		config.controler().setLastIteration(5);

		addActivityTypeToScoringParameters(config, "home");
		addActivityTypeToScoringParameters(config, "work");

		return config;
	}

	public static void addActivityTypeToScoringParameters(Config config, String actType) {
		ActivityParams params = new ActivityParams(actType);
		params.setTypicalDuration(3600.0);
		((PlanCalcScoreConfigGroup) config.getModules().get("planCalcScore")).addActivityParams(params);
	}
	
	public static Scenario createScenario(Config config, String hasLicense, String carOwnership) {
	
		Scenario scenario = ScenarioUtils.loadScenario(config);

		Network network = scenario.getNetwork();
		// create nodes
		Node node1 = createNode(network, Id.createNodeId("1"), CoordUtils.createCoord(0.0, 0.0));
		Node node2 = createNode(network, Id.createNodeId("2"), CoordUtils.createCoord(0.0, 100.0));
		Node node3 = createNode(network, Id.createNodeId("3"), CoordUtils.createCoord(0.0, 200.0));

		// create links
		Link link1 = createLink(network, Id.createLinkId("12"), node1, node2, 100);
		Link link2 = createLink(network, Id.createLinkId("23"), node2, node3, 100);
		Link link3 = createLink(network, Id.createLinkId("32"), node3, node2, 100);
		Link link4 = createLink(network, Id.createLinkId("21"), node2, node1, 100);

		Population population = scenario.getPopulation();
		Person person = population.getFactory().createPerson(Id.createPersonId("1"));
		// create plan
		Plan plan = population.getFactory().createPlan();
		Activity act1 = PopulationUtils.createAndAddActivityFromCoord(plan, "home", link1.getCoord());
		act1.setEndTime(8.0 * 3600.0);
		PopulationUtils.createAndAddLeg(plan, "car");
		Activity act2 = PopulationUtils.createAndAddActivityFromCoord(plan, "work", link2.getCoord());
		act2.setEndTime(17.0 * 3600.0);
		PopulationUtils.createAndAddLeg(plan, "car");
		PopulationUtils.createAndAddActivityFromCoord(plan, "home", link1.getCoord());
		person.addPlan(plan);
		person.setSelectedPlan(plan);
		person.getAttributes().putAttribute("hasLicense", hasLicense);
		person.getAttributes().putAttribute("carAvail", carOwnership);
		population.addPerson(person);
		
		return scenario;
	}

	@Test
	public void testIntegrationRandomSampling() {
		// create config
		Config config = createConfig(true);
		// create scenario
		Scenario scenario = createScenario(config, "yes", "always");
		
		Controler controller = new Controler(scenario);
		controller.getConfig().controler()
				.setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		DiscreteModeChoiceConfigurator.configureAsSubtourModeChoiceReplacement(config);
		((DiscreteModeChoiceConfigGroup) config.getModules().get("DiscreteModeChoice")).getCarModeAvailabilityConfig()
				.setAvailableModesAsString("car,walk,bike");
		controller.addOverridingModule(new DiscreteModeChoiceModule());
		controller.run();

	}

	@Test
	public void testIntegrationImportanceSampling() {
		// create config
		Config config = createConfig(true);
		// create scenario
		Scenario scenario = createScenario(config, "no", "always");

		Controler controller = new Controler(scenario);
		controller.getConfig().controler()
				.setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		DiscreteModeChoiceConfigurator.configureAsImportanceSampler(config);
		((DiscreteModeChoiceConfigGroup) config.getModules().get("DiscreteModeChoice")).getCarModeAvailabilityConfig()
				.setAvailableModesAsString("car,walk,bike");
		final AnalysisNoCar myAnalysis = new AnalysisNoCar();
		controller.addOverridingModule( new AbstractModule(){
			@Override public void install() {
				this.bind(AnalysisNoCar.class).toInstance( myAnalysis ) ;
				this.addControlerListenerBinding().toInstance( myAnalysis ) ;
			}
		});
		controller.addOverridingModule(new DiscreteModeChoiceModule());
		controller.run();

	}
	
	@Test
	public void testMNL() {
		
		// create config
		Config config = createConfig(true);
		// create scenario
		Scenario scenario = createScenario(config, "yes", "always");

		Controler controller = new Controler(scenario);
		controller.getConfig().controler()
				.setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		DiscreteModeChoiceConfigurator.configureAsModeChoiceInTheLoop(config, 1.0);
		((DiscreteModeChoiceConfigGroup) config.getModules().get("DiscreteModeChoice")).getCarModeAvailabilityConfig()
				.setAvailableModesAsString("car,walk,bike");

		//TODO: check that there are only two strategies
		controller.addOverridingModule(new DiscreteModeChoiceModule());
		controller.run();
	}

	static class AnalysisNoCar implements AfterMobsimListener {
		@Inject
		private LegHistogram histogram;

		void testOutput(int iteration) {

			for (String legMode : this.histogram.getLegModes()) {

				if (iteration != 0) {
					if (TransportMode.car.equals(legMode)) {
						Assert.shouldNeverReachHere("There should be no car legs after iteration 0");
					}
				}
			}
		}
		@Override
		public void notifyAfterMobsim(AfterMobsimEvent event) {
			testOutput(event.getIteration());
		}
	}

}
