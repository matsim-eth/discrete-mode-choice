package ch.ethz.matsim.discrete_mode_choice.model.utilities;

public interface UtilitySelectorFactory<T extends UtilityCandidate> {
	UtilitySelector<T> createUtilitySelector();
}
