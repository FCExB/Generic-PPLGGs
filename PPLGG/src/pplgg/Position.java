package pplgg;

import java.io.Serializable;

public class Position implements Serializable{
    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public float x,y;
}
