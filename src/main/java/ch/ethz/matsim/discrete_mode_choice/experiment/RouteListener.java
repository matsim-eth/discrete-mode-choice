package ch.ethz.matsim.discrete_mode_choice.experiment;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.listener.IterationEndsListener;

public class RouteListener implements LinkEnterEventHandler, IterationEndsListener {
	private final Logger logger = Logger.getLogger(RouteListener.class);

	private final List<Integer> numberOfAgentsA = new LinkedList<>();
	private final List<Integer> numberOfAgentsB = new LinkedList<>();
	private final List<Integer> differentialError = new LinkedList<>();

	int currentNumberOfAgentsA = 0;
	int currentNumberOfAgentsB = 0;

	public RouteListener() {
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		String linkId = event.getLinkId().toString();

		if (linkId.equals("A")) {
			currentNumberOfAgentsA++;
		} else if (linkId.equals("B")) {
			currentNumberOfAgentsB++;
		}
	}

	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		numberOfAgentsA.add(currentNumberOfAgentsA);
		numberOfAgentsB.add(currentNumberOfAgentsB);

		int currentDifferentialError = 0;

		for (int i = 1; i < numberOfAgentsA.size(); i++) {
			currentDifferentialError += Math.abs(numberOfAgentsA.get(i) - numberOfAgentsA.get(i - 1));
			currentDifferentialError += Math.abs(numberOfAgentsB.get(i) - numberOfAgentsB.get(i - 1));
		}

		differentialError.add(currentDifferentialError);

		logger.warn(String.format("%d) A: %d ; B: %d ; Diff. Error: %d", event.getIteration(), currentNumberOfAgentsA,
				currentNumberOfAgentsB, currentDifferentialError));

		currentNumberOfAgentsA = 0;
		currentNumberOfAgentsB = 0;
	}
}