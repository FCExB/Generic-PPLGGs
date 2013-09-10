package ga;

import java.util.ArrayList;
import java.util.List;

public class Population<T extends Individual<T>> {
	private List<T> individuals;
	private int size;
	
	Creator<T> creator;
	
	public Population(int size, Creator<T> creator) {
		individuals = new ArrayList<T>(size);
		this.size = size;
		this.creator = creator;
		
		for (int i=0; i<size; i++) {
			individuals.add(creator.createRandom());
		}
	}
	
	public List<T> getIndividuals() {
	    return individuals;
	}
	
	public void setIndividualFitness(int i, double fitness) {
	    individuals.get(i).setFitness( fitness );
	}
	
	public float evaluate(Fitness<T> fitnessFunction) {
		float totalFit = 0;
		for (int i=0; i<size; i++) {
			T ind = individuals.get(i);
			double fit = fitnessFunction.evaluate(ind);
			ind.setFitness(fit);
			totalFit += fit;
		}
		return totalFit/size;
	}
	
	//remove fitnesses lower than or equal to 0
	public void nextGeneration() {
	    ArrayList<T> genepool = new ArrayList<T>();
	    for (int i=0; i<individuals.size(); i++) {
	        if (individuals.get( i ).getFitness()>0)
	            genepool.add(individuals.get(i));
	    }
	    nextGeneration(genepool);
	}
	
	public void nextGeneration(ArrayList<T> genepool) {
		if(genepool.isEmpty()){
			for (int i=0; i<individuals.size(); i++) {
			    if (Math.random() < creator.getMutationRate())
			        individuals.get(i).mutate();
			}
			return;
		}
		
		ArrayList<T> newPop = new ArrayList<T>(genepool.size());
		
		for (int i=0; i<individuals.size(); i++) {
			T parent1 = Reproduction.tournamentSelection(genepool, creator);
			if (Math.random() >= creator.getCrossOverRate()) //new = copy of old individual
				newPop.add(parent1.copy()); 
			else { //new = offspring of two old individuals
				T parent2 = Reproduction.tournamentSelection(genepool, creator);
				newPop.add(creator.mate(parent1,parent2));
			}
		}
		individuals = newPop;
		for (int i=0; i<individuals.size(); i++) {
		    if (Math.random() < creator.getMutationRate())
		        individuals.get(i).mutate();
		}
	}
	
	public void replaceIndividual(int index, T ind) {
	    individuals.set( index, ind );
	}

    public T getFittestIndividual() {
        double bestFitness = -1; 
        T fittestInd = null;
        for (int i=0; i<size; i++) {
            double indFit = individuals.get(i).getFitness(); 
            if (indFit>bestFitness) {
                bestFitness = indFit;
                fittestInd = individuals.get(i);
            }
        }
        return fittestInd;
    }

    public void setIndividuals(List<T> newIndividuals ) {
        individuals = newIndividuals;
    }
	
}
