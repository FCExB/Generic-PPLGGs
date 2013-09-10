package reuse;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class Camera {

	private int x;
	private int y;
	private int viewWidth;
	private int viewHeight;
	private float angle = 42;

	public Camera(int x, int y, int viewWidth, int viewHeight) {
		this.x = x;
		this.y = y;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.angle = 45;
	}

	public float zScaler() {
		return (float) Math.sin(angle * (Math.PI / 180));
	}

	public float otherScaler() {
		return (float) Math.cos(angle * (Math.PI) / 180);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getViewWidth() {
		return viewWidth;
	}

	public void setViewWidth(int viewWidth) {
		this.viewWidth = viewWidth;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	public boolean inRenderView(int thatX, int thatY) {

		int halfWidth = viewWidth / 2;
		float halfHeight = (viewHeight / zScaler()) / 2;

		return thatX >= x - halfWidth && thatX < x + halfWidth
				&& thatY >= y - halfHeight && thatY < y + halfHeight;
	}

	public void update(GameContainer gc, Player player, int deltaT) {

		float rotateSpeed = 0.06f;

		Input input = gc.getInput();

		if (input.isKeyDown(Input.KEY_Q) && angle < 90) {
			angle += rotateSpeed * deltaT;
		} else if (input.isKeyDown(Input.KEY_E) && angle > 2) {
			angle -= rotateSpeed * deltaT;
		}

		x = Math.round(player.x*16+8);
		y = Math.round(player.y*16+8);
	}
}
