package gg;


public abstract class GameInterface<T> implements Runnable {
	
	public abstract void setMap(T level);
	
	@Override
	public abstract void run();
	
	public abstract void close();
}
