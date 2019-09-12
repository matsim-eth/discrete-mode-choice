package ch.ethz.matsim.discrete_mode_choice.examples;

import java.net.URL;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;

public class TestSiouxFalls {
	@Test
	public void testSiouxFallsWithSubtourModeChoiceReplacement() {
		URL scenarioURL = ExamplesUtils.getTestScenarioURL("siouxfalls-2014");

		Config config = ConfigUtils.loadConfig(IOUtils.newUrl(scenarioURL, "config_default.xml"));
		DiscreteModeChoiceConfigurator.configureAsSubtourModeChoiceReplacement(config);
		
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setLastIteration(1);

		Scenario scenario = ScenarioUtils.loadScenario(config);
		
		Controler controller = new Controler(scenario);
		controller.addOverridingModule(new DiscreteModeChoiceModule());
		
		controller.run();
	}
}
