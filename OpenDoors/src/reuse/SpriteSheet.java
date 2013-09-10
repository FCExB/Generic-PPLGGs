package reuse;

import org.newdawn.slick.Image;

public class SpriteSheet extends org.newdawn.slick.SpriteSheet {

	private final int spriteWidth, spriteHeight, numSpritesWide;

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public SpriteSheet(Image image, int spriteWidth, int spriteHeight) {
		super(image, spriteWidth, spriteHeight);
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;

		numSpritesWide = width / spriteWidth;
	}

	public SpriteSheet(Image image, int spriteWidth, int spriteHeight,
			int numSpritesWide) {
		super(image, spriteWidth, spriteHeight);
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;

		this.numSpritesWide = numSpritesWide;

	}

	public int getNumberOfSpritesWide() {
		return numSpritesWide;
	}

}
