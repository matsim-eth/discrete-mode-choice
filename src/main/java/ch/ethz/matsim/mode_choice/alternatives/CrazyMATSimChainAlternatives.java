package ch.ethz.matsim.mode_choice.alternatives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.matsim.api.core.v01.BasicLocation;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contrib.locationchoice.utils.PlanUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.algorithms.PermissibleModesCalculator;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.TripRouter;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Subtour;
import org.matsim.core.router.TripStructureUtils.Trip;

public class CrazyMATSimChainAlternatives implements ChainAlternatives {

	private static class Candidate {
		final Subtour subtour;
		final String newTransportMode;

		public Candidate(
				final Subtour subtour,
				final String newTransportMode) {
			this.subtour = subtour;
			this.newTransportMode = newTransportMode;
		}
	}
	
	private Collection<String> modes;
	private final Collection<String> chainBasedModes;
	private Collection<String> singleTripSubtourModes;

	private final StageActivityTypes stageActivityTypes;


	private PermissibleModesCalculator permissibleModesCalculator;

	private boolean anchorAtFacilities = false;
	
	private MainModeIdentifier mainModeIdentifier;
	
	public CrazyMATSimChainAlternatives(
			final StageActivityTypes stageActivityTypes,
			final MainModeIdentifier mainModeIdentifier,
			final PermissibleModesCalculator permissibleModesCalculator,
			final String[] modes,
			final String[] chainBasedModes
			) {
		this.stageActivityTypes = stageActivityTypes;
		this.permissibleModesCalculator = permissibleModesCalculator;
		this.modes = Arrays.asList(modes);
		this.chainBasedModes = Arrays.asList(chainBasedModes);
		this.singleTripSubtourModes = this.chainBasedModes;
		this.mainModeIdentifier = mainModeIdentifier;
		
	}
	
	
	
	
	@Override
	public List<List<String>> getTripChainAlternatives(Plan plan, List<String> chainModes, List<String> nonChainModes) {

		List<List<String>> allChains = new LinkedList<>();
		final Id<? extends BasicLocation> homeLocation = anchorAtFacilities ?
				((Activity) plan.getPlanElements().get(0)).getFacilityId() :
				((Activity) plan.getPlanElements().get(0)).getLinkId();
			Collection<String> permissibleModesForThisPlan = permissibleModesCalculator.getPermissibleModes(plan);

			List<Candidate> choiceSet =
				determineChoiceSet(
						homeLocation,
						TripStructureUtils.getTrips( plan , stageActivityTypes ),
						TripStructureUtils.getSubtours(
							plan,
							stageActivityTypes,
							anchorAtFacilities),
						permissibleModesForThisPlan);
		
		for (Candidate c : choiceSet) {
			List<String> l = new LinkedList<>();
			
			int index = -1;
			boolean origin = false;
			List<Trip> trips = TripStructureUtils.getTrips( plan , stageActivityTypes );
			
			for (Trip tr : trips) {
				l.add(mainModeIdentifier.identifyMainMode( tr.getTripElements() ));
			}
			
			for (Trip trip : c.subtour.getTrips()) {				
				index = -1;
				for (PlanElement pe : plan.getPlanElements()) {					
					
					if (pe instanceof Activity) {
						
						if (pe == trip.getOriginActivity()) {
							origin = true;
							
						}
						else if (pe == trip.getDestinationActivity() && origin) {
							l.set(index, c.newTransportMode);
							origin = false;
						}
						else
							origin = false;
						index++;
					}

				}
				
				
			}		
			
			allChains.add(l);
			
		}			
		return allChains;
	}

	
	private List<Candidate> determineChoiceSet(
			final Id<? extends BasicLocation> homeLocation,
			final List<Trip> trips,
			final Collection<Subtour> subtours,
			final Collection<String> permissibleModesForThisPerson) {
		final ArrayList<Candidate> choiceSet = new ArrayList<Candidate>();
		for ( Subtour subtour : subtours ) {
			if ( !subtour.isClosed() ) {
				continue;
			}

			if ( containsUnknownMode( subtour ) ) {
				continue;
			}

			final Set<String> usableChainBasedModes = new LinkedHashSet<>();
			final Id<? extends BasicLocation> subtourStartLocation = anchorAtFacilities ?
				subtour.getTrips().get( 0 ).getOriginActivity().getFacilityId() :
				subtour.getTrips().get( 0 ).getOriginActivity().getLinkId();
			
			final Collection<String> testingModes =
				subtour.getTrips().size() == 1 ?
					singleTripSubtourModes :
					chainBasedModes;

			for (String mode : testingModes) {
				Id<? extends BasicLocation> vehicleLocation = homeLocation;
				Activity lastDestination =
					findLastDestinationOfMode(
						trips.subList(
							0,
							trips.indexOf( subtour.getTrips().get( 0 ) )),
						mode);
				if (lastDestination != null) {
					vehicleLocation = getLocationId( lastDestination );
				}
				if (vehicleLocation.equals(subtourStartLocation)) {
					usableChainBasedModes.add(mode);
				}
			}
			
			Set<String> usableModes = new LinkedHashSet<>();
			if (isMassConserving(subtour)) { // We can only replace a subtour if it doesn't itself move a vehicle from one place to another
				for (String candidate : permissibleModesForThisPerson) {
					if (chainBasedModes.contains(candidate)) {
						if (usableChainBasedModes.contains(candidate)) {
							usableModes.add(candidate);
						}
					} else {
						usableModes.add(candidate);
					}
				} 
			}

			usableModes.remove(getTransportMode(subtour));
			for (String transportMode : usableModes) {
				choiceSet.add(
						new Candidate(
							subtour,
							transportMode ));
			}
		}
		return choiceSet;
	}

	private boolean containsUnknownMode(final Subtour subtour) {
		
		return false;
	}

	private boolean isMassConserving(final Subtour subtour) {
		for (String mode : chainBasedModes) {
			if (!isMassConserving(subtour, mode)) {
				return false;
			} 
		}
		return true;
	}

	private boolean isMassConserving(
			final Subtour subtour,
			final String mode) {
		final Activity firstOrigin =
			findFirstOriginOfMode(
					subtour.getTrips(),
					mode);

		if (firstOrigin == null) {
			return true;
		}

		final Activity lastDestination =
			findLastDestinationOfMode(
					subtour.getTrips(),
					mode);

		return atSameLocation(firstOrigin, lastDestination);
	}

	private Id<? extends BasicLocation> getLocationId(Activity activity) {
		return anchorAtFacilities ?
			activity.getFacilityId() :
			activity.getLinkId();
	}
	
	private boolean atSameLocation(Activity firstLegUsingMode,
			Activity lastLegUsingMode) {
		return anchorAtFacilities ?
			firstLegUsingMode.getFacilityId().equals(
					lastLegUsingMode.getFacilityId() ) :
			firstLegUsingMode.getLinkId().equals(
					lastLegUsingMode.getLinkId() );
	}

	private Activity findLastDestinationOfMode(
			final List<Trip> tripsToSearch,
			final String mode) {
		final List<Trip> reversed = new ArrayList<>(tripsToSearch);
		Collections.reverse( reversed );
		for (Trip trip : reversed) {
			if ( mode.equals( mainModeIdentifier.identifyMainMode( trip.getTripElements() ) ) ) {
				return trip.getDestinationActivity();
			}
		}
		return null;
	}
	
	private Activity findFirstOriginOfMode(
			final List<Trip> tripsToSearch,
			final String mode) {
		for (Trip trip : tripsToSearch) {
			if ( mode.equals( mainModeIdentifier.identifyMainMode( trip.getTripElements() ) ) ) {
				return trip.getOriginActivity();
			}
		}
		return null;
	}

	private String getTransportMode(final Subtour subtour) {
		return mainModeIdentifier.identifyMainMode(
				subtour.getTrips().get( 0 ).getTripElements() );
	}

	private static void applyChange(
			final Candidate whatToDo,
			final Plan plan) {
		for (Trip trip : whatToDo.subtour.getTrips()) {
			TripRouter.insertTrip(
					plan,
					trip.getOriginActivity(),
					Collections.singletonList( PopulationUtils.createLeg(whatToDo.newTransportMode) ),
					trip.getDestinationActivity());
		}
	}

	public void setAnchorSubtoursAtFacilitiesInsteadOfLinks(
			final boolean anchorAtFacilities) {
		this.anchorAtFacilities = anchorAtFacilities;
	}
	
	
	
	
	
}
