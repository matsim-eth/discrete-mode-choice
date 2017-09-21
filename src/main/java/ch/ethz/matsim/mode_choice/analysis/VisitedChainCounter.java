package ch.ethz.matsim.mode_choice.analysis;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.pt.PtConstants;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class VisitedChainCounter implements IterationEndsListener, StartupListener {
	@Inject
	Population population;

	@Inject
	OutputDirectoryHierarchy hierarchy;

	final private Map<Id<Person>, Set<Integer>> observedModeChains = new HashMap<>();

	private List<String> getModeChain(Plan plan) {
		return TripStructureUtils.getTrips(plan, new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE)).stream()
				.map(t -> t.getLegsOnly().get(0).getMode()).collect(Collectors.toList());
	}

	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(hierarchy.getIterationFilename(event.getIteration(), "chain_counts.txt"))));

			for (Person person : population.getPersons().values()) {
				List<String> modeChain = getModeChain(person.getSelectedPlan());
				int modeChainHash = String.join(":", modeChain).hashCode();
				observedModeChains.get(person.getId()).add(modeChainHash);
				writer.write(String.format("%s %d", person.getId().toString(),
						observedModeChains.get(person.getId()).size()));
				writer.flush();
			}
			
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyStartup(StartupEvent event) {
		for (Person person : population.getPersons().values()) {
			observedModeChains.put(person.getId(), new HashSet<>());
		}
	}
}
