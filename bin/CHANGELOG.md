# CHANGELOG

**1.0.5 (under development)**

- No commits yet

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