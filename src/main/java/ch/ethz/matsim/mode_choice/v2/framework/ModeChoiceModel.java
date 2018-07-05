package ch.ethz.matsim.mode_choice.v2.framework;

import java.util.List;
import java.util.Random;

public interface ModeChoiceModel {
	ModeChoiceResult chooseModes(List<ModeChoiceTrip> trips, Random random);
}
