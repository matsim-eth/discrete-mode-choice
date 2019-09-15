package ch.ethz.matsim.discrete_mode_choice.experiment;

import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.router.DijkstraFactory;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.discrete_mode_choice.experiment.router.BestNLeastCostPathCalculatorFactory;
import ch.ethz.matsim.discrete_mode_choice.experiment.router.BestNLeastCostPathSelector;
import ch.ethz.matsim.discrete_mode_choice.experiment.router.MultinomialPathSelector;
import ch.ethz.matsim.discrete_mode_choice.experiment.router.RandomPathSelector;
import ch.ethz.matsim.discrete_mode_choice.experiment.travel_time.AdjustedTravelTime;
import ch.ethz.matsim.discrete_mode_choice.experiment.travel_time.SmoothingTravelTimeModule;

public class RunOscillations {
	static public void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.WARN);

		// Set up configuration
		int numberOfIterations = 100;
		Config config = setupConfig(numberOfIterations);

		// Add strategies
		double keepLastSelectedWeight = 0.8;
		double rerouteWeight = 0.2;

		// Smooth travel time
		boolean useSmoothingTravelTime = true;
		double smoothingFactor = 0.3;

		StrategySettings keepLastSelectedStrategy = new StrategySettings();
		keepLastSelectedStrategy.setStrategyName("KeepLastSelected");
		keepLastSelectedStrategy.setWeight(keepLastSelectedWeight);
		config.strategy().addStrategySettings(keepLastSelectedStrategy);

		StrategySettings rerouteStrategy = new StrategySettings();
		rerouteStrategy.setStrategyName("ReRoute");
		rerouteStrategy.setWeight(rerouteWeight);
		config.strategy().addStrategySettings(rerouteStrategy);

		// Set up scenario
		int numberOfAgents = 500;
		double departureSigma = 0.0; // Diversify departure times (0 = None)

		double capacityA = 200.0;
		double capacityB = 200.0;

		Random random = new Random(0);
		Scenario scenario = createScenario(numberOfAgents, capacityA, capacityB, departureSigma, config, random);

		// Set up controller
		Controler controller = new Controler(scenario);
		RouteListener routeListener = new RouteListener();

		controller.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().toInstance(routeListener);
				addControlerListenerBinding().toInstance(routeListener);

				addTravelDisutilityFactoryBinding("car").toInstance(new OnlyTimeDependentTravelDisutilityFactory());

				if (useSmoothingTravelTime) {
					install(new SmoothingTravelTimeModule(smoothingFactor));
				} else {
					addTravelTimeBinding("car").to(AdjustedTravelTime.class);
				}
			}

			@Provides
			@Singleton
			public LeastCostPathCalculatorFactory provideLeastCostPathCalculatorFactory() {
				int maximumNumberOfAlternatives = 2;
				double maximumDelay = Double.POSITIVE_INFINITY;
				LeastCostPathCalculatorFactory delegateFactory = new DijkstraFactory();
				
				//BestNLeastCostPathSelector selector = new RandomPathSelector();
				
				double beta = 0.0;
				BestNLeastCostPathSelector selector = new MultinomialPathSelector(beta * 1.0 / 60.0);
				
				return new BestNLeastCostPathCalculatorFactory(delegateFactory, selector, maximumDelay,
						maximumNumberOfAlternatives, 1e6);
			}
		});

		controller.run();
	}

	static Config setupConfig(int numberOfIterations) {
		Config config = ConfigUtils.createConfig();

		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setLastIteration(numberOfIterations);

		config.qsim().setStuckTime(3600.0 * 24.0);
		config.qsim().setTrafficDynamics(TrafficDynamics.queue);

		ActivityParams activityParams = new ActivityParams("generic");
		activityParams.setTypicalDuration(1.0);
		activityParams.setScoringThisActivityAtAll(false);
		config.planCalcScore().addActivityParams(activityParams);

		ModeParams carParams = config.planCalcScore().getModes().get("car");
		carParams.setConstant(0.0);
		carParams.setMarginalUtilityOfDistance(0.0);
		carParams.setMonetaryDistanceRate(0.0);
		carParams.setMarginalUtilityOfTraveling(-1.0);

		return config;
	}

	static Scenario createScenario(int numberOfAgents, double capacityA, double capacityB, double departureSigma,
			Config config, Random random) {
		Scenario scenario = ScenarioUtils.createScenario(config);

		Network network = scenario.getNetwork();
		NetworkFactory networkFactory = network.getFactory();

		Node startFromNode = networkFactory.createNode(Id.createNodeId("startFrom"), new Coord(0.0, 10000.0));
		Node startToNode = networkFactory.createNode(Id.createNodeId("startTo"), new Coord(0.0, 0.0));

		Node endFromNode = networkFactory.createNode(Id.createNodeId("endFrom"), new Coord(10000.0, 0.0));
		Node endToNode = networkFactory.createNode(Id.createNodeId("endTo"), new Coord(10000.0, 10000.0));

		Link startLink = networkFactory.createLink(Id.createLinkId("start"), startFromNode, startToNode);
		Link endLink = networkFactory.createLink(Id.createLinkId("end"), endFromNode, endToNode);

		Link linkA = networkFactory.createLink(Id.createLinkId("A"), startToNode, endFromNode);
		Link linkB = networkFactory.createLink(Id.createLinkId("B"), startToNode, endFromNode);

		startLink.setCapacity(1000.0);
		endLink.setCapacity(1000.0);
		startLink.setFreespeed(10000.0);
		endLink.setFreespeed(10000.0);

		linkA.setCapacity(capacityA);
		linkB.setCapacity(capacityB);

		linkA.setFreespeed(10.0);
		linkB.setFreespeed(10.0);

		network.addNode(startFromNode);
		network.addNode(startToNode);
		network.addNode(endFromNode);
		network.addNode(endToNode);

		network.addLink(startLink);
		network.addLink(endLink);
		network.addLink(linkA);
		network.addLink(linkB);

		Population population = scenario.getPopulation();
		PopulationFactory populationFactory = population.getFactory();

		for (int i = 0; i < numberOfAgents; i++) {
			Person person = populationFactory.createPerson(Id.createPersonId(i));
			population.addPerson(person);

			Plan plan = populationFactory.createPlan();
			person.addPlan(plan);

			Activity startActivity = populationFactory.createActivityFromLinkId("generic", Id.createLinkId("start"));
			startActivity.setEndTime(Math.max(0.0, random.nextGaussian() * departureSigma));
			plan.addActivity(startActivity);

			Leg leg = populationFactory.createLeg("car");
			plan.addLeg(leg);

			Activity endActivity = populationFactory.createActivityFromLinkId("generic", Id.createLinkId("end"));
			plan.addActivity(endActivity);
		}

		return scenario;
	}
}
