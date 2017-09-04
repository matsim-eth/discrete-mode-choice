package ch.ethz.matsim.mode_choice.run;

import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;

import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;

public class RunModeChoiceController {

	public static void main(String[] args) {

		Controler controler = new Controler (args[0]);
		
		//or would it be better to have it in a sturtuplistener and 
		//then access it through the MatsimServices?? mb sep '17
		controler.getStrategyManager().setPlanSelectorForRemoval(new OldPlanForRemovalSelector<>());
				
		controler.addOverridingModule( new AbstractModule() {
			@Override
			public void install() {
				
				this.addPlanStrategyBinding("ModeChoiceStrategy").toProvider( ModeChoiceStrategy.class ) ;
			}
		});
		
		controler.run();		
	}
}
