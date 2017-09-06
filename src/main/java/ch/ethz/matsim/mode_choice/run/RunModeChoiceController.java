package ch.ethz.matsim.mode_choice.run;

import java.util.Arrays;
import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.alternatives.ChainAlternatives;
import ch.ethz.matsim.mode_choice.alternatives.TripChainAlternatives;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceMNL;
import ch.ethz.matsim.mode_choice.mnl.prediction.CrowflyDistancePredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.FixedSpeedPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.NetworkPathPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.mode_choice.utils.QueueBasedThreadSafeDijkstra;

public class RunModeChoiceController {

	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig(args[0]);
		config.strategy().setMaxAgentPlanMemorySize(1);
		
		List<String> listArgs = Arrays.asList(args);
		
		final boolean useBestResponse = listArgs.contains("best-response");
		final boolean useShortestPath = listArgs.contains("shortest-path");

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);

		new RemoveLongPlans(10).run(scenario.getPopulation());

		// Set up MNL

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
			}

			@Singleton
			@Provides
			public ModeChoiceModel provideModeChoiceModel(Network network, @Named("car") TravelTime travelTime,
					GlobalConfigGroup config) {
				ChainAlternatives chainAlternatives = new TripChainAlternatives();
				ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom(), chainAlternatives,
						scenario.getNetwork(), useBestResponse ? ModeChoiceMNL.Mode.BEST_RESPONSE : ModeChoiceMNL.Mode.SAMPLING);

				BasicModeChoiceParameters carParameters = new BasicModeChoiceParameters(0.0, -0.176 / 1000.0,
						-23.29 / 3600.0, true);
				BasicModeChoiceParameters ptParameters = new BasicModeChoiceParameters(0.0, -0.25 / 1000.0,
						-14.43 / 3600.0, false);
				BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(0.0, 0.0, -33.2 / 3600.0,
						false);

				TripPredictor carPredictor = useShortestPath ? new NetworkPathPredictor(
						new QueueBasedThreadSafeDijkstra(config.getNumberOfThreads(), network,
								new OnlyTimeDependentTravelDisutility(travelTime), travelTime))
						: new FixedSpeedPredictor(30.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor());

				model.addModeAlternative("car", new BasicModeChoiceAlternative(carParameters, carPredictor));
				model.addModeAlternative("pt", new BasicModeChoiceAlternative(ptParameters,
						new FixedSpeedPredictor(12.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor())));
				model.addModeAlternative("walk", new BasicModeChoiceAlternative(walkParameters,
						new FixedSpeedPredictor(8.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor())));

				return model;
			}
		});

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.bindPlanSelectorForRemoval().to(OldPlanForRemovalSelector.class);
				this.addPlanStrategyBinding("ModeChoiceStrategy").toProvider(ModeChoiceStrategy.class);
			}
		});

		controler.run();
	}
}
