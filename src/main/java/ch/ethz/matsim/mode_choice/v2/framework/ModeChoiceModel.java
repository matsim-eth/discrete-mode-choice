package ch.ethz.matsim.mode_choice.v2.framework;

import java.util.List;
import java.util.Random;

public interface ModeChoiceModel {
	ModeChoiceResult chooseModes(List<ModeChoiceTrip> trips, Random random) throws NoFeasibleChoiceException;

	static public enum FallbackBehaviour {
		IGNORE_AGENT, INITIAL_CHOICE, EXCEPTION
	}

	static public class NoFeasibleChoiceException extends Exception {
		public NoFeasibleChoiceException(String message) {
			super(message);
		}
	}
}
