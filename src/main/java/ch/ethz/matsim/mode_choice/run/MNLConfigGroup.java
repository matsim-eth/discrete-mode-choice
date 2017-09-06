package ch.ethz.matsim.mode_choice.run;

import org.matsim.core.config.ReflectiveConfigGroup;

import ch.ethz.matsim.mode_choice.mnl.ModeChoiceMNL;

public class MNLConfigGroup extends ReflectiveConfigGroup {
	final public static String GROUP_NAME = "MNL";
	
	final public static String MODE = "mode";
	final public static String CAR_UTILITY = "carUtility";
	final public static String NUMBER_OF_THREADS = "numberOfThreads";
	
	public enum MNLCarUtility {
		CROWFLY, NETWORK
	}
	
	private ModeChoiceMNL.Mode mode = ModeChoiceMNL.Mode.SAMPLING;
	private MNLCarUtility carUtility = MNLCarUtility.CROWFLY;
	private Integer numberOfThreads = null;
	
	public MNLConfigGroup() {
		super(GROUP_NAME);
	}

	@StringGetter(MODE)
	public ModeChoiceMNL.Mode getMode() {
		return mode;
	}

	@StringSetter(MODE)
	public void setMode(ModeChoiceMNL.Mode mode) {
		this.mode = mode;
	}

	@StringGetter(CAR_UTILITY)
	public MNLCarUtility getCarUtility() {
		return carUtility;
	}

	@StringSetter(CAR_UTILITY)
	public void setCarUtility(MNLCarUtility carUtility) {
		this.carUtility = carUtility;
	}

	@StringGetter(NUMBER_OF_THREADS)
	public Integer getNumberOfThreads() {
		return numberOfThreads;
	}

	@StringSetter(NUMBER_OF_THREADS)
	public void setNumberOfThreads(Integer numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}
}
