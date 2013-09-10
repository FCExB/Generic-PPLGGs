package ga;

import java.util.ArrayList;

public class Reproduction<T extends Individual<T>> {

	public static <T extends Individual<T>> T tournamentSelection(ArrayList<T> genePool, Creator<T> creator) {

		// select competitors at random, possibly selecting the same more than
		// once
		ArrayList<T> competitors = new ArrayList<T>();
		for (int i = 0; i < creator.getTournamentSize(); i++) {

			competitors
					.add(genePool.get((int) (Math.random() * genePool.size())));
		}

		// find the most fit and return it
		double bestFit = -1;
		T bestInd = null;
		for (int i = 0; i < competitors.size(); i++) {
			double indFit = competitors.get(i).getFitness();
			if (indFit > bestFit) {
				bestFit = indFit;
				bestInd = competitors.get(i);
			}
		}
		return bestInd;
	}
}
