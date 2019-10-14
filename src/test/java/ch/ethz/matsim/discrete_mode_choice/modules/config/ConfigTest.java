package ch.ethz.matsim.discrete_mode_choice.modules.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;

public class ConfigTest {
	@Test
	public void testReadWriteConfig() {
		// Create config
		DiscreteModeChoiceConfigGroup dmcConfig = new DiscreteModeChoiceConfigGroup();
		Config config = ConfigUtils.createConfig(dmcConfig);

		dmcConfig.setSelector("unknown selector");
		dmcConfig.getCarModeAvailabilityConfig().setAvailableModes(Arrays.asList("abc", "def"));

		// Write config
		new ConfigWriter(config).write("test_config.xml");

		// Read in again
		DiscreteModeChoiceConfigGroup dmcConfig2 = new DiscreteModeChoiceConfigGroup();
		ConfigUtils.loadConfig("test_config.xml", dmcConfig2);

		assertEquals("unknown selector", dmcConfig2.getSelector());
		assertEquals(new HashSet<>(dmcConfig.getCarModeAvailabilityConfig().getAvailableModes()),
				new HashSet<>(Arrays.asList("abc", "def")));
	}

	@Test
	public void testReadWriteConfigMultipleTimes() throws IOException {
		DiscreteModeChoiceConfigGroup dmcConfig = new DiscreteModeChoiceConfigGroup();
		Config config1 = ConfigUtils.createConfig(dmcConfig);

		new ConfigWriter(config1).write("test_config1.xml");

		Config config2 = ConfigUtils.loadConfig("test_config1.xml", new DiscreteModeChoiceConfigGroup());
		new ConfigWriter(config2).write("test_config2.xml");

		Config config3 = ConfigUtils.loadConfig("test_config2.xml", new DiscreteModeChoiceConfigGroup());
		new ConfigWriter(config3).write("test_config3.xml");

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File("test_config3.xml"))));

		String line = null;

		int numberOfMultinomialLogit = 0;
		int numberOfVehicleContinuity = 0;

		while ((line = reader.readLine()) != null) {
			if (line.contains("parameterset") && line.contains("selector:MultinomialLogit")) {
				numberOfMultinomialLogit++;
			}

			if (line.contains("parameterset") && line.contains("tourConstraint:VehicleContinuity")) {
				numberOfVehicleContinuity++;
			}
		}

		reader.close();

		assertEquals(1, numberOfMultinomialLogit);
		assertEquals(1, numberOfVehicleContinuity);
	}
}
