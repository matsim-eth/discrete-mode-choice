package ch.ethz.matsim.discrete_mode_choice.model.nested;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultNest implements Nest {
	private final String name;
	private final double scaleParameter;

	private final Collection<Nest> children = new LinkedList<>();

	public DefaultNest(String name, double scaleParameter) {
		this.name = name;
		this.scaleParameter = scaleParameter;
	}

	public String getName() {
		return name;
	}

	@Override
	public Collection<Nest> getChildren() {
		return children;
	}

	public void addChild(DefaultNest child) {
		children.add(child);
	}

	@Override
	public double getScaleParameter() {
		return scaleParameter;
	}
}
