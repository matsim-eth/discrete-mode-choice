package ch.ethz.matsim.mode_choice.v2.framework.utilities;

public interface UtilitySelectorFactory<T extends UtilityCandidate> {
	UtilitySelector<T> createUtilitySelector();
}
