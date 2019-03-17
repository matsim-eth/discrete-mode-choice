package ch.ethz.matsim.discrete_mode_choice.benchmark;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.run.RunBerlinScenario;

import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

public class BenchmarkBerlin {
	static public void main(String[] args) throws ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("output-path", "use-case", "size", "use-time-mutator") //
				.build();

		RunBerlinScenario berlin = new RunBerlinScenario("berlin-v5.3-" + cmd.getOptionStrict("size") + ".config.xml",
				"overridingConfig.xml");

		Config config = berlin.prepareConfig();
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory(cmd.getOptionStrict("output-path"));
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

		DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
				.get(DiscreteModeChoiceConfigGroup.GROUP_NAME);
		dmcConfig.getDefaultModeAvailabilityConfig()
				.setAvailableModes(Arrays.asList("car", "bicycle", "walk", "pt", "ride"));

		if (!Boolean.parseBoolean(cmd.getOptionStrict("use-time-mutator"))) {
			Set<ConfigGroup> remove = new HashSet<>();

			for (StrategySettings strategy : config.strategy().getStrategySettings()) {
				if (strategy.getStrategyName().equals("TimeAllocationMutator")) {
					remove.add(strategy);
				}
			}

			remove.forEach(config.strategy()::removeParameterSet);
		}

		Set<Id<Person>> remove = new HashSet<>();
		Scenario scenario = berlin.prepareScenario();

		for (Person person : scenario.getPopulation().getPersons().values()) {
			for (Plan plan : person.getPlans()) {
				int numberOfActivities = 0;

				for (PlanElement element : plan.getPlanElements()) {
					if (element instanceof Activity) {
						Activity activity = (Activity) element;

						if (!activity.getType().contains("interaction")) {
							numberOfActivities++;
						}
					}
				}

				if (numberOfActivities > 10) {
					remove.add(person.getId());
				}
			}
		}

		remove.forEach(scenario.getPopulation()::removePerson);

		new PopulationWriter(scenario.getPopulation()).write("output_pop.xml.gz");
		new ConfigWriter(scenario.getConfig()).write("output_config.xml");

		Controler controller = berlin.prepareControler();
		controller.addOverridingModule(new DiscreteModeChoiceModule());

		berlin.run();
	}
}
