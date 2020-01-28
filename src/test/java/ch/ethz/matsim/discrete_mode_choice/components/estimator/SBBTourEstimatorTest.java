package ch.ethz.matsim.discrete_mode_choice.components.estimator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.router.TripRouter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.functions.ModeUtilityParameters;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.pt.config.TransitConfigGroup;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.discrete_mode_choice.components.estimators.CumulativeTourEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.estimators.MATSimDayScoringEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.estimators.MATSimTripScoringEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.estimation.CachedTripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.DefaultTourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;
import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.MATSimTripScoringConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ModeChainFilterRandomThresholdConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.utils.ScheduleWaitingTimeEstimatorModule;
import ch.sbb.matsim.mobsim.qsim.SBBTransitModule;

class SBBTourEstimatorTest {

	@Test
	void SBBParkingSpaceUf() {
		// TODO Auto-generated method stub
				System.setProperty("matsim.preferLocalDtds", "true");

		        final Config config = ConfigUtils.loadConfig("C:/Users/spenazzi/Projects/MatSim/Data/ZurichScenario/zurich_1pm/zurich_1pm/zurich_config.xml",
		        		new DiscreteModeChoiceConfigGroup(),new ModeChainFilterRandomThresholdConfigGroup());

		        Scenario scenario = ScenarioUtils.loadScenario(config);

		        // controler
		        Controler controler = new Controler(scenario);
		        
		        controler.addOverridingModule(new DiscreteModeChoiceModule());
		        
		        controler.addOverridingModule(new AbstractModule() {
		            @Override
		            public void install() {
		            	install(new SBBEstimatorModule());
		            	}
		         });
		        
		        
		        controler.run();
	}

	public class SBBEstimatorModule extends AbstractDiscreteModeChoiceExtension {
		
		public static final String SBB_SCORING = "SBBScoring";

		@Override
		public void installExtension() {

			bindTourEstimator(SBB_SCORING).to(SBBTourEstimator.class);
		}

		@Provides
		public SBBTourEstimator provideSBBTourEstimator(MATSimTripScoringEstimator tripEstimator,
				ScoringParametersForPerson scoringParametersForPerson, DiscreteModeChoiceConfigGroup dmcConfig) {
			return new SBBTourEstimator(new CachedTripEstimator(tripEstimator, dmcConfig.getCachedModes()),
					scoringParametersForPerson);
		}
	}

	
	public class SBBTourEstimator implements TourEstimator{
		
		private final TourEstimator delegate;
		private final ScoringParametersForPerson scoringParametersForPerson;

		public SBBTourEstimator(TripEstimator tripEstimator,
				ScoringParametersForPerson scoringParametersForPerson) {
			this.delegate = new CumulativeTourEstimator(tripEstimator);
			this.scoringParametersForPerson = scoringParametersForPerson;
		}

		@Override
		public TourCandidate estimateTour(Person person, List<String> modes, List<DiscreteModeChoiceTrip> trips,
				List<TourCandidate> previousTours) {
			ScoringParameters parameters = scoringParametersForPerson.getScoringParameters(person);

			// First, calculate utility from trips. They're simply summed up.
			TourCandidate candidate = delegate.estimateTour(person, modes, trips, previousTours);
			double utility = candidate.getUtility();

			//Add parking costs
			utility += estimateParkingCosts(person, modes, trips, previousTours);
			
			// Add daily constants for trips
			Set<String> uniqueModes = new HashSet<>(modes);

			for (String uniqueMode : uniqueModes) {
				ModeUtilityParameters modeParams = parameters.modeParams.get(uniqueMode);
				utility += modeParams.dailyUtilityConstant;
				utility += parameters.marginalUtilityOfMoney * modeParams.dailyMoneyConstant;
			}

			return new DefaultTourCandidate(utility, candidate.getTripCandidates());
		}

		public double estimateParkingCosts(Person person, List<String> modes, List<DiscreteModeChoiceTrip> trips,
				List<TourCandidate> previousTours) {
			
			double utility = 0.0;

			if(modes.get(0).compareTo("car")==0) {
				List<DiscreteModeChoiceTrip> carTrips = new ArrayList();
				for (int i = 0; i < modes.size(); i++) {
					if(modes.get(i).compareTo("car") == 0) { 
						carTrips.add(trips.get(i));
					} 
				}
				for (int i = 0; i < carTrips.size()-1; i++) {
					utility += (carTrips.get(i+1).getDepartureTime()  - carTrips.get(i).getDestinationActivity().getStartTime());
				}
			}
			
			return 0;
		}
	}
}

