package ch.ethz.matsim.mode_choice.run;

import java.util.Iterator;

import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.pt.PtConstants;

public class RemoveLongPlans {
	final private int maximumNumberOfPlans;
	
	public RemoveLongPlans(int maximumNumberOfPlans) {
		this.maximumNumberOfPlans = maximumNumberOfPlans;
	}
	
	public void run(Population population) {
		Iterator<? extends Person> personIterator = population.getPersons().values().iterator();
		
		while (personIterator.hasNext()) {
			int count = TripStructureUtils.getActivities(personIterator.next().getSelectedPlan(), new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE)).size();
			
			if (count > maximumNumberOfPlans) {
				personIterator.remove();
			}
		}
	}
}
