package ch.ethz.matsim.discrete_mode_choice.examples;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

import ch.ethz.matsim.baseline_scenario.config.CommandLine;
import ch.ethz.matsim.baseline_scenario.config.CommandLine.ConfigurationException;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import ch.ethz.matsim.discrete_mode_choice.modules.EstimatorModule;
import ch.ethz.matsim.discrete_mode_choice.modules.SelectorModule;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

public class RunFrozenRandomness {
	static public void main(String[] args) throws ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args).build();
		URL configURL = IOUtils.newUrl(ExamplesUtils.getTestScenarioURL("siouxfalls-2014"), "config_default.xml");

		Config config = ConfigUtils.loadConfig(configURL);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory("output_frozen_randomness");
		config.controler().setLastIteration(1000);
		config.controler().setWriteEventsInterval(100);
		config.controler().setWritePlansInterval(100);
		
		cmd.applyConfiguration(config);

		Scenario scenario = ScenarioUtils.loadScenario(config);

		// Initialize a random number generator
		Random random = new Random(0);
		List<String> modes = Arrays.asList("car", "pt", "walk");

		for (Person person : scenario.getPopulation().getPersons().values()) {
			for (PlanElement element : person.getSelectedPlan().getPlanElements()) {
				if (element instanceof Activity) {
					// Go through all activities of all agents
					Activity activity = (Activity) element;

					for (String mode : modes) {
						// Sample an extreme value (Gumbel) error term
						// (see https://en.wikipedia.org/wiki/Gumbel_distribution)
						double uniformError = random.nextDouble();
						double gumbelError = -Math.log(-Math.log(uniformError));

						activity.getAttributes().putAttribute("next_error_" + mode, gumbelError);
					}
				}
			}
		}

		Controler controller = new Controler(scenario);

		DiscreteModeChoiceConfigurator.configureAsSubtourModeChoiceReplacement(config);
		DiscreteModeChoiceConfigurator.configureAsModeChoiceInTheLoop(config);
		DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
				.get(DiscreteModeChoiceConfigGroup.GROUP_NAME);

		dmcConfig.setTripEstimator("FrozenRandomness");
		dmcConfig.setTourEstimator(EstimatorModule.CUMULATIVE);
		dmcConfig.setSelector(SelectorModule.MAXIMUM);

		controller.addOverridingModule(new DiscreteModeChoiceModule());
		controller.addOverridingModule(new MyFrozenRandomnessExtension());

		controller.run();
	}
}
