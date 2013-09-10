package ga;

public abstract class Individual<T extends Individual<T>> {
	
	protected double fitness;
	
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double newFitness) {
		fitness = newFitness;
	}
	
	public abstract T copy();
	public abstract void mutate();
}
