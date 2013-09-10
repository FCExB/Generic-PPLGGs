package ga;

public interface Creator<T extends Individual<T>> {
	
	public T createRandom();
	public T mate(T parent1, T parent2);
	public int getTournamentSize();
	public float getMutationRate();
	public float getCrossOverRate();
}
