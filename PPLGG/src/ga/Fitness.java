package ga;


public interface Fitness<T extends Individual<T>> {
	public abstract double evaluate(T genotype);
}
