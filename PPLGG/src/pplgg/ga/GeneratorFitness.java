package pplgg.ga;

import ga.Fitness;
import pplgg.Generator;
import pplgg.Map;

public class GeneratorFitness<T> implements Fitness<GeneratorIndividual<T>> {

    private final int sampleSize; 
    private final MapFitness<T> mapFitness;
    
    public GeneratorFitness(int sampleSize, MapFitness<T> mapFitness){
    	this.sampleSize = sampleSize;
    	this.mapFitness = mapFitness;
    }

    @Override
    public double evaluate(GeneratorIndividual<T> ind) {
        Generator<T> gen = ind.getGenerator();

        double fitness = 0;
        for (int k=0; k<sampleSize; k++) {
            Map sampleMap = gen.generateMap(0);
            fitness += mapFitness.evaluateMap( sampleMap ) ;
        }
        fitness /= sampleSize;
		System.out.println("Fitness: " + fitness);
        return fitness;
    }
}
