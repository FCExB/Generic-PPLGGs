package pplgg;

import ga.GeneticAlgorithm;
import ga.Individual;
import gg.GameAIInterface;
import gg.GameMapConverter;
import gg.PPLGGFront;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import pplgg.ga.GeneratorCreator;
import pplgg.ga.GeneratorFitness;
import pplgg.ga.GeneratorIndividual;
import pplgg.ga.MapFitness;

public class PPLGG {

	public static <T> void run(PPLGGFront<T> front, int width, int height,
			int populationSize, int generations, int mapSampleSize, int numGens) {

		Generator.width = width;
		Generator.height = height;
		generateGenerators(front.getAI(), front.getConverter(), populationSize, generations, mapSampleSize, numGens);

	}

	private static <T> void generateGenerators(GameAIInterface<T> ai,
			GameMapConverter<T> converter, int populationSize, int generations, int mapSampleSize, int numGens) {

		long timeStamp = System.currentTimeMillis();
		for (int i = 0; i < numGens; i++) {
			Generator<T> gen = runGA(ai, converter, populationSize, generations, mapSampleSize);
			if (gen != null) {
				try {
					String filename = "GeneratorsTest" + timeStamp
							+ "/Generator" + i + ".gen";
					File file = new File(filename);
					file.getParentFile().mkdirs();
					FileOutputStream fos = null;
					ObjectOutputStream out = null;
					fos = new FileOutputStream(file);
					out = new ObjectOutputStream(fos);

					System.out.println("Running GA to find generator #"
							+ (i + 1));

					out.writeObject(gen);

					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	public static <T> Generator<T> runGA(GameAIInterface<T> ai,
			GameMapConverter<T> converter, int populationSize, int generations, int mapSampleSize) {

		GeneratorCreator<T> creator = new GeneratorCreator<T>(converter);
		
		GeneticAlgorithm<GeneratorIndividual<T>> geneticAlgorithm = new GeneticAlgorithm<GeneratorIndividual<T>>(
				new GeneratorFitness<T>(mapSampleSize, new MapFitness<T>(ai,converter)),creator, populationSize, generations);
		Individual<GeneratorIndividual<T>> fittestInd = geneticAlgorithm.getFittestIndividual();
		if (fittestInd != null) {
			GeneratorIndividual<T> genInd = (GeneratorIndividual<T>) fittestInd;
			System.out.println("Finished the iteration with a fitness of: "
					+ genInd.getFitness());
			return genInd.getGenerator();
		} else {
			return null;
		}
	}

	// timeToWait = 0 means you don't get to see the output
	public static <T> void loopGenerator(Generator<T> gen, int timeToWait) {
		int mapsCreated = 0;
		while (true) {
			// generate and show a map
			Map map = gen.generateMap(1);
			if (timeToWait > 0) {
				System.out.println(map.toString());
			}
			mapsCreated++;
			System.out.println("Map nr.: " + mapsCreated);

			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
			}

		}
	}

	public static <T> void runPPLGG() {

		int mapSamples = 100;

		ArrayList<Generator<T>> allGens = new ArrayList<Generator<T>>();

		File dir = new File("C:/PPLGG/");
		String[] dirs = dir.list(new GeneratorFilter());

		for (String dirName : dirs) {
			for (String genName : new File(dirName).list()) {

				FileInputStream fis = null;
				ObjectInputStream in = null;
				try {
					fis = new FileInputStream(dirName + "/" + genName);
					in = new ObjectInputStream(fis);

					allGens.add((Generator<T>) in.readObject());

					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		int i = 0;
		long startTime = System.currentTimeMillis();

		for (Generator<T> levelGen : allGens) {
			System.out.println("Generator #" + ((i++) + 1));
			for (int j = 0; j < mapSamples; j += 1) {
				levelGen.generateMap(0);
			}

		}
		long timeTaken = System.currentTimeMillis() - startTime;
		System.out.println("Finished");
		System.out.println("Took " + timeTaken + "ms");

		System.out.println("That's an average of" + (timeTaken / i)
				+ "ms per map");
	}
}
