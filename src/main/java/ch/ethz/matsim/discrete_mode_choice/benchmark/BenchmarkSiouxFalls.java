package ch.ethz.matsim.discrete_mode_choice.benchmark;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

public class BenchmarkSiouxFalls {
	static public void main(String[] args) throws ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("output-path", "use-case", "use-time-mutator") //
				.build();

		URL configURL = IOUtils.newUrl(ExamplesUtils.getTestScenarioURL("siouxfalls-2014"), "config_default.xml");

		Config config = ConfigUtils.loadConfig(configURL);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory(cmd.getOptionStrict("output-path"));
		config.controler().setLastIteration(500);
		config.strategy().setFractionOfIterationsToDisableInnovation(0.8);
		cmd.applyConfiguration(config);

		switch (cmd.getOptionStrict("use-case")) {
		case "subtour-mode-choice-replacement":
			DiscreteModeChoiceConfigurator.configureAsSubtourModeChoiceReplacement(config);
			break;
		case "importance-sampler":
			DiscreteModeChoiceConfigurator.configureAsImportanceSampler(config);
			break;
		case "standard":
			config.addModule(new DiscreteModeChoiceConfigGroup());
			break;
		default:
			throw new IllegalStateException("Case: " + cmd.getOptionStrict("use-case"));
		}
		
		for (StrategySettings strategy : config.strategy().getStrategySettings()) {
			if (strategy.getWeight() == 0.01) {
				strategy.setWeight(0.1);
				strategy.setDisableAfter(-1);
			}
		}
		
		if (!Boolean.parseBoolean(cmd.getOptionStrict("use-time-mutator"))) {
			Set<ConfigGroup> remove = new HashSet<>();

			for (StrategySettings strategy : config.strategy().getStrategySettings()) {
				if (strategy.getStrategyName().equals("TimeAllocationMutator")) {
					remove.add(strategy);
				}
			}

			remove.forEach(config.strategy()::removeParameterSet);
		}

		Scenario scenario = ScenarioUtils.loadScenario(config);

		Controler controller = new Controler(scenario);
		controller.addOverridingModule(new DiscreteModeChoiceModule());
		controller.run();
	}
}
