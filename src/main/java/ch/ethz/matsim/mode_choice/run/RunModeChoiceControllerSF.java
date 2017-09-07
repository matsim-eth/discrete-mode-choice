package ch.ethz.matsim.mode_choice.run;

import java.util.Iterator;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelDisutility;
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
import ch.ethz.matsim.mode_choice.mnl.prediction.HashPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.NetworkPathPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCacheCleaner;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.run.MNLConfigGroup.MNLCarUtility;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.mode_choice.utils.BlockingThreadSafeDijkstra;
import ch.ethz.matsim.mode_choice.utils.QueueBasedThreadSafeDijkstra;
import ch.ethz.matsim.sioux_falls.SiouxFallsUtils;

public class RunModeChoiceControllerSF {

	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig(SiouxFallsUtils.getConfigURL());
		config.strategy().setMaxAgentPlanMemorySize(1);

		config.strategy().clearStrategySettings();

		StrategySettings settings = new StrategySettings();
		settings.setWeight(1.0);
		settings.setStrategyName("ModeChoiceStrategy");
		config.strategy().addStrategySettings(settings);

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);

		Iterator<? extends Person> personIterator = scenario.getPopulation().getPersons().values().iterator();

		while (personIterator.hasNext()) {
			personIterator.next();

			if (MatsimRandom.getRandom().nextDouble() < 0.99) {
				personIterator.remove();
			}
		}

		// Set up MNL
		
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addControlerListenerBinding().to(PredictionCacheCleaner.class);
			}
			
			@Singleton @Provides
			public PredictionCacheCleaner providePredictionCacheCleaner(PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}
			
			@Singleton @Provides
			public PredictionCache providePredictionCache() {
				return new HashPredictionCache();
			}
			
			@Singleton @Provides
			public ModeChoiceModel provideModeChoiceModel(Network network, @Named("car") TravelTime travelTime, GlobalConfigGroup config, PredictionCache cache) {
				ChainAlternatives chainAlternatives = new TripChainAlternatives();
				ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom(), chainAlternatives, scenario.getNetwork(), ModeChoiceMNL.Mode.BEST_RESPONSE);

				BasicModeChoiceParameters carParameters = new BasicModeChoiceParameters(0.0, -0.176 / 1000.0, -23.29 / 3600.0, true);
				BasicModeChoiceParameters ptParameters = new BasicModeChoiceParameters(0.0, -0.25 / 1000.0, -14.43 / 3600.0, false);
				BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(0.0, 0.0, -33.2 / 3600.0, false);
				
				TripPredictor carPredictor = new NetworkPathPredictor(new QueueBasedThreadSafeDijkstra(config.getNumberOfThreads(), network, new OnlyTimeDependentTravelDisutility(travelTime), travelTime));
				
				model.addModeAlternative("car", new BasicModeChoiceAlternative(carParameters, carPredictor, cache));
				model.addModeAlternative("pt", new BasicModeChoiceAlternative(ptParameters, new FixedSpeedPredictor(12.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor())));
				model.addModeAlternative("walk", new BasicModeChoiceAlternative(walkParameters, new FixedSpeedPredictor(8.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor())));
				
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
