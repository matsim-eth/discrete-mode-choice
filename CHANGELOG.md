# CHANGELOG

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
