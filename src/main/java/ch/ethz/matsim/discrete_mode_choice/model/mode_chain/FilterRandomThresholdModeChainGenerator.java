package ch.ethz.matsim.discrete_mode_choice.model.mode_chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.gbl.MatsimRandom;

import com.google.inject.Inject;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.config.ModeChainFilterRandomThresholdConfigGroup;

public class FilterRandomThresholdModeChainGenerator implements ModeChainGenerator {

	final private List<String> availableModes;
	final private int numberOfTrips;
	final private int numberOfModes;
	final private int maximumAlternatives;
	final private int alternatives;
	final private boolean thresholdExceeded;
	private int index = 0;
	
	//private static Logger log = Logger.getLogger(FilterRandomThresholdModeChainGenerator.class);
	
	public FilterRandomThresholdModeChainGenerator(Collection<String> availableModes, int numberOfTrips, int maximumAlternatives) {
		this.availableModes = new ArrayList<>(availableModes);
		this.numberOfModes = availableModes.size();
		this.numberOfTrips = numberOfTrips;
		this.maximumAlternatives = maximumAlternatives;
	   
		int alt = numberOfTrips < 10? (int)Math.pow(numberOfModes, numberOfTrips): (this.maximumAlternatives+1);
		this.alternatives = alt  < this.maximumAlternatives ? alt: this.maximumAlternatives;
		this.thresholdExceeded = alt > this.maximumAlternatives ? true : false;
	}

	public int getNumberOfAlternatives() {
		return alternatives;
	}

	@Override
	public boolean hasNext() {
		return index < alternatives;
	}

	@Override
	public List<String> next() {
		
		if (!hasNext()) {
			throw new IllegalStateException();
		}

		List<String> chain = new ArrayList<>(numberOfTrips);
		int copy = index;

		if(!thresholdExceeded) {
			System.out.println(maximumAlternatives);
			for (int k = 0; k < numberOfTrips; k++) {
				chain.add(availableModes.get(copy % numberOfModes));
				copy -= copy % numberOfModes;
				copy /= numberOfModes;
			}
		}
		else {
			for (int k = 0; k < numberOfTrips; k++) {
				chain.add(availableModes.get(MatsimRandom.getRandom().nextInt(numberOfModes)));
			}
		}
		index++;
		return chain;
	}

	static public class Factory implements ModeChainGeneratorFactory {
	   
		ModeChainFilterRandomThresholdConfigGroup modeChainGeneratorConfig;
		
		@Inject
		public Factory(ModeChainFilterRandomThresholdConfigGroup modeChainGeneratorConfig) {
			this.modeChainGeneratorConfig = modeChainGeneratorConfig;
		}

		@Override
		public ModeChainGenerator createModeChainGenerator(Collection<String> modes, Person person,
				List<DiscreteModeChoiceTrip> trips) {
			if(true) {
				return new FilterRandomThresholdModeChainGenerator(modes, trips.size(),modeChainGeneratorConfig.getMaxChainsThreshold());
			}
			else {
				throw new IllegalStateException("the module ModeChainFilterRandomThreshold not found in Config file. Insert the module in the Config file or change modeChainGenerator in the DiscreteModeChoice module");
			}
			
		}
	}

	
}
