# CHANGELOG
- Allow ReRoute strategy in combination with DCM
- BC: Remove over-complicated generics for UtilitySelector/Factory
- Update MATSim version, fix MainModeIdentifier and remove StageActivityTypes
- Make initial plan elements available to mode choice process

**1.0.8**

- Fix duplicate config parametersets
- Add getter to DiscreteModeChoiceConfigGroup
- Add Sioux Falls as integration test for SubtourModeChoiceReplacement
- Fix error messages in EstimatorModule
- Fix wrong initialization of array size in DiscreteModeChoiceAlgorithm
- Add IndexUtils to clarify how to calculate trip indices
- Rename index variables in DiscreteModeChoiceTrip to avoid confusion

**1.0.7**

- Switch to GitFlow repository model
- Update to MATSim 12
- Change version scheme to be in line with MATSim

**1.0.5**

- Fix cases where no trip/tour constraints are given
- Add problem filters (TripFilter, TourFilter)

**1.0.4**

- Fix bug in MATSimDayScoringEstimator
- Better handling of max_dur in activities
- Switch to weekly SNAPSHOT instead of continuous SNAPSHOT of MATSim
- Add check and warning for NaN utilities

**1.0.3**

- Fix HomeActivityFinder (was just based on links rather than BasicLocation before)
- Fix buggy vehicle constraints
- Make inference of origin/destination facility in AbstractTripRouterEstimator compatible with PlanRouter
- Fix MNL selection

**1.0.2**

- Fix 'restricted mode' setters for LinkAttributeConstraint and ShapeFileConstraint configuration
- Put in caching of trips again (it fell out accidentally during refactoring)
- Attach sources to maven artifacts

**1.0.1**

- Fix MATSimScoringEstimator parallelization
- Generalize MATSimTripScoringEstimator to all modes in scoring config
- Fix: Multinomial logit was filtering for < -minimumUtility
- Fix initial choice fallback for TourModel

**1.0.0**

- First stable release after refactoring
