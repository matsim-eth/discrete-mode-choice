package ch.ethz.matsim.mode_choice.run;

import java.util.Arrays;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.mnl.CrowflyModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceMNL;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.sioux_falls.SiouxFallsUtils;

public class RunModeChoiceController {

	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig(SiouxFallsUtils.getConfigURL());
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);

		// Set up MNL

		ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom());

		for (String mode : Arrays.asList("car", "pt", "walk")) {
			model.addModeAlternative(mode,
					new CrowflyModeChoiceAlternative(config.planCalcScore().getMarginalUtilityOfMoney(),
							config.plansCalcRoute().getModeRoutingParams().get(mode),
							config.planCalcScore().getModes().get(mode)));
		}

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				bind(ModeChoiceModel.class).toInstance(model);
			}
		});

		// or would it be better to have it in a sturtuplistener and
		// then access it through the MatsimServices?? mb sep '17
		controler.getStrategyManager().setPlanSelectorForRemoval(new OldPlanForRemovalSelector<>());

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {

				this.addPlanStrategyBinding("ModeChoiceStrategy").toProvider(ModeChoiceStrategy.class);
			}
		});

		controler.run();
	}
}
