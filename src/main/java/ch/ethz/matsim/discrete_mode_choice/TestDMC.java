package ch.ethz.matsim.discrete_mode_choice;

import java.util.LinkedList;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.qsim.components.QSimComponentsConfig;
import org.matsim.core.mobsim.qsim.components.StandardQSimComponentConfigurator;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.ScoringFunctionFactory;

import com.google.inject.Provides;

import ch.ethz.matsim.discrete_mode_choice.modules.ConstraintModule;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import ch.ethz.matsim.discrete_mode_choice.modules.SelectorModule;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ModeChainFilterRandomThresholdConfigGroup;

public class TestDMC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("matsim.preferLocalDtds", "true");

        final Config config = ConfigUtils.loadConfig("/home/stefanopenazzi/projects/sbb/dmc/input_data/zurich_1pm/zurich_config.xml",
        		new DiscreteModeChoiceConfigGroup(),new ModeChainFilterRandomThresholdConfigGroup());

        Scenario scenario = ScenarioUtils.loadScenario(config);

        // controler
        Controler controler = new Controler(scenario);
        
        System.out.println(config.controler().getOutputDirectory());
        
        controler.addOverridingModule(new DiscreteModeChoiceModule());
        
        // make population smaller
       /*int nrPeopleToKeep = 100;
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
        }*/
        
        System.setProperty("scenario","sbb");
        
        controler.run();
    }

}
