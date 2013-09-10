package game;

import gg.GameMapConverter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import world.Tile;

public class MapConverter implements GameMapConverter<Map> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -619924234469363792L;

	@Override
	public Map convertMap(pplgg.Map myMap) {
		Tile[][] newMap = new Tile[myMap.getWidth()][myMap.getHeight()];
		List<Tile> keys = new ArrayList<Tile>();

		for (int x = 0; x < myMap.getWidth(); x++) {
			for (int y = 0; y < myMap.getHeight(); y++) {

				int type = myMap.getTerrain(x, y);

				if (type == 0) {
					newMap[x][y] = new Tile(type, false, x, y);
				} else if(type == 5){
					Tile t = new Tile(type, false, x, y);
					newMap[x][y] = t;
					keys.add(t);
				} else {
					newMap[x][y] = new Tile(type, true, x, y);
				}
			}

		}

		return new Map(newMap, keys);
	}

	private java.util.Map<Integer, Color> colorMap = new HashMap<Integer, Color>();

	@Override
	public Color colorReprisentation(int value) {
		// if(colorMap.containsKey(value)){
		// return colorMap.get(value);
		// }

		if(value == 6){
			return Color.red;
		}
		
		if(value == 5){
			return Color.yellow;
		}
		
		if (isTileTypeSolid(value)) {
			return Color.black;
		}

		return Color.white;
	}

	@Override
	public int numOfTileTypes() {
		return 7;
	}

	@Override
	public boolean isTileTypeSolid(int value) {
		return value != 0 && value != 5;
	}
}
