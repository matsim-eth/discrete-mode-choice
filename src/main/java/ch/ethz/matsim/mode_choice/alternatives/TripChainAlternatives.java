package ch.ethz.matsim.mode_choice.alternatives;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Trip;

public class TripChainAlternatives implements ChainAlternatives {
	final private boolean matsimStyle;
	
	public TripChainAlternatives(boolean matsimStyle) {
		this.matsimStyle = matsimStyle;
	}
		
	@Override
	public List<List<String>> getTripChainAlternatives(Plan plan, List<String> chainModes, List<String> nonChainModes) {
		final List<Trip> trips = TripStructureUtils.getTrips( plan, new StageActivityTypesImpl("") );
		int numberOfTrips = trips.size();
		Set<String> modes = new HashSet<>();
		modes.addAll(chainModes);
		modes.addAll(nonChainModes);
		
		List<List<String>> allChains =  getCh(modes, numberOfTrips, trips, new HashSet<>(chainModes));
		
		ListIterator<List<String>> iter = allChains.listIterator();
		while(iter.hasNext()){
			if ( matsimStyle ) {
			    if(!isFeasableMATSimStyle(iter.next(), trips, new HashSet<>(chainModes), new HashSet<>(nonChainModes))){
			        iter.remove();
			    }
			}
			else {
				if(!isFeasable(iter.next(), trips, new HashSet<>(chainModes), new HashSet<>(nonChainModes))){
			        iter.remove();
			    }				
			}			
		}
		
		return allChains;
	}
	
	private List<List<String>> getCh(Set<String> modes, int size, List<Trip> trips, Set<String> chainModes) {
		
		List<List<String>> currentList = new LinkedList<>();

		
		for (int i = 0; i < size; i++){
			List<List<String>> newList = new LinkedList<>();
			if (currentList.isEmpty()) {
				
				for (String mode : modes) {
					List<String> l = new LinkedList<>();
					l.add(mode);
					newList.add(l);

				}
			}
			else {
				for (List<String> list : currentList) {
					
					for (String mode : modes) {
						List<String> llist = new LinkedList<String>(list);
						llist.add(mode);
						if (isFeasablePart(llist, trips.subList(0, llist.size()), chainModes))
							newList.add(llist);
					}
				}
			}
			
			currentList.clear();
			currentList.addAll(newList);
		}		
		return currentList;		
	}
	
	private boolean isFeasablePart(List<String> chain, List<Trip> trips, Set<String> chainModes) {
		
		Map<String, Id<Link>> locationsOfVehicles = new HashMap<>();
		Id<Link> startLinkId = trips.get(0).getOriginActivity().getLinkId();
		for (String mode : chainModes) 
			locationsOfVehicles.put(mode, startLinkId);
		int i = -1;
		for (String mode : chain) {
			i++;
			if (chainModes.contains(mode)) {
				if (i == 0) {
					
					locationsOfVehicles.put(mode, trips.get(i).getDestinationActivity().getLinkId());
				}
				else {
					
					if ((locationsOfVehicles.get(mode) != null && locationsOfVehicles.get(mode).
							equals(trips.get(i).getOriginActivity().getLinkId())) ||
							(trips.get(i).getOriginActivity().getLinkId().equals(startLinkId) 
									  && locationsOfVehicles.get(mode) != null &&
										locationsOfVehicles.get(mode).equals(startLinkId))) {
						locationsOfVehicles.put(mode, trips.get(i).getDestinationActivity().getLinkId());		
						continue;
						}
					else 
						return false;
				}				
			}
		}
						
		return true;		
	}
	
	private boolean isFeasable(List<String> chain, List<Trip> trips, Set<String> chainModes, Set<String> nonChainModes) {
		
		Map<String, Id<Link>> locationsOfVehicles = new HashMap<>();
		Id<Link> startLinkId = trips.get(0).getOriginActivity().getLinkId();
		for (String mode : chainModes) 
			locationsOfVehicles.put(mode, startLinkId);
		int i = -1;
		for (String mode : chain) {
			i++;
			if (chainModes.contains(mode)) {
				if (i == 0) {
					
					locationsOfVehicles.put(mode, trips.get(i).getDestinationActivity().getLinkId());
				}
				else {
					
					if ((locationsOfVehicles.get(mode) != null && locationsOfVehicles.get(mode).
							equals(trips.get(i).getOriginActivity().getLinkId())) || (trips.get(i).getOriginActivity().getLinkId().equals(startLinkId) 
									  && locationsOfVehicles.get(mode) != null &&
										locationsOfVehicles.get(mode).equals(startLinkId))) {
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
	
	private boolean isFeasableMATSimStyle(List<String> chain, List<Trip> trips, Set<String> chainModes, Set<String> nonchainModes) {
		Map<String, Id<Link>> locationsOfVehicles = new HashMap<>();
		Map<String, Set<Id<Link>>> startLocationMap = new HashMap<>();
		Map<String, Set<Id<Link>>> endLocationMap = new HashMap<>();

		Id<Link> startLinkId = trips.get(0).getOriginActivity().getLinkId();
		for (String mode : chainModes) 
			locationsOfVehicles.put(mode, startLinkId);

		
		int i = -1;
		for (String mode : chain) {
			i++;
			if (chainModes.contains(mode)) {
				if (i == 0) {
					
					locationsOfVehicles.put(mode, trips.get(i).getDestinationActivity().getLinkId());
				}
				else {
					
					if ((locationsOfVehicles.get(mode) != null && locationsOfVehicles.get(mode).
							equals(trips.get(i).getOriginActivity().getLinkId())) || (trips.get(i).getOriginActivity().getLinkId().equals(startLinkId) 
									  && locationsOfVehicles.get(mode) != null &&
										locationsOfVehicles.get(mode).equals(startLinkId))) {
						locationsOfVehicles.put(mode, trips.get(i).getDestinationActivity().getLinkId());		
						continue;
							}
					else 
						return false;
				}
				
			}
			else if (nonchainModes.contains(mode)) {
				if (i == 0) {
					
					Set<Id<Link>> newSet1 = new HashSet<>();
					newSet1.add(trips.get(i).getOriginActivity().getLinkId());
					startLocationMap.put(mode, newSet1);
					Set<Id<Link>> newSet2 = new HashSet<>();
					newSet2.add(trips.get(i).getDestinationActivity().getLinkId());
					endLocationMap.put(mode, newSet2);
				}
				else {
					
					if (endLocationMap.containsKey(mode) && endLocationMap.get(mode).contains(trips.get(i).getOriginActivity().getLinkId())) {
						
						endLocationMap.get(mode).remove(trips.get(i).getOriginActivity().getLinkId());
						endLocationMap.get(mode).add(trips.get(i).getDestinationActivity().getLinkId());
						
						if (startLocationMap.containsKey(mode) && startLocationMap.get(mode).contains(trips.get(i).getDestinationActivity().getLinkId())) {
							
							startLocationMap.get(mode).remove(trips.get(i).getDestinationActivity().getLinkId());
						}

					}
					else {
						if (!endLocationMap.containsKey(mode)){
							
							Set<Id<Link>> newSet2 = new HashSet<>();
							newSet2.add(trips.get(i).getDestinationActivity().getLinkId());
							endLocationMap.put(mode, newSet2);
						}
						else {
							Set<Id<Link>> oldSet = startLocationMap.get(mode);
							oldSet.add(trips.get(i).getDestinationActivity().getLinkId());
							endLocationMap.put(mode, oldSet);
							
						}							
						
						if (!startLocationMap.containsKey(mode)) {
							Set<Id<Link>> newSet1 = new HashSet<>();
							newSet1.add(trips.get(i).getOriginActivity().getLinkId());
							startLocationMap.put(mode, newSet1);
							
						}
						else {
							Set<Id<Link>> oldSet = startLocationMap.get(mode);
							oldSet.add(trips.get(i).getOriginActivity().getLinkId());
							startLocationMap.put(mode, oldSet);
						}							
					}					
				}				
			}
		}		
		
		for (String mode : locationsOfVehicles.keySet()) {
			
			if (!locationsOfVehicles.get(mode).equals(startLinkId))
				return false;
		}
		
		for (String mode : startLocationMap.keySet()) {
			
			if (!startLocationMap.get(mode).isEmpty())
				return false;
		}
		
		return true;
	}
}
