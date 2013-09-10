package game;
import org.newdawn.slick.util.pathfinding.AStarHeuristic;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;


public class Heuristic implements AStarHeuristic {

	@Override
	public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx,
			int ty) {
		
		return 0;
	}

}
