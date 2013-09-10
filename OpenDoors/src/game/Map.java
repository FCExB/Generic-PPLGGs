package game;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import world.Tile;


public class Map implements TileBasedMap, Iterable<Tile> {
	
	private int height = 14;
	private int width = 80;
	
	private List<Tile> keys;
	
	private final Tile[][] tiles;
	
	public Map(Tile[][] tiles, List<Tile> keys){
		this.tiles = tiles;
		height = tiles[0].length;
		width = tiles.length;
		this.keys = keys;
	}
	
	public List<Tile> getKeys(){
		return keys;
	}
	
	@Override
	public boolean blocked(PathFindingContext context, int x, int y) {
		if(x < 0 || y < 0 || x >= width || y >=  height){
			return true;
		}
		
		return tiles[x][y].isSolid();
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		return 1;
	}

	@Override
	public int getHeightInTiles() {
		return height;
	}

	@Override
	public int getWidthInTiles() {
		return width;
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		
	}
	
	public Tile getTile(int x, int y){
		return tiles[x][y];
	}

	@Override
	public Iterator<Tile> iterator() {
		return new MapIterator();
	}
	
	public class MapIterator implements Iterator<Tile>{
		
		private int nextX, nextY;
		
		@Override
		public boolean hasNext() {
			return nextX < width && nextY < height;
		}

		@Override
		public Tile next() {
			Tile result = tiles[nextX][nextY];
			
			if(nextX == width - 1){
				nextX = 0;
				nextY++;
			} else{
				nextX++;
			}
			
			return result;
		}

		@Override
		public void remove() {
			
		}
		
	}
	
	public void keyPressed(int x, int y){
		keys.clear();
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				Tile tile = tiles[i][j]; 
				if(tile.getType() == 5 || tile.getType() == 6) {
					tiles[i][j] = new Tile(0, false, tile.x,tile.y);
				}
			}
		}
	}

}
