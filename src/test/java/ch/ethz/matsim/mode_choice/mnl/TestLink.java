package ch.ethz.matsim.mode_choice.mnl;

import java.util.Set;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.utils.objectattributes.attributable.Attributes;

public class TestLink implements Link {
	final private Coord coord;
	
	public TestLink(Coord coord) {
		this.coord = coord;
	}
	
	@Override
	public Coord getCoord() {
		return coord;
	}

	@Override
	public Id<Link> getId() {
		return null;
	}

	@Override
	public Attributes getAttributes() {
		return null;
	}

	@Override
	public boolean setFromNode(Node node) {
		return false;
	}

	@Override
	public boolean setToNode(Node node) {
		return false;
	}

	@Override
	public Node getToNode() {
		return null;
	}

	@Override
	public Node getFromNode() {
		return null;
	}

	@Override
	public double getLength() {
		return 0;
	}

	@Override
	public double getNumberOfLanes() {
		return 0;
	}

	@Override
	public double getNumberOfLanes(double time) {
		return 0;
	}

	@Override
	public double getFreespeed() {
		return 0;
	}

	@Override
	public double getFreespeed(double time) {
		return 0;
	}

	@Override
	public double getCapacity() {
		return 0;
	}

	@Override
	public double getCapacity(double time) {
		return 0;
	}

	@Override
	public void setFreespeed(double freespeed) {
		
	}

	@Override
	public void setLength(double length) {
		
	}

	@Override
	public void setNumberOfLanes(double lanes) {
		
	}

	@Override
	public void setCapacity(double capacity) {
		
	}

	@Override
	public void setAllowedModes(Set<String> modes) {
		
	}

	@Override
	public Set<String> getAllowedModes() {
		return null;
	}

	@Override
	public double getFlowCapacityPerSec() {
		return 0;
	}

	@Override
	public double getFlowCapacityPerSec(double time) {
		return 0;
	}

}
