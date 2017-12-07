package ch.ethz.matsim.mode_choice.replanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.population.routes.NetworkRoute;

import ch.ethz.matsim.mode_choice.DefaultModeChoiceTrip;
import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ChangeSingleLegModeAlgorithm implements PlanAlgorithm{

	private final Random rng;
	ModeChoiceModel modeChoiceModel;
	private Network network;
	public ChangeSingleLegModeAlgorithm(final Random rng, ModeChoiceModel modeChoiceModel,
			Network network) {
		this.rng = rng;
		this.modeChoiceModel = modeChoiceModel;
		this.network = network;
	}
	
	@Override
	public void run(Plan plan) {	
		ArrayList<Leg> legs = new ArrayList<>();
		ArrayList<Activity> activities = new ArrayList<>();
		
		int cnt = 0;
		for (PlanElement pe : plan.getPlanElements()) {
			if (pe instanceof Leg) {
				legs.add((Leg) pe);
				cnt++;
			} else if (pe instanceof Activity) {
				activities.add((Activity) pe);
			}
		}
		if (cnt == 0) {
			return;
		}
		
		
		
		int rndIdx = this.rng.nextInt(cnt);
		setLegMode(plan.getPerson(), legs.get(rndIdx), activities.get(rndIdx).getLinkId(), activities.get(rndIdx + 1).getLinkId(), activities.get(rndIdx).getEndTime());
		
	}

	private void setLegMode(Person person,  Leg leg, Id<Link> originLinkId, Id<Link> destinationLinkId, double departureTime) {
		Link startLink = network.getLinks().get(originLinkId);
		Link endLink = network.getLinks().get(destinationLinkId);
		String newMode = modeChoiceModel.chooseMode(new DefaultModeChoiceTrip(startLink, endLink, departureTime, person));
		
		leg.setMode(newMode);
		Route route = leg.getRoute() ;
		if ( route != null && route instanceof NetworkRoute) {
			((NetworkRoute)route).setVehicleId(null);
		}
	}

}
