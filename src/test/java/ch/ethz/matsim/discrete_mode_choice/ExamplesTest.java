package ch.ethz.matsim.discrete_mode_choice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.ethz.matsim.baseline_scenario.config.CommandLine.ConfigurationException;
import ch.ethz.matsim.discrete_mode_choice.examples.RunFrozenRandomness;
import ch.ethz.matsim.discrete_mode_choice.examples.RunImportanceSampler;
import ch.ethz.matsim.discrete_mode_choice.examples.RunModeChoiceInTheLoop;
import ch.ethz.matsim.discrete_mode_choice.examples.RunSubtourModeChoice;
import ch.ethz.matsim.discrete_mode_choice.examples.RunSubtourModeChoiceReplacement;

public class ExamplesTest {
	@Test
	@Disabled
	public void testExamples() throws ConfigurationException {
		String[] args = new String[] { "--config:controler.lastIteration", "1" };

		RunSubtourModeChoice.main(args);
		RunSubtourModeChoiceReplacement.main(args);
		RunImportanceSampler.main(args);
		RunModeChoiceInTheLoop.main(args);
		RunFrozenRandomness.main(args);
	}
}
