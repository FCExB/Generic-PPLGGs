package world;

import org.newdawn.slick.Image;

import reuse.Assets;

public class Tile {

	private final int type;
	private final boolean solid;
	public int x,y;

	public Tile(int type, boolean solid, int x, int y) {
		this.type = type;
		this.solid = solid;
		this.x = x;
		this.y = y;
	}

	public Image getImage() {
		if (type == 1)
			return Assets.FENCE;
		else if (type == 2)
			return Assets.TORCH;
		else if (type == 3)
			return Assets.ROCK;
		else if (type == 4)
			return Assets.FENCE;
		else if (type == 5)
			return Assets.KEY;
		else if (type == 6)
			return Assets.DOOR;
		else
			return Assets.TILE_ONE;
	}
	
	public boolean isSolid(){
		return solid;
	}
	
	public int getType(){
		return type;
	}
}
