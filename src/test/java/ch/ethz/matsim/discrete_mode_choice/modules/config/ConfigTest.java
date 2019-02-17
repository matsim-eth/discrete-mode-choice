package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
