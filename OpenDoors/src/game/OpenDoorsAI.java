package game;

import gg.GameAIInterface;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;

import world.Tile;

public class OpenDoorsAI implements GameAIInterface<Map> {

	@Override
	public double playLevel(Map toPlay, boolean visuals) {
		int w = toPlay.getWidthInTiles();
		int h = toPlay.getHeightInTiles();
		int noSolid = 0;
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++)
				noSolid += toPlay.blocked(null, x, y) ? 1 : 0;
		double solidRatio = (double) noSolid / (double) (w * h);
		
		AStarPathFinder pathfinder = new AStarPathFinder(toPlay, 500, false);
		
		return pathLengthFromLocation(0,0,pathfinder,toPlay);
	}

	private int pathLengthFromLocation(int x, int y,
			AStarPathFinder pathfinder, Map toPlay) {
		Path path = pathfinder.findPath(null, x, y,
				toPlay.getWidthInTiles() - 1, 0);

		if (path != null) {
			return path.getLength();
		}

		Tile closestKey = null;
		int shortestLength = Integer.MAX_VALUE;

		for (Tile key : toPlay.getKeys()) {
			path = pathfinder.findPath(null, x, y, key.x, key.y);

			if (path != null) {
				int length = path.getLength();
				if (length < shortestLength) {
					closestKey = key;
					shortestLength = length;
				}
			}
		}

		if (closestKey != null) {
			toPlay.keyPressed(closestKey.x, closestKey.y);
			int fromHere = 	pathLengthFromLocation(closestKey.x, closestKey.y,
					pathfinder, toPlay);		
			if(fromHere == 0)
				return 0;
			
			return shortestLength + fromHere;
		}
		
		return 0;

	}

}
