package game;
import gg.GameInterface;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import reuse.Assets;

public class OpenDoors extends GameInterface<Map> {

	private AppGameContainer app;
	private Map map;
	
	@Override
	public void run() {
		try {
			app = new AppGameContainer(new OpenDoorsLogic(
					"Open Doors!", map));
			app.setDisplayMode(800, 600, false);
			app.setForceExit(false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		
		if(app == null) return;
		
		app.exit();
		
		app = null;
	}

	@Override
	public void setMap(Map level) {
		map = level;
	}

}
