package ch.ethz.matsim.mode_choice.run;

import java.util.Iterator;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.alternatives.ChainAlternatives;
import ch.ethz.matsim.mode_choice.alternatives.TripChainAlternatives;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceMNL;
import ch.ethz.matsim.mode_choice.mnl.prediction.CrowflyDistancePredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.FixedSpeedPredictor;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.sioux_falls.SiouxFallsUtils;

public class RunModeChoiceController {

	public static void main(String[] args) {
		//Config config = ConfigUtils.loadConfig(SiouxFallsUtils.getConfigURL());
		Config config = ConfigUtils.loadConfig(args[0]);
		config.strategy().setMaxAgentPlanMemorySize(1);
		
		boolean bestResponse = args.length > 1 ? Boolean.valueOf(args[1]) : false;

		//config.strategy().clearStrategySettings();

		/*StrategySettings settings = new StrategySettings();
		settings.setWeight(1.0);
		settings.setStrategyName("ModeChoiceStrategy");
		config.strategy().addStrategySettings(settings);*/

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		
		new RemoveLongPlans(10).run(scenario.getPopulation());

		/*Iterator<? extends Person> personIterator = scenario.getPopulation().getPersons().values().iterator();

		while (personIterator.hasNext()) {
			personIterator.next();

			if (MatsimRandom.getRandom().nextDouble() < 0.99) {
				personIterator.remove();
			}
		}*/

		// Set up MNL
		
		ChainAlternatives chainAlternatives = new TripChainAlternatives();
		ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom(), chainAlternatives, scenario.getNetwork(), bestResponse ? ModeChoiceMNL.Mode.BEST_RESPONSE : ModeChoiceMNL.Mode.SAMPLING);

		BasicModeChoiceParameters carParameters = new BasicModeChoiceParameters(0.0, -0.176 / 1000.0, -23.29 / 3600.0, true);
		BasicModeChoiceParameters ptParameters = new BasicModeChoiceParameters(0.0, -0.25 / 1000.0, -14.43 / 3600.0, false);
		BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(0.0, 0.0, -33.2 / 3600.0, false);
		
		model.addModeAlternative("car", new BasicModeChoiceAlternative(carParameters, new FixedSpeedPredictor(30.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor())));
		model.addModeAlternative("pt", new BasicModeChoiceAlternative(ptParameters, new FixedSpeedPredictor(12.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor())));
		model.addModeAlternative("walk", new BasicModeChoiceAlternative(walkParameters, new FixedSpeedPredictor(8.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor())));

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				bind(ModeChoiceModel.class).toInstance(model);
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
