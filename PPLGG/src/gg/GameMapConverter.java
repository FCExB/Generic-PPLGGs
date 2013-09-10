package gg;

import java.awt.Color;
import java.io.Serializable;

import pplgg.Map;

public interface GameMapConverter<T> extends Serializable {
	public T convertMap(Map pplggMap);
	public Color colorReprisentation(int value);
	public int numOfTileTypes();
	public boolean isTileTypeSolid(int value);
}
