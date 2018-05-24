package ch.ethz.matsim.mode_choice.run;

import java.util.Iterator;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.config.TransitRouterConfigGroup;
import org.matsim.pt.router.TransitRouter;
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
import ch.ethz.matsim.mode_choice.mnl.BasicPublicTransitModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.BasicPublicTransitModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
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
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
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

			@Singleton
			@Provides
			@Named("car")
			public PredictionCacheCleaner providePredictionCacheCleanerForCar(@Named("car") PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}

			@Singleton
			@Provides
			@Named("pt")
			public PredictionCacheCleaner providePredictionCacheCleanerForPt(@Named("pt") PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}

			@Singleton
			@Provides
			@Named("car")
			public PredictionCache providePredictionCacheForCar() {
				return new HashPredictionCache();
			}

			@Singleton
			@Provides
			@Named("pt")
			public PredictionCache providePredictionCacheForPt() {
				return new HashPredictionCache();
			}

			@Singleton
			@Provides
			public ModeChoiceModel provideModeChoiceModel(Network network, @Named("car") TravelTime travelTime,
					GlobalConfigGroup config, @Named("car") PredictionCache carCache,
					@Named("pt") PredictionCache ptCache, TransitSchedule transitSchedule,
					Provider<TransitRouter> transitRouterProvider, TransitRouterConfigGroup transitRouterConfig,
					PlansCalcRouteConfigGroup routeConfig) {
				ChainAlternatives chainAlternatives = new TripChainAlternatives(false);
				ModeChoiceMNL model = new ModeChoiceMNL(chainAlternatives, scenario.getNetwork(),
						ModeChoiceMNL.Mode.SAMPLING);

				BasicModeChoiceParameters carParameters = new BasicModeChoiceParameters(0.0, -0.62 / 1000.0,
						-23.29 / 3600.0, true);
				BasicModeChoiceParameters ptParameters = new BasicModeChoiceParameters(0.0, -0.5 / 1000.0,
						-14.43 / 3600.0, false);
				BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(0.0, 0.0, -33.2 / 3600.0,
						false);

				BasicPublicTransitModeChoiceParameters extendedPtParameters = new BasicPublicTransitModeChoiceParameters(
						-14.43 / 3600.0, -0.5 / 1000.0, -33.2 / 3600.0, 0.0 / 3600.0, -24.13 / 3600.0, -3.0, 0.0);

				TripPredictor carPredictor = new FixedSpeedPredictor(30.0 * 1000.0 / 3600.0,
						new CrowflyDistancePredictor());
				TripPredictor ptPredictor = new FixedSpeedPredictor(14.0 * 1000.0 / 3600.0,
						new CrowflyDistancePredictor());
				TripPredictor walkPredictor = new FixedSpeedPredictor(routeConfig.getTeleportedModeSpeeds().get("walk")
						/ routeConfig.getBeelineDistanceFactors().get("walk"), new CrowflyDistancePredictor());

				double walkDistanceFactor = routeConfig.getTeleportedModeSpeeds().get("walk")
						/ routeConfig.getBeelineDistanceFactors().get("walk");

				carPredictor = new NetworkPathPredictor(new QueueBasedThreadSafeDijkstra(config.getNumberOfThreads(),
						network, new OnlyTimeDependentTravelDisutility(travelTime), travelTime));
				ptPredictor = new PublicTransitPredictor(
						new QueueBasedThreadSafeTransitRouter(config.getNumberOfThreads(), transitRouterProvider),
						transitSchedule, walkDistanceFactor);

				// BasicPublicTransitModeChoiceParameters ptParameters = new
				// BasicPublicTransitModeChoiceParameters(-14.43 / 3600.0, -0.5 / 1000.0, -33.2
				// / 3600.0, 0.0, 8.0 / 3600.0, -3, 0.0);

				//
				// TripPredictor carPredictor = new NetworkPathPredictor(new
				// QueueBasedThreadSafeDijkstra(config.getNumberOfThreads(), network, new
				// OnlyTimeDependentTravelDisutility(travelTime), travelTime));
				// TripPredictor ptPredictor = new PublicTransitPredictor(new
				// QueueBasedThreadSafeTransitRouter(config.getNumberOfThreads(),
				// transitRouterProvider), transitSchedule);
				// PublicTransitPredictor ptPredictor = new PublicTransitPredictor(new
				// BlockingThreadSafeTransitRouter(transitRouterProvider), transitSchedule,
				// walkDistanceFactor);

				ModeChoiceAlternative carAlternative = new BasicModeChoiceAlternative(carParameters, carPredictor,
						carCache);
				ModeChoiceAlternative ptAlternative = new BasicModeChoiceAlternative(ptParameters, ptPredictor,
						ptCache);
				ModeChoiceAlternative walkAlternative = new BasicModeChoiceAlternative(walkParameters, walkPredictor);

				ptAlternative = new BasicPublicTransitModeChoiceAlternative(extendedPtParameters,
						(PublicTransitPredictor) ptPredictor, ptCache);

				model.addModeAlternative("car", carAlternative);
				model.addModeAlternative("pt", ptAlternative);
				model.addModeAlternative("walk", walkAlternative);

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
