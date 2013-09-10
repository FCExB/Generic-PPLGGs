package ga;


public class GeneticAlgorithm<T extends Individual<T>> {
	private Population<T> population;
	private Fitness<T> fitnessFunction;
	private T fittestIndividual;
    private int generations;
    
    
	
	public GeneticAlgorithm(Fitness<T> fitness, Creator<T> creator, int populationSize, int generations) {
	    fittestIndividual = null;
		population = new Population<T>(populationSize, creator);
		fitnessFunction = fitness;
		this.generations = generations;
		runGA();
	}

    private void runGA() {
        int generation = 1;
        while (generation<=this.generations) {
            System.out.println("Generation "+generation);
            population.evaluate(fitnessFunction);
            T populationFittest = population.getFittestIndividual();
            if (generation == 1)
                fittestIndividual = populationFittest; 
            else if (populationFittest.getFitness() > fittestIndividual.getFitness()) {
            	fittestIndividual = populationFittest;
            }
            population.nextGeneration();
            generation++;
        }
    }

    public T getFittestIndividual() {
        return fittestIndividual;
    }
    
    public Population<T> getPopulation() {
        return population;
    }
}
