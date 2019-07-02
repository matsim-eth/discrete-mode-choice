package ch.ethz.matsim.discrete_mode_choice.modules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

class ModeChainGeneratorTest {

	@Test
	void testModeChainGenerator() {
		Config config = ConfigUtils.loadConfig("C:\\Users\\spenazzi\\Projects\\sbb\\SBB\\input\\CNB\\config\\config.xml");
		Scenario scenario = ScenarioUtils.createScenario(config);
		ScenarioUtils.loadScenario(scenario);
		Controler controler = new Controler(scenario);
		DiscreteModeChoiceConfigurator.configureAsImportanceSampler(config);
		
		int nrPeopleToKeep = 20;
		if (nrPeopleToKeep > 0) {
            int interval = scenario.getPopulation().getPersons().size() / nrPeopleToKeep;
            int i = 0;
            LinkedList<Id<Person>> toRemove = new LinkedList<>();
            for (Person person : scenario.getPopulation().getPersons().values()) {
                if (i++ == interval) {
                    i = 0;
                } else {
                    toRemove.addLast(person.getId());
                }
            }
            toRemove.forEach(id -> scenario.getPopulation().removePerson(id));
        }
		controler.run();
	}
}
