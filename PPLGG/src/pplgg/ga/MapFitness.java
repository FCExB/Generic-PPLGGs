package pplgg.ga;

import gg.GameAIInterface;
import gg.GameMapConverter;
import pplgg.Map;

public class MapFitness<T> {
	private GameAIInterface<T> ai;
	private GameMapConverter<T> converter;

	public MapFitness(GameAIInterface<T> ai, GameMapConverter<T> converter) {
		this.ai = ai;
		this.converter = converter;
	}

	public double evaluateMap(Map pplggMap) {
		T toPlay = converter.convertMap(pplggMap);
		return ai.playLevel(toPlay, false);
	}
}
