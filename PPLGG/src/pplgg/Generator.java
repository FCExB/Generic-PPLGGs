package pplgg;

import gg.GameMapConverter;

import java.io.Serializable;
import java.util.ArrayList;

import agents.GAgent;

public class Generator<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2736800572059010855L;
	
	public static final float maximumSpawnRadius = 60;
	public static final int maximumSpawnTime = 200;
	public static final int minimumTokens = 10;
	public static final int maximumTokens = 80;
	public static final int maximumWaitingTime = 5;
	private static final int maxGenerationSteps = 500;
	public static int width = 80;
	public static int height = 14;

	public static int[] testSubjects = { 5, 12 };

	private ArrayList<AgentParams> agentComposition;

	private GameMapConverter<T> converter;

	public Generator() {

	}

	// public static <T> Generator<T> randomGenerator(GameMapConverter<T>
	// converter) {
	// int noAgents = (int) (24+20 * Math.random());
	// ArrayList<AgentParams> agentComposition = new
	// ArrayList<AgentParams>(noAgents);
	// for (int i=0; i<noAgents; i++) {
	// agentComposition.add( i, Generator.randomAgentParams(converter));
	// }
	//
	// return new Generator<T>(agentComposition, converter);
	// }

	public Generator(GameMapConverter<T> converter) {
		this.converter = converter;

		int noAgents = (int) (24 + 20 * Math.random());
		agentComposition = new ArrayList<AgentParams>(noAgents);
		for (int i = 0; i < noAgents; i++) {
			agentComposition.add(i, randomAgentParams());
		}
	}

	public Generator(ArrayList<AgentParams> agentComposition,
			GameMapConverter<T> converter) {
		this.agentComposition = agentComposition;
		this.converter = converter;
	}
	
	private AgentParams randomAgentParams() {
		AgentParams parameters = new AgentParams();
		parameters.pos = new Position((int) (width * Math.random()),
				(int) (height * Math.random()));
		parameters.spawnRadius = maximumSpawnRadius * Math.random();
		parameters.tokens = (int) (minimumTokens + (maximumTokens - minimumTokens)
				* Math.random());
		parameters.spawnTime = (int) (1 + maximumSpawnTime * Math.random());
		parameters.waitingPeriod = (int) (1 + (maximumWaitingTime - 1)
				* Math.random());
		parameters.agentType = new GAgent(converter);
		return parameters;
	}

	public Map generateMap(int timeToWait) {
		MapManager mapManager = new MapManager(width, height);
		ArrayList<AgentParams> spawnEvents = (ArrayList<AgentParams>) agentComposition
				.clone();
		ArrayList<AgentParams> removeList = new ArrayList<AgentParams>();
		int steps = 0;
		while (mapManager.agentsLeft() || spawnEvents.size() > 0) {
			// spawn the agents according to their spawn time
			for (AgentParams agentPar : spawnEvents) {
				if (agentPar.spawnTime == steps) {
					// pick a position inside the spawn circle
					double direction = Math.random() * 2.0 * Math.PI;
					double xDistance = Math.random() * agentPar.spawnRadius;
					double yDistance = Math.random() * agentPar.spawnRadius
							/ (width / height);
					int xPos = Math.max(
							Math.min(
									(int) (agentPar.pos.x + xDistance
											* Math.cos(direction)), width - 1),
							0);
					int yPos = Math
							.max(Math.min((int) (agentPar.pos.y + yDistance
									* Math.sin(direction)), height - 1), 0);
					Position spawnPos = new Position(xPos, yPos);

					// intialize the agent
					agentPar.agentType.clone().initialize(mapManager,
							agentPar.tokens, spawnPos, agentPar.waitingPeriod);
					removeList.add(agentPar);
				}
			}
			for (AgentParams toRemove : removeList) {
				spawnEvents.remove(toRemove);
			}
			removeList.clear();
			mapManager.performStep();

			if (timeToWait > 0) {
				System.out.println(mapManager.showMap());
				try {
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			steps++;
			if (steps > maxGenerationSteps)
				break;
		}
		return mapManager.extractMap();
	}

	// removes and returns a random agent, used for mutation
	public AgentParams extractRandomAgent() {
		int index = (int) (Math.random() * agentComposition.size());
		return agentComposition.remove(index);
	}

	public void addRandomAgent() {
		AgentParams parameters = new AgentParams();
		parameters.pos = new Position((int) (width * Math.random()),
				(int) (height * Math.random()));
		parameters.spawnRadius = maximumSpawnRadius * Math.random();
		parameters.tokens = (int) (minimumTokens + (maximumTokens - minimumTokens)
				* Math.random());
		parameters.spawnTime = (int) (1 + maximumSpawnTime * Math.random());
		parameters.waitingPeriod = (int) (1 + (maximumWaitingTime - 1)
				* Math.random());
		parameters.agentType = new GAgent(converter);

		addAgent(parameters);
	}

	// adds an agent, used for mutation
	public void addAgent(AgentParams newAgent) {
		agentComposition.add(newAgent);
	}

	// returns a copy, used for genetic algorithm
	public Generator<T> copy() {
		ArrayList<AgentParams> parametersCopy = new ArrayList<AgentParams>(
				agentComposition.size());
		for (int i = 0; i < agentComposition.size(); i++)
			parametersCopy.add((AgentParams) agentComposition.get(i).clone());
		return new Generator<T>(parametersCopy, this.converter);
	}

	public int getNoAgents() {
		return agentComposition.size();
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < agentComposition.size(); i++) {
			s += "Agent " + (i + 1) + ": ";
			s += agentComposition.get(i).agentType + "\n";
		}
		return s;
	}
}
