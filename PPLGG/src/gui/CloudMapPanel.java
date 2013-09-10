package gui;

import gg.GameMapConverter;
import gg.PPLGGFront;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pplgg.Generator;
import pplgg.Map;

// Extend our ball class from Canvas
public class CloudMapPanel<T> extends Canvas implements
		MouseListener {

	private enum State {
		IDLE, MOUSEOVER, INSPECTED, SELECTED;
	}

	private final PPLGGFront<T> front;
	private final GameMapConverter<T> converter;

	State state;
	Generator<T> myGen;
	ArrayList<Map> sampleMaps;
	private int playableSamples;
	private int playtestedSamples;
	double[][][] tileTypeAverages;
	private int mapHeight;
	private int mapWidth;
	private PPLGGGUI<T> gui;
	private static final int blockWidth = 4;
	private static final int blockHeight = 4;
	public static final Color coinColor = new Color(255, 255, 0);
	public static final Color brickColor = new Color(255, 0, 0);
	public static final Color rockColor = new Color(0, 0, 0);
	public static final Color groundColor = new Color(200, 100, 0);
	public static final Color backgroundColor = new Color(255, 255, 255);
	public static final int sampleSize = 30;

	public CloudMapPanel(PPLGGGUI<T> gui, PPLGGFront<T> front) {

		this.front = front;
		this.converter = front.getConverter();

		this.gui = gui;
		sampleMaps = new ArrayList<Map>();
		state = State.IDLE;
		this.setSize(blockWidth * Generator.width, blockHeight
				* Generator.height);
		playableSamples = 0;
		playtestedSamples = 0;
		mapWidth = Generator.width;
		mapHeight = Generator.height;
		tileTypeAverages = new double[converter.numOfTileTypes()][mapWidth][mapHeight];

		for (int t = 0; t < converter.numOfTileTypes(); t++) {
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					tileTypeAverages[t][x][y] = 0;
				}
			}
		}
		this.addMouseListener(this);
	}

	public void setGenerator(Generator<T> gen) {
		myGen = gen;
	}

	public void addMaps(int noToAdd) {

		for (int i = 0; i < noToAdd; i++) {
			Map newMap = myGen.generateMap(0);
			sampleMaps.add(newMap);
		}

		int noMaps = sampleMaps.size();
		for (int x = 0; x < mapWidth; x++)
			for (int y = 0; y < mapHeight; y++) {
				for (int t = 0; t < converter.numOfTileTypes(); t++) {
					double v;
					v = tileTypeAverages[t][x][y];
					v *= (double) (noMaps - noToAdd) / (double) noMaps;
					tileTypeAverages[t][x][y] = v;
				}
			}

		for (int m = sampleMaps.size() - noToAdd; m < sampleMaps.size(); m++) {
			Map map = sampleMaps.get(m);
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {	
					tileTypeAverages[map.getTerrain(x, y)][x][y] += (double) 1 / noMaps;
				}
			}
		}

		repaint();
	}

	public void paint(Graphics g) {
		synchronized (this) {
			drawCloudMap(g);
			drawPlayability(g);
		}
	}

	private void drawPlayability(Graphics g) {
		g.setFont(new Font("Arial", 1, 15));
		g.drawString(playableSamples + " / " + playtestedSamples, mapWidth
				* blockWidth - 46, 15);
	}

	private void drawCloudMap(Graphics graphics) {
		BufferedImage buffer = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics gr = buffer.getGraphics();
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				double emptyAverage = 1;
				for (int t = 0; t < converter.numOfTileTypes(); t++) {
					emptyAverage -= tileTypeAverages[t][x][y];
				}
				
				double r = emptyAverage * backgroundColor.getRed();
				double g = emptyAverage * backgroundColor.getGreen();
				double b = emptyAverage * backgroundColor.getBlue();
				
				for (int t = 0; t < converter.numOfTileTypes(); t++) {
					Color tileColor =  converter.colorReprisentation(t);
					
					r += tileTypeAverages[t][x][y] * tileColor.getRed();
					g += tileTypeAverages[t][x][y] * tileColor.getGreen();
					b += tileTypeAverages[t][x][y] * tileColor.getBlue();
				}
				
				if (state == State.SELECTED)
					gr.setColor(new Color((int) (r / 1.1), (int) (g / 1.1), (int)b));
				else
					gr.setColor(new Color((int)r, (int)g, (int)b));
				gr.fillRect(x * blockWidth, y * blockHeight, blockWidth,
						blockHeight);
			}
		}

		int panelWidth = getWidth();
		int panelHeight = getHeight();
		gr.setColor(Color.black);
		switch (state) {
		case SELECTED:
		case INSPECTED:
			gr.setColor(Color.blue);
		case MOUSEOVER:
			gr.drawRect(1, 1, panelWidth - 3, panelHeight - 3);
			gr.drawRect(2, 2, panelWidth - 5, panelHeight - 5);
		case IDLE:
			gr.drawRect(0, 0, panelWidth - 1, getHeight() - 1);
			break;
		}
		gr.setColor(Color.black);
		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.drawImage(buffer, null, 0, 0);
	}

	public void clear() {
		sampleMaps.clear();
		playableSamples = 0;
		playtestedSamples = 0;

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (!(state == State.SELECTED || state == State.INSPECTED)) {
			state = State.MOUSEOVER;
			repaint();

		}

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (!(state == State.SELECTED || state == State.INSPECTED)) {
			state = State.IDLE;
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			switch (state) {
			case INSPECTED:
				state = State.SELECTED;
				gui.viewOverView();
				break;
			case SELECTED:
				state = State.MOUSEOVER;
				break;
			case MOUSEOVER:
				state = State.SELECTED;
				break;
			}
		else if (e.getButton() == MouseEvent.BUTTON3) {
			if (state == State.MOUSEOVER || state == State.SELECTED) {
				state = State.INSPECTED;
				gui.inspect(this);
			} else {
				gui.viewOverView();
				state = State.MOUSEOVER;
			}
		}
		repaint();
	}

	public boolean isSelected() {
		return state == State.SELECTED;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

	public int getNoMaps() {
		return sampleMaps.size();
	}

	public Generator<T> getGenerator() {
		return myGen;
	}

	public void deselect() {
		state = State.IDLE;
	}

	public int getNoPlaytestedSamples() {
		return playtestedSamples;
	}

	private static final Object gameLock = new Object();

	public void playtestSample() {
		synchronized (gameLock) {
			T toPlay = converter.convertMap(
					sampleMaps.get(playtestedSamples++));
			double score = front.getAI().playLevel(toPlay, false);
			if (score > 0)
				playableSamples++;
		}
		repaint();
	}

}
