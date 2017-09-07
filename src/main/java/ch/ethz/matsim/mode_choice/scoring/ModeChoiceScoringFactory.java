package ch.ethz.matsim.mode_choice.scoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.ScenarioConfigGroup;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.ActivityUtilityParameters;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.core.scoring.functions.ScoringParameters.Builder;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;
import org.matsim.utils.objectattributes.ObjectAttributes;

import com.google.inject.Inject;

public class ModeChoiceScoringFactory implements ScoringFunctionFactory {
	
	private final Scenario scenario;
	private final StageActivityTypes blackList;

	private final Map<Id, ScoringParameters> individualActivityParameters = new HashMap< >();


	@Inject
	ModeChoiceScoringFactory( final Scenario sc, ScoringParametersForPerson params,
			final StageActivityTypes typesNotToScore) {
		this.scenario = sc;
		this.blackList = typesNotToScore;
	}


	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		SumScoringFunction scoringFunctionSum = new SumScoringFunction();
		final PlanCalcScoreConfigGroup config = scenario.getConfig().planCalcScore();

		final ObjectAttributes personAttributes =
				scenario.getPopulation().getPersonAttributes();

		final ScoringParameters params = createActivityParams( person , config , scenario.getConfig().scenario(), personAttributes );
		
		Map<String, ModeChoiceScoringParameters> modeBetas = createModeBetas();
		ScoringModes utilityScoring = new ModeScoringUtil(modeBetas, scenario);

		scoringFunctionSum.addScoringFunction(new ModeChoiceLegScoring(utilityScoring, person));
		//the remaining scoring functions can be changed and adapted to the needs of the user
		
		
		scoringFunctionSum.addScoringFunction(new ModeChoiceLegScoring(utilityScoring, person));
		
		scoringFunctionSum.addScoringFunction(
				new CharyparNagelActivityScoring(
						params ) );
		scoringFunctionSum.addScoringFunction(
				new CharyparNagelAgentStuckScoring(
						params ) );
	    return scoringFunctionSum;
	  }
	
	private Map<String, ModeChoiceScoringParameters> createModeBetas() {
		
		Map<String, ModeChoiceScoringParameters> modesBetas = new HashMap<>();
		
		ModeChoiceScoringParameters modeChoiceSP = new ModeChoiceScoringParameters(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		
		modesBetas.put("car", modeChoiceSP);
		modesBetas.put("pt", modeChoiceSP);
		modesBetas.put("walk", modeChoiceSP);
		modesBetas.put("bike", modeChoiceSP);		
		
		return modesBetas;
	}


	private ScoringParameters createActivityParams(
			final Person person,
			final PlanCalcScoreConfigGroup config,
			final ScenarioConfigGroup scenarioConfig,
			final ObjectAttributes personAttributes) {
		if ( individualActivityParameters.containsKey( person.getId() ) ) {
			return individualActivityParameters.get( person.getId() );
		}

		final Builder builder =
				new Builder(config, config.getScoringParameters(null), scenarioConfig);
		final Set<String> handledTypes = new HashSet<String>();
		for ( Activity act : TripStructureUtils.getActivities( person.getSelectedPlan() , blackList ) ) {
			// XXX works only if no variation of type of activities between plans
			if ( !handledTypes.add( act.getType() ) ) continue; // parameters already gotten

			final String id = person.getId().toString();

			// I am not so pleased with this, as wrong parameters may silently be
			// used (for instance if individual preferences are ill-specified).
			// This should become nicer once we have a better format for specifying
			// utility parameters in the config.
			final ActivityUtilityParameters.Builder typeBuilder =
					new ActivityUtilityParameters.Builder(
							config.getActivityParams( act.getType() ) != null ?
									config.getActivityParams( act.getType() ) :
									new ActivityParams( act.getType() ) );

			final Double earliestEndTime =
					(Double) personAttributes.getAttribute(
						id,
						"earliestEndTime_"+act.getType() );
			if ( earliestEndTime != null ) {
				typeBuilder.setScoreAtAll(true);
				typeBuilder.setEarliestEndTime( earliestEndTime );
			}

			final Double latestStartTime =
					(Double) personAttributes.getAttribute(
						id,
						"latestStartTime_"+act.getType() );
			if ( latestStartTime != null ) {
				typeBuilder.setScoreAtAll(true);
				typeBuilder.setLatestStartTime(latestStartTime);
			}

			final Double minimalDuration =
					(Double) personAttributes.getAttribute(
						id,
						"minimalDuration_"+act.getType() );
			if ( minimalDuration != null ) {
				typeBuilder.setScoreAtAll( true );
				typeBuilder.setMinimalDuration(minimalDuration);
			}

			final Double typicalDuration =
					(Double) personAttributes.getAttribute(
						id,
						"typicalDuration_"+act.getType() );
			if ( typicalDuration != null ) {
				typeBuilder.setScoreAtAll( true );
				typeBuilder.setTypicalDuration_s(typicalDuration);
			}

			builder.setActivityParameters(
					act.getType(),
					typeBuilder );
		}

		final ScoringParameters params =
				builder.build();
		individualActivityParameters.put( person.getId() , params );
		return params;
	}


	
}