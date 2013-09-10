package reuse;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import world.World;

public class Player {

	int x;
	int y;

	private int width;
	private int height;

	private World world;

	private Image image;

	public Player(int x, int y, World world, Image image) {

		this.x = x;
		this.y = y;

		this.world = world;

		this.image = image;
		height = image.getHeight();
		width = image.getWidth();
	}

	public void render(Camera camera, Graphics g) {
		if (camera.inRenderView(x * world.getTileSize(),
				y * world.getTileSize())) {

			float zScaler = camera.zScaler();
			float otherScaler = camera.otherScaler();

			int drawX = Math.round(x * world.getTileSize() - camera.getX())
					+ 400 - width / 2 + world.getTileSize() / 2;
			int drawY = Math.round((y * world.getTileSize() - camera.getY())
					* zScaler + 300 - height * otherScaler
					+ world.getTileSize() / 2);
			float xScale = 1;
			float yScale = otherScaler;

			image.draw(drawX, drawY, width * xScale, height * yScale);
		}
	}

	private int timer;

	public void update(GameContainer container, int delta) {
		Input input = container.getInput();

		int newX = x;
		int newY = y;

		timer += delta;

		if (timer > 50) {
			timer = 0;
			
			if (input.isKeyDown(Input.KEY_W)) {
				newY--;

			} else if (input.isKeyDown(Input.KEY_S)) {
				newY++;

			} else if (input.isKeyDown(Input.KEY_D)) {
				newX++;
			} else if (input.isKeyDown(Input.KEY_A)) {
				newX--;
			}
		}

		if (!world.isSolid(newX, newY)) {
			x = newX;
			y = newY;
		}
	}

	public int getX(){
		return x;
	}
	
	public int getY() {
		return y;
	}
}
