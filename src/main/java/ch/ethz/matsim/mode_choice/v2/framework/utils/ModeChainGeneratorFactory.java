package ch.ethz.matsim.mode_choice.v2.framework.utils;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface ModeChainGeneratorFactory {
	ModeChainGenerator createModeChainGenerator(Collection<String> modes, List<ModeChoiceTrip> trips);
}
