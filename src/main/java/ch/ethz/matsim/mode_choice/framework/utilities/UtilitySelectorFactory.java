package ch.ethz.matsim.mode_choice.framework.utilities;

public interface UtilitySelectorFactory<T extends UtilityCandidate> {
	UtilitySelector<T> createUtilitySelector();
}
