package ch.ethz.matsim.discrete_mode_choice.model.nested;

import java.util.Collection;

public interface Nest {
	String getName();

	Collection<Nest> getChildren();

	double getScaleParameter();
}
