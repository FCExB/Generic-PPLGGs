package gui;

import ga.Population;
import gg.GameInterface;
import gg.PPLGGFront;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pplgg.Generator;
import pplgg.GeneratorFilter;
import pplgg.ga.GeneratorCreator;
import pplgg.ga.GeneratorIndividual;

public class PPLGGGUI<T> extends JFrame implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8871774359891654252L;

	private PPLGGFront<T> front;

	private JButton newGeneratorButton;
	private JButton redoButton;
	private JButton nextGenButton;
	private JButton goBackButton;
	private MapPanel<T> mapSamples[];
	private List<CloudMapPanel<T>> generators;
	private CloudMapThread<T> mapAdders[];
	private CloudMapPanel<T> inspectedGenerator;
	private JPanel generatorGrid;
	private JPanel inspectGrid;
	private Population<GeneratorIndividual<T>> gaPop;
	private Stack<Population<GeneratorIndividual<T>>> oldPops;
	private static final int noGenerators = 10;

	private ArrayList<String> allGens;

	public PPLGGGUI(PPLGGFront<T> front) {
		this.front = front;
		game = front.getGameRunnable();

		setSize(900, 750); // Standaard = .setSize(0,0)
		createGUI();
		setVisible(true); // Standaard = setVisible(false)
		setResizable(false);
		loadGenerators();
		startGA();

	}

	private void startGA() {
		// GeneticAlgorithm ga = new GeneticAlgorithm( new AIFitness(),
		// noGenerators, 2 );
		// gaPop = ga.getPopulation();
		oldPops = new Stack<Population<GeneratorIndividual<T>>>();
		gaPop = new Population<GeneratorIndividual<T>>(noGenerators, new GeneratorCreator<T>(front.getConverter()));
		initializeFromLoadedGenerators();
		setGeneratorsToPopulation();
	}

	private void loadGenerators() {
		allGens = new ArrayList<String>();
		File dir = new File(System.getProperty("user.dir"));
		String[] dirs = dir.list(new GeneratorFilter());
		for (String dirName : dirs) {
			for (String genName : new File(dirName).list())
				allGens.add(dirName + "/" + genName);
		}
	}

	private void initializeFromLoadedGenerators() {

		for (int i = 0; i < noGenerators && i < allGens.size(); i++) {
			String filename = allGens.remove((int) (Math.random() * allGens
					.size()));
			Generator<T> loadedGen = loadGenerator(filename);
			gaPop.replaceIndividual(i,
					new GeneratorIndividual<T>(loadedGen));
		}
	}

	private Generator<T> loadGenerator(String filename) {
		Generator<T> gen = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);

			gen = (Generator<T>) in.readObject();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return gen;
	}

	private void createGUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container window = this.getContentPane();
		this.setLayout(new FlowLayout());

		JLabel pplggLabel = new JLabel("PPLGG");
		pplggLabel.setFont(new Font("Verdana", 0, 48));
		window.add(pplggLabel);

		createButtons();
		createGeneratorGrid();

		JLabel creditsLabel = new JLabel(
				"Created by Jeppe Tuxen and Manuel Kerssemakers @ ITU");
		creditsLabel.setFont(new Font("Verdana", 0, 10));
		window.add(creditsLabel);

		window.setVisible(true);
		window.repaint();
	}

	private void createGeneratorGrid() {
		generatorGrid = new JPanel();
		GridLayout layout = new GridLayout(0, 1, 20, 5);
		generatorGrid.setLayout(layout);
		generators = new ArrayList<CloudMapPanel<T>>(noGenerators);
		for (int i = 0; i < noGenerators; i++) {
			generators.add(new CloudMapPanel<T>(this, front));
			generatorGrid.add(generators.get(i));
		}

		this.getContentPane().add(generatorGrid);
	}

	private void newMapSamples() {
		for (int i = 0; i < mapSamples.length; i++) {
			mapSamples[i].setMap(inspectedGenerator.getGenerator().generateMap(
					0));
		}
	}

	public void inspect(CloudMapPanel<T> cloudMapPanel) {
		newGeneratorButton.setEnabled(false);
		redoButton.setEnabled(true);
		goBackButton.setEnabled(false);
		nextGenButton.setEnabled(false);

		inspectedGenerator = cloudMapPanel;
		System.out.println(cloudMapPanel.getGenerator().toString());
		inspectGrid = new JPanel();
		GridLayout layout = new GridLayout(0, 1, 20, 5);
		inspectGrid.setLayout(layout);

		// find index of inspected generator
		int index;
		for (index = 0; index < generators.size(); index++) {
			if (generators.get(index) == cloudMapPanel)
				break;
		}

		mapSamples = new MapPanel[noGenerators - 1];
		int count = 0;
		for (int i = 0; i < generators.size(); i++) {
			if (i == index)
				inspectGrid.add(cloudMapPanel);
			else {
				mapSamples[count] = new MapPanel<T>(this, front.getConverter());
				inspectGrid.add(mapSamples[count]);
				count++;
			}
		}
		newMapSamples();

		this.getContentPane().remove(generatorGrid);
		// this.getContentPane().add( inspectGrid );
		this.getContentPane().add(inspectGrid,
				getContentPane().getComponentCount() - 1);
		validate();
	}

	public void viewOverView() {
		newGeneratorButton.setEnabled(true);
		redoButton.setEnabled(false);
		goBackButton.setEnabled(true);
		nextGenButton.setEnabled(true);
		if (game != null)
			closeGame();
		generatorGrid.removeAll();
		for (int i = 0; i < generators.size(); i++) {
			generatorGrid.add(generators.get(i));
		}
		this.getContentPane().remove(inspectGrid);
		this.getContentPane().add(generatorGrid,
				getContentPane().getComponentCount() - 1);
		validate();
	}

	private void createButtons() {
		Container window = this.getContentPane();
		redoButton = new JButton("New Map Samples");
		redoButton.setActionCommand("newmaps");
		redoButton.addActionListener(this);
		redoButton.setEnabled(false);
		window.add(redoButton);

		newGeneratorButton = new JButton("Replace Unselected");
		newGeneratorButton.setActionCommand("newgens");
		newGeneratorButton.addActionListener(this);
		window.add(newGeneratorButton);

		goBackButton = new JButton("Go Back <");
		goBackButton.setActionCommand("goback");
		goBackButton.addActionListener(this);
		goBackButton.setEnabled(false);
		window.add(goBackButton);

		nextGenButton = new JButton("> Next Generation ");
		nextGenButton.setActionCommand("nextgen");
		nextGenButton.addActionListener(this);
		window.add(nextGenButton);
	}

	@Override
	public void repaint() {
		super.repaint();
		System.out.println("Repainting " + Math.random());
	}

	public void actionPerformed(ActionEvent e) {
		if ("newmaps".equals(e.getActionCommand())) {
			if (game != null)
				closeGame();
			newMapSamples();
		} else if ("newgens".equals(e.getActionCommand())) {
			List<GeneratorIndividual<T>> individuals = gaPop.getIndividuals();
			for (int index = 0; index < individuals.size(); index++) {
				if (!generators.get(index).isSelected()) {
					Generator<T> replacementGen = loadGenerator(allGens
							.get((int) (allGens.size() * Math.random())));
					gaPop.replaceIndividual(index, new GeneratorIndividual<T>(
							replacementGen));
					mapAdders[index].stopAdding();
					generators.get(index).clear();
					generators.get(index)
							.setGenerator((individuals
									.get(index)).getGenerator());
					mapAdders[index] = new CloudMapThread(generators.get(index));
					mapAdders[index].setPriority(Thread.MIN_PRIORITY);
					mapAdders[index].start();
				}
			}
		} else if ("nextgen".equals(e.getActionCommand())) {
			nextGeneration();
		} else if ("goback".equals(e.getActionCommand())) {
			gaPop = oldPops.pop();
			for (int i = 0; i < generators.size(); i++)
				gaPop.setIndividualFitness(i, 0);
			setGeneratorsToPopulation();
			/** disable button when no populations are available anymore */
			if (oldPops.isEmpty()) {
				goBackButton.setEnabled(false);
			}
		}
	}

	private void setGeneratorsToPopulation() {
		if (mapAdders != null) {
			for (int i = 0; i < mapAdders.length; i++) {
				mapAdders[i].stopAdding();
			}
		}
		List<GeneratorIndividual<T>> individuals = gaPop.getIndividuals();
		mapAdders = new CloudMapThread[generators.size()];
		for (int i = 0; i < generators.size(); i++) {
			generators.get(i).setGenerator((individuals
					.get(i)).getGenerator());
			generators.get(i).clear();
			mapAdders[i] = new CloudMapThread(generators.get(i));
			mapAdders[i].setPriority(Thread.MIN_PRIORITY);
			mapAdders[i].start();
		}
	}

	private void nextGeneration() {

		boolean someSelected = false;
		for (int i = 0; i < generators.size(); i++)
			if (generators.get(i).isSelected()) {
				gaPop.setIndividualFitness(i, 1);
				generators.get(i).deselect();
				someSelected = true;
			}
		if (someSelected) {
			if (!goBackButton.isEnabled())
				goBackButton.setEnabled(true);
			Population<GeneratorIndividual<T>> oldPop = new Population<GeneratorIndividual<T>>(noGenerators, new GeneratorCreator<T>(front.getConverter()));
			oldPop.setIndividuals(gaPop.getIndividuals());
			oldPops.push(oldPop);
			gaPop.nextGeneration();
			setGeneratorsToPopulation();
		} else {
			System.out.println("Select at least one parent");
		}
	}

	private GameInterface<T> game;
	Thread popUp;

	public void playGame(MapPanel<T> mapPanel) {
		popUp = new Thread(game, "Game PopUp");

		game.setMap(front.getConverter().convertMap(mapPanel.getMap()));

		this.setAlwaysOnTop(false);

		closeGame();

		popUp.start();
	}

	public void closeGame() {
		game.close();
		try {
			if (popUp != null) {
				popUp.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
