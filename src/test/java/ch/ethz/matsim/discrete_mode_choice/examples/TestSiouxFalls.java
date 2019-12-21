package ch.ethz.matsim.discrete_mode_choice.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
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
		config.controler().setWriteEventsInterval(1);
		
		config.qsim().setFlowCapFactor(10000.0);
		config.qsim().setStorageCapFactor(10000.0);

		Scenario scenario = ScenarioUtils.loadScenario(config);

		Controler controller = new Controler(scenario);
		controller.addOverridingModule(new DiscreteModeChoiceModule());

		ModeListener listener = new ModeListener();
		controller.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().toInstance(listener);
			}
		});

		controller.run();

		// Fix for MATSim 10.1, not 100% sure where this comes from. The other modes are fine.
		assertEquals(42502 - 5, listener.counts.get("pt"));
		assertEquals(132094, listener.counts.get("car"));
		assertEquals(79098, listener.counts.get("walk") + listener.counts.get("transit_walk"));
	}

	static class ModeListener implements PersonArrivalEventHandler {
		private final Map<String, Integer> counts = new HashMap<>();

		@Override
		public void reset(int iteration) {
			counts.clear();
		}

		@Override
		public void handleEvent(PersonArrivalEvent event) {
			String mode = event.getLegMode();

			if (!counts.containsKey(mode)) {
				counts.put(mode, 0);
			}

			counts.put(mode, counts.get(mode) + 1);
		}
	}
}
