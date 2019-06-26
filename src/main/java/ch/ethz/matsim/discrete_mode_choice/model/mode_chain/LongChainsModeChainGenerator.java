package ch.ethz.matsim.discrete_mode_choice.model.mode_chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class LongChainsModeChainGenerator implements ModeChainGenerator {
	
	final private List<String> availableModes;

	final private int numberOfTrips;
	final private int numberOfModes;

	final private int maximumAlternatives;

	private int index = 0;
	
	

	public LongChainsModeChainGenerator(Collection<String> availableModes, int numberOfTrips) {
		this.availableModes = new ArrayList<>(availableModes);
		this.numberOfModes = availableModes.size();
		this.numberOfTrips = numberOfTrips;
		this.maximumAlternatives = ArithmeticUtils.pow(numberOfModes, numberOfTrips);
		
	}

	public int getNumberOfAlternatives() {
		return maximumAlternatives;
	}

	@Override
	public boolean hasNext() {
		return index < maximumAlternatives;
	}

	@Override
	public List<String> next() {
		if (!hasNext()) {
			throw new IllegalStateException();
		}

		List<String> chain = new ArrayList<>(numberOfTrips);
		

		for (int k = 0; k < numberOfTrips; k++) {
			
			//chain.add(availableModes.get());
		}

		index++;

		return chain;
	}

	static public class Factory implements ModeChainGeneratorFactory {
		@Override
		public ModeChainGenerator createModeChainGenerator(Collection<String> modes, Person person,
				List<DiscreteModeChoiceTrip> trips) {
			return new LongChainsModeChainGenerator(modes, trips.size());
		}
	}

}
