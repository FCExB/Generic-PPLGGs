package world;

import game.Map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

import reuse.Camera;
import reuse.Player;

public class World {
	private static final int INITIAL_WIDTH = 1;
	private static final int INITIAL_HEIGHT = 1;
	private static final int LENGTH_OF_NIGHT_DAY = 11000;
	private static final int SUNRISE_SET_LENGTH = 6000;

	private final int tileSize = 16;

	private float brightness = 1;
	private int timeSince;
	private boolean isDay = true;
	private boolean transitioning = false;

	private final Map map;

	private Player player;

	public World(Map map, Camera camera) throws SlickException {

		this.map = map;
	}
	
	public void addPlayer(Player player){
		this.player = player;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void update(int deltaT) {

		if (transitioning) {

			float change = (float) deltaT / (float) SUNRISE_SET_LENGTH;

			if (isDay) {
				brightness -= change;
			} else {
				brightness += change;
			}

			if (timeSince < SUNRISE_SET_LENGTH) {
				timeSince += deltaT;
			} else {
				timeSince = 0;
				transitioning = false;
				isDay = !isDay;
			}

		} else {
			if (timeSince < LENGTH_OF_NIGHT_DAY) {
				timeSince += deltaT;
			} else {
				timeSince = 0;
				transitioning = true;
			}
		}
	}

	public void render(Camera camera) {
		float zScaler = camera.zScaler();

		for (Tile tile : map) {

			int tileX = tile.x * tileSize;
			int tileY = tile.y * tileSize;

			if (camera.inRenderView(tileX, tileY)) {

				// Draw floor tile
				int xLocation = (tileX - camera.getX()) + 400;
				float yLocation = (tileY - camera.getY()) * zScaler + 300;
				float xScale = 1;
				float yScale = zScaler;

				tile.getImage().draw(xLocation, yLocation, tileSize * xScale,
						tileSize * yScale);
			}
		}
	}

	public boolean isSolid(int x, int y) {
		return map.blocked(null, x, y);
	}
	
	public boolean finished(){
		return player.getX() == map.getWidthInTiles()-1 && player.getY() == 0;
	}
	
	public Tile getTileAt(int x, int y){
		return map.getTile(x, y);
	}
}
