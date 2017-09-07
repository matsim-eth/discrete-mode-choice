package ch.ethz.matsim.mode_choice.mnl.prediction;

import org.matsim.core.controler.events.ReplanningEvent;
import org.matsim.core.controler.listener.ReplanningListener;

public class PredictionCacheCleaner implements ReplanningListener {
	final private PredictionCache cache;
	
	public PredictionCacheCleaner(PredictionCache cache) {
		this.cache = cache;
	}
	
	@Override
	public void notifyReplanning(ReplanningEvent event) {
		cache.clear();
	}
}
