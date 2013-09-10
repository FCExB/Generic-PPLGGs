package game;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import reuse.Assets;
import reuse.Camera;
import reuse.Player;
import world.World;


public class OpenDoorsLogic extends BasicGame {

	private final Map map;
	private World world;
	private Player player;
	private Camera camera;
	
	public OpenDoorsLogic(String title, Map map) {
		super(title);
		this.map = map;
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		world.render(camera);
		player.render(camera, g);
		
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		new Assets();
		camera = new Camera(5*16,5*16,832,632);
		world = new World(map, camera);
		player = new Player(0,0,world,Assets.PLAYER);
		world.addPlayer(player);
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		if(world.finished()){
			container.exit();
		}
		
		player.update(container, delta);
		camera.update(container, player, delta);
		
		if(world.getTileAt(player.getX(), player.getY()).getType() == 5){
			map.keyPressed(player.getX(), player.getY());
		}
	}

}
