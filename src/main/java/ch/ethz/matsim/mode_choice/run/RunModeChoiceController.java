package ch.ethz.matsim.mode_choice.run;

import java.util.Arrays;
import java.util.Iterator;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.scenario.ScenarioUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.mnl.CrowflyModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.CrowflyModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceMNL;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.sioux_falls.SiouxFallsUtils;

public class RunModeChoiceController {

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

		ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom());

		CrowflyModeChoiceParameters carParameters = new CrowflyModeChoiceParameters(30.0 * 1000.0 / 3600.0, 0.0, -0.176 / 1000.0, -23.29 / 3600.0);
		CrowflyModeChoiceParameters ptParameters = new CrowflyModeChoiceParameters(12.0 * 1000.0 / 3600.0, 0.0, -0.25 / 1000.0, -14.43 / 3600.0);
		CrowflyModeChoiceParameters walkParameters = new CrowflyModeChoiceParameters(8.0 * 1000.0 / 3600.0, 0.0, 0.0, -33.2 / 3600.0);
		
		model.addModeAlternative("car", new CrowflyModeChoiceAlternative(carParameters, true));
		model.addModeAlternative("pt", new CrowflyModeChoiceAlternative(ptParameters, false));
		model.addModeAlternative("walk", new CrowflyModeChoiceAlternative(walkParameters, false));

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
