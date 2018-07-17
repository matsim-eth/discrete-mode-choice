package ch.ethz.matsim.mode_choice.v2.framework.utilities;

public interface UtilityCandidate {
	double getUtility();
	
	boolean isFallback();
	void setFallback(boolean isFallback);
}
