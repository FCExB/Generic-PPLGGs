package gg;

import pplgg.Generator;
import pplgg.PPLGG;
import gui.PPLGGGUI;

public class PPLGGFront<T> {
	private final GameAIInterface<T> aiInterface;
	private final GameMapConverter<T> levelConverter;
	private final GameInterface<T> gameWindow;

	public PPLGGFront(GameAIInterface<T> aiInterface,
			GameMapConverter<T> levelConverter, GameInterface<T> gameWindow, int mapWidth, int mapHeight) {
		this.aiInterface = aiInterface;
		this.levelConverter = levelConverter;
		this.gameWindow = gameWindow;
		
		Generator.width = mapWidth;
		Generator.height = mapHeight;
	}
	
	public void runPPLGGGUI() {
		new PPLGGGUI<T>(this);
	}
	
	public void generatePLGs(int width, int height, int populationSize, int generations, int mapSampleSize, int numGens) {
		PPLGG.run(this, width, height, populationSize, generations, mapSampleSize, numGens);
	}
	
	public GameAIInterface<T> getAI(){
		return aiInterface;
	}
	
	public GameMapConverter<T> getConverter(){
		return levelConverter;
	}
	
	public GameInterface<T> getGameRunnable(){
		return gameWindow;
	}
}
