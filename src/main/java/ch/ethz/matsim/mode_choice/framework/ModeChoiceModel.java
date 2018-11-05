package ch.ethz.matsim.mode_choice.framework;

import java.util.List;
import java.util.Random;

public interface ModeChoiceModel {
	ModeChoiceResult chooseModes(List<ModeChoiceTrip> trips, Random random) throws NoFeasibleChoiceException;

	static public enum FallbackBehaviour {
		IGNORE_AGENT, INITIAL_CHOICE, EXCEPTION
	}

	static public class NoFeasibleChoiceException extends Exception {
		private static final long serialVersionUID = -7909941248706791794L;

		public NoFeasibleChoiceException(String message) {
			super(message);
		}
	}
}
