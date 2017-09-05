package ch.ethz.matsim.mode_choice.alternatives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Trip;

public class TripChainAlternatives implements ChainAlternatives {
		
	@Override
	public List<List<String>> getTripChainAlternatives(Plan plan, String[] chainModes, String[] nonChainModes) {
		final List<Trip> trips = TripStructureUtils.getTrips( plan, new StageActivityTypesImpl("") );
		int numberOfTrips = trips.size();
		Set<String> modes = new HashSet<>();
		modes.addAll(Arrays.asList(chainModes));
		modes.addAll(Arrays.asList(nonChainModes));
		
		List<List<String>> allChains =  getCh(modes, numberOfTrips);
		
		ListIterator<List<String>> iter = allChains.listIterator();
		while(iter.hasNext()){
		    if(!isFeasable(iter.next(), trips, new HashSet<>(Arrays.asList(chainModes)), new HashSet<>(Arrays.asList(nonChainModes)))){
		        iter.remove();
		    }
		}
		
		return allChains;
	}
	
	private List<List<String>> getCh(Set<String> modes, int size) {
		
		List<List<String>> currentList = new ArrayList<>();

		
		for (int i = 0; i < size; i++){
			List<List<String>> newList = new ArrayList<>();
			if (currentList.isEmpty()) {
				
				for (String mode : modes) {
					List<String> l = new ArrayList<>();
					l.add(mode);
					newList.add(l);

				}
			}
			else {
				for (List<String> list : currentList) {
					
					for (String mode : modes) {
						List<String> llist = new ArrayList(list);
						llist.add(mode);
						newList.add(llist);
						
					}
				}
			}
			
			currentList.clear();
			currentList.addAll(newList);
		}
		
		
			
		
		return currentList;		
	}
	
	private boolean isFeasable(List<String> chain, List<Trip> trips, Set<String> chainModes, Set<String> nonChainModes) {
		
		Map<String, Id<Link>> locationsOfVehicles = new HashMap<>();
		Id<Link> startLinkId = trips.get(0).getOriginActivity().getLinkId();
		boolean isFirstHome = trips.get(0).getOriginActivity().getType().startsWith("home");
		int i = -1;
		for (String mode : chain) {
			i++;
			if (chainModes.contains(mode)) {
				if (i == 0) {
					
					locationsOfVehicles.put(mode, trips.get(i).getDestinationActivity().getLinkId());
				}
				else {
					
					if ((locationsOfVehicles.get(mode) != null && locationsOfVehicles.get(mode).
							equals(trips.get(i).getOriginActivity().getLinkId())) || trips.get(i).getOriginActivity().getLinkId().equals(startLinkId)) {
						locationsOfVehicles.put(mode, trips.get(i).getDestinationActivity().getLinkId());		
						continue;
							}
					else 
						return false;
				}
				
			}
		}
		
		
		for (String mode : locationsOfVehicles.keySet()) {
			
			if (!locationsOfVehicles.get(mode).equals(startLinkId))
				return false;
		}
		
		return true;
	}
}
