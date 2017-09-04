package ch.ethz.matsim.mode_choice.replanning;

import java.util.ArrayList;
import java.util.Random;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.population.routes.NetworkRoute;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ChnageSingleLegModeAlgorithm implements PlanAlgorithm{

	private final Random rng;
	ModeChoiceModel modeChoiceModel;
	private Network network;
	public ChnageSingleLegModeAlgorithm(final Random rng, ModeChoiceModel modeChoiceModel,
			Network network) {
		this.rng = rng;
		this.modeChoiceModel = modeChoiceModel;
		this.network = network;
	}
	
	@Override
	public void run(Plan plan) {		
		ArrayList<Leg> legs = new ArrayList<Leg>();
		int cnt = 0;
		for (PlanElement pe : plan.getPlanElements()) {
			if (pe instanceof Leg) {
				legs.add((Leg) pe);
				cnt++;
			}
		}
		if (cnt == 0) {
			return;
		}
		
		int rndIdx = this.rng.nextInt(cnt);
		setLegMode(plan.getPerson(), legs.get(rndIdx));
		
	}

	private void setLegMode(Person person,  Leg leg) {
		Link startLink = network.getLinks().get(leg.getRoute().getStartLinkId());
		Link endLink = network.getLinks().get(leg.getRoute().getEndLinkId());
		String newMode = modeChoiceModel.chooseMode(person, startLink, endLink);
		
		leg.setMode(newMode);
		Route route = leg.getRoute() ;
		if ( route != null && route instanceof NetworkRoute) {
			((NetworkRoute)route).setVehicleId(null);
		}
		
	}

}
