package com.ashwin.fri.genetic;

import java.util.Properties;

public class GeneticAlgorithm {
	
	public static final String PROPERTIES = "./ga.properties";	
	
	/**
	 * Runs the binary genetic algorithm with the specified fitness calculator
	 * and the specified properties. This method returns the best chromosome.
	 * 
	 * @param decoder genetic decoder implementation (fitness calculator)
	 * @param props algorithm properties
	 * @return most optimal chromosome
	 */
	public static GeneticChromosome run(GeneticDecoder decoder, Properties props) {
		GeneticPopulation pop = new GeneticPopulation(decoder, props);		
		int maxGen = Integer.valueOf(props.getProperty("ga.maxgen"));
		int gen = 0;
		
		printHeader();
		printGen(gen, pop, decoder);
		while(gen < maxGen) {
			pop = pop.evolve();
			gen++;
			printGen(gen, pop, decoder);
		}
		
		return pop.getBestChromosome();
	}
	
	/** Prints the header for the tabular data. */
	private static void printHeader() {
		System.out.printf("%6s\t%15s\t%15s\t%s\n", "Gen", "Min", "Avg", "Genotype");
	}
	
	/** Prints the specified population into tabular form. */
	private static void printGen(int gen, GeneticPopulation pop, GeneticDecoder decoder) {
		GeneticChromosome best = pop.getBestChromosome();
		double min = best.getFitness();
		double avg = pop.getAverageFitness();
		
		System.out.printf("%6d\t%15.8f\t%15.8f\t%s\n", gen, min, avg, best.getGenotype());
	}
}
