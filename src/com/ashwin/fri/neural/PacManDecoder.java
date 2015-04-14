package com.ashwin.fri.neural;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.ashwin.fri.genetic.GeneticAlgorithm;
import com.ashwin.fri.genetic.GeneticChromosome;
import com.ashwin.fri.genetic.GeneticDecoder;
import com.ashwin.fri.genetic.GeneticGene;
import com.ashwin.fri.pacman.Game;
import com.ashwin.fri.pacman.actor.PacManAi;

/**
 * The PacManDecoder is responsible for training the neural net's weights.
 * Fitness is calculated as 1 / score, because the genetic algorithm MINIMIZES
 * the fitness of the population.
 * 
 * @author ashwin
 *
 */
public class PacManDecoder extends GeneticDecoder {

	/** Where to save the output of the neural network training algorithm. */
	private static final String NEURAL_FILE  = "./assets/neural/n2.ser";

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(new File(GeneticAlgorithm.PROPERTIES)));
		props.load(new FileInputStream(new File("./pacman.properties")));
		props.setProperty("game.neural", NEURAL_FILE);		// Point to the generated neural net
		props.setProperty("game.enable.ai", "true");		// Enable PacMan AI
		props.setProperty("game.fps", "10000");				// Run game at infinite speed
		File output = new File(NEURAL_FILE);	// Output file for the neural net
		
		// Generate a randomized neural net with an arbitrary number of layers
		// with an arbitrary number of nodes in each layer. The only restriction is
		// that the number of inputs must match the number of inputs in PacManAi
		// and the number of outputs must match the number of outputs in PacManAi.
		NeuralNet net = new NeuralNet(13, 10, 8, 4);
		net.save(output);
		
		GeneticGene[] genes = new GeneticGene[net.size()];
		for(int i = 0; i < genes.length; i++)
			genes[i] = new GeneticGene(null, 40, -3.0, 3.0);

		// Initialize the decoder and run the algorithm using the loaded properties
		PacManDecoder decoder = new PacManDecoder(genes, Game.load(props));
		GeneticChromosome best = GeneticAlgorithm.run(decoder, props);
		
		// Set the weights of the neural net to be the best chromosome in the population
		// and then save this neural net to the output file. THe program terminates once
		// this condition has been met.
		String bits = best.getGenotype();
		double[] phenotype = decoder.getPhenotype(bits);
		net.setWeights(phenotype);
		System.out.println(net.getWeights());
		net.save(output);
	}
	
	private Game _game;
	
	public PacManDecoder(GeneticGene[] genes, Game game) throws Exception {
		super(genes);
		_game = game;
	}
	
	@Override
	public double getFitness(double[] phenotype) {
		// Set the weights of the NeuralNet to be the weights 
		// of the phenotype specified in the parameters
		PacManAi pacman = (PacManAi) _game.getPacMan();
		pacman.getNeuralNet().setWeights(phenotype);
		long start = System.currentTimeMillis();

		// Run the game and wait for it to complete.
		_game.reset();
		_game.start();
			
		while(_game.isRunning()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long elapsed = System.currentTimeMillis() - start;
		// Compute and return the inverse of the score as the fitness
		return 1.0 / (elapsed + pacman.getPoints());
	}

}
