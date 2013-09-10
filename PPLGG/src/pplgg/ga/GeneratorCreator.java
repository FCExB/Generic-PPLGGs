package pplgg.ga;

import ga.Creator;
import gg.GameMapConverter;

import java.util.ArrayList;

import pplgg.AgentParams;
import pplgg.Generator;

public class GeneratorCreator<T> implements Creator<GeneratorIndividual<T>> {

	public static int tournamentSize = 5;
	public static float mutationRate = 0.5f;
	public static float crossOverRate = 0.9f;
	
	private final GameMapConverter<T> converter;
	
	public GeneratorCreator(GameMapConverter<T> converter){
		this.converter = converter;
	}
	
	@Override
	public GeneratorIndividual<T> createRandom() {
        return new GeneratorIndividual<T>(new Generator<T>(converter));
	}

	@Override
	public GeneratorIndividual<T> mate(GeneratorIndividual<T> p1,
			GeneratorIndividual<T> p2) {
		GeneratorIndividual<T> parent1 = p1.copy();
		GeneratorIndividual<T> parent2 = p2.copy();
		int size1 = parent1.getGenerator().getNoAgents();
		int size2 = parent2.getGenerator().getNoAgents();
		ArrayList<AgentParams> childAgentComposition = new ArrayList<AgentParams>();

		// determine randomly what part of agents should come from which agent
		float agentRatio = (float) Math.random();
		int amountFromParent1 = Math.round(agentRatio * (size1));
		int amountFromParent2 = Math.round((1 - agentRatio) * (size2));
		for (int i = 0; i < amountFromParent1; i++)
			childAgentComposition.add(parent1.getGenerator()
					.extractRandomAgent());
		for (int i = 0; i < amountFromParent2; i++)
			childAgentComposition.add(parent2.getGenerator()
					.extractRandomAgent());
		// construct the new individual
		return new GeneratorIndividual<T>(new Generator<T>(childAgentComposition, converter));
	}

	@Override
	public int getTournamentSize() {
		return tournamentSize;
	}

	@Override
	public float getMutationRate() {
		return mutationRate;
	}

	@Override
	public float getCrossOverRate() {
		return crossOverRate;
	}

}
