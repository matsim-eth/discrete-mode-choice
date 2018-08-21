package ch.ethz.matsim.mode_choice.v2.mode_availability;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.population.PersonUtils;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public class LicenseAndOwnershipModeAvailability extends DefaultModeAvailability {
	final private String CAR_MODE = "car";

	public LicenseAndOwnershipModeAvailability(Collection<String> modes) {
		super(modes);
	}

	@Override
	public Collection<String> getAvailableModes(List<ModeChoiceTrip> trips) {
		if (trips.size() > 0) {
			Person person = trips.get(0).getPerson();

			if (!PersonUtils.hasLicense(person) || PersonUtils.getCarAvail(person).equals("never")) {
				return super.getAvailableModes(trips).stream().filter(m -> !CAR_MODE.equals(m))
						.collect(Collectors.toSet());
			}
		}

		return super.getAvailableModes(trips);
	}
}
