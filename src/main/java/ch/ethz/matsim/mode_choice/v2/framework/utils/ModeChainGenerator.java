package ch.ethz.matsim.mode_choice.v2.framework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.ArithmeticUtils;

public class ModeChainGenerator implements Iterator<List<String>> {
	final private List<String> modes;

	final private int numberOfTrips;
	final private int numberOfModes;

	final private int maximumAlternatives;

	private int index = 0;

	public ModeChainGenerator(Collection<String> modes, int numberOfTrips) {
		this.modes = new ArrayList<>(modes);
		this.numberOfModes = modes.size();
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
		int copy = index;

		for (int k = 0; k < numberOfTrips; k++) {
			chain.add(modes.get(copy % numberOfModes));
			copy -= copy % numberOfModes;
			copy /= numberOfModes;
		}

		index++;

		return chain;
	}
}
