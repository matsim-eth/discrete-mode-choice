package ch.ethz.matsim.mode_choice.run;

import java.util.Iterator;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.config.TransitConfigGroup;
import org.matsim.pt.config.TransitRouterConfigGroup;
import org.matsim.pt.router.TransitRouter;
import org.matsim.pt.router.TransitRouterImplFactory;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

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
import ch.ethz.matsim.mode_choice.mnl.prediction.PublicTransitPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.run.MNLConfigGroup.MNLCarUtility;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.mode_choice.utils.BlockingThreadSafeDijkstra;
import ch.ethz.matsim.mode_choice.utils.BlockingThreadSafeTransitRouter;
import ch.ethz.matsim.mode_choice.utils.QueueBasedThreadSafeDijkstra;
import ch.ethz.matsim.mode_choice.utils.QueueBasedThreadSafeTransitRouter;
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
				addControlerListenerBinding().to(Key.get(PredictionCacheCleaner.class, Names.named("car")));
				addControlerListenerBinding().to(Key.get(PredictionCacheCleaner.class, Names.named("pt")));
			}
			
			@Singleton @Provides @Named("car")
			public PredictionCacheCleaner providePredictionCacheCleanerForCar(@Named("car") PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}
			
			@Singleton @Provides @Named("pt")
			public PredictionCacheCleaner providePredictionCacheCleanerForPt(@Named("pt") PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}
			
			@Singleton @Provides @Named("car")
			public PredictionCache providePredictionCacheForCar() {
				return new HashPredictionCache();
			}
			
			@Singleton @Provides @Named("pt")
			public PredictionCache providePredictionCacheForPt() {
				return new HashPredictionCache();
			}
			
			@Singleton @Provides
			public ModeChoiceModel provideModeChoiceModel(Network network, @Named("car") TravelTime travelTime, GlobalConfigGroup config, @Named("car") PredictionCache carCache, @Named("pt") PredictionCache ptCache, TransitSchedule transitSchedule, Provider<TransitRouter> transitRouterProvider, TransitRouterConfigGroup transitRouterConfig, PlansCalcRouteConfigGroup routeConfig) {
				ChainAlternatives chainAlternatives = new TripChainAlternatives();
				ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom(), chainAlternatives, scenario.getNetwork(), ModeChoiceMNL.Mode.BEST_RESPONSE);

				BasicModeChoiceParameters carParameters = new BasicModeChoiceParameters(-4.42, -0.176 / 1000.0, -23.29 / 3600.0, true);
				BasicModeChoiceParameters ptParameters = new BasicModeChoiceParameters(0.0, -0.25 / 1000.0, -14.43 / 3600.0, false);
				BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(0.0, 0.0, -33.2 / 3600.0, false);
				
				double walkDistanceFactor = routeConfig.getTeleportedModeSpeeds().get("walk") / routeConfig.getBeelineDistanceFactors().get("walk");
				
				TripPredictor carPredictor = new NetworkPathPredictor(new QueueBasedThreadSafeDijkstra(config.getNumberOfThreads(), network, new OnlyTimeDependentTravelDisutility(travelTime), travelTime));
				//TripPredictor ptPredictor = new PublicTransitPredictor(new QueueBasedThreadSafeTransitRouter(config.getNumberOfThreads(), transitRouterProvider), transitSchedule);
				TripPredictor ptPredictor = new PublicTransitPredictor(new BlockingThreadSafeTransitRouter(transitRouterProvider), transitSchedule, walkDistanceFactor);
					
				model.addModeAlternative("car", new BasicModeChoiceAlternative(carParameters, carPredictor, carCache));
				model.addModeAlternative("pt", new BasicModeChoiceAlternative(ptParameters, ptPredictor, ptCache));
				model.addModeAlternative("walk", new BasicModeChoiceAlternative(walkParameters, new FixedSpeedPredictor(routeConfig.getTeleportedModeSpeeds().get("walk") / routeConfig.getBeelineDistanceFactors().get("walk"), new CrowflyDistancePredictor())));
				
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
