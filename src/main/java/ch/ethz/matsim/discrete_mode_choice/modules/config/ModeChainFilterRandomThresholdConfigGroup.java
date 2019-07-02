package ch.ethz.matsim.discrete_mode_choice.modules.config;

import org.matsim.core.config.ReflectiveConfigGroup;


public class ModeChainFilterRandomThresholdConfigGroup extends ReflectiveConfigGroup {

	public static final String GROUP_NAME = "ModeChainFilterRandomThreshold";
	
	public static final String MAX_CHAINS_THRESHOLD = "maxChainsThreshold";
	
	private int maxChainsThreshold = 256;
	
	public ModeChainFilterRandomThresholdConfigGroup() {
		super(GROUP_NAME);
	}

	@StringSetter(MAX_CHAINS_THRESHOLD)
	public void setMaxChainsThreshold(int maxChainsThreshold) {
		this.maxChainsThreshold = maxChainsThreshold;
	}

	@StringGetter(MAX_CHAINS_THRESHOLD)
	public int getMaxChainsThreshold() {
		return maxChainsThreshold;
	}
}
