package com.ashwin.fri.genetic;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

public class GeneticPopulation {
	
	private GeneticChromosome[] _pop;
	private Properties _props;
	private GeneticDecoder _decoder;
	
	/**
	 * Creates a new GeneticPopulation with randomized chromosomes using
	 * the genetic algorithm parameters specified by the properties file.
	 * 
	 * @param decoder genetic decoder implementation
	 * @param props algorithm parameters
	 */
	public GeneticPopulation(GeneticDecoder decoder, Properties props) {
		_props = props;
		_decoder = decoder;
		
		int size = Integer.valueOf(_props.getProperty("pop.size"));
		int bits = decoder.getTotalBits();
		
		_pop = new GeneticChromosome[size];
		for(int i = 0; i < _pop.length; i++)
			_pop[i] = new GeneticChromosome(_decoder, bits);
		Arrays.sort(_pop, new ChromosomeComparator());
	}
	
	/**
	 * Creates a new GeneticPopulation from an existing chromosome array.
	 * This is used by the evolve method to generate new generations.
	 * 
	 * @param props algorithm parameters
	 * @param decoder genetic decoder implementation
	 * @param pop population
	 */
	public GeneticPopulation(GeneticDecoder decoder, Properties props, GeneticChromosome[] pop) {
		_props = props;
		_decoder = decoder;
		_pop = pop;
		Arrays.sort(_pop, new ChromosomeComparator());
	}
	
	/**
	 * This method evolves the population by one generation. It performs
	 * elitism, selection, mating, and mutation.
	 * 
	 * @return evolved population
	 */
	public GeneticPopulation evolve() {
		GeneticChromosome[] next = new GeneticChromosome[_pop.length];
		double cross   = Double.valueOf(_props.getProperty("pop.cross"));
		double mutate  = Double.valueOf(_props.getProperty("pop.mutate"));
		double elitism = Double.valueOf(_props.getProperty("pop.elitism"));
		
		// Population size MUST be even, because we do everything in multiples of 2
		int index = (int) (_pop.length * elitism);
		if(index % 2 != 0) index++;
				
		// Elitism: Copy the best elements in the population into the next generation.
		// Because the population is sorted, take elements between [0, index)
		System.arraycopy(_pop, 0, next, 0, index);
		
		// While the next generation is not yet full, continue natural selection
		while(index < next.length) {
			// Select two parents using tournament selection
			GeneticChromosome p1 = select();
			GeneticChromosome p2 = select();
			
			// Mate the parents and mutate their offspring
			GeneticChromosome[] off = p1.mate(p2, cross);
			off[0].mutate(mutate);
			off[1].mutate(mutate);
			
			// Put the offspring into the next generation and increment the counter
			System.arraycopy(off, 0, next, index, 2);
			index += 2;
		}

		// Return a new generation of the population
		return new GeneticPopulation(_decoder, _props, next);
	}
	
	/**
	 * This method performs tournament selection. Tournament selection involves
	 * selecting a group of chromosomes and returning the chromosome with the
	 * lowest fitness value.
	 * 
	 * @return selected chromosome
	 */
	private GeneticChromosome select() {
		int size = Integer.valueOf(_props.getProperty("tournament.size"));
		GeneticChromosome winner = null;
		double min = Double.MAX_VALUE;
		
		for(int i = 0; i < size; i++) {
			int rand = (int) (Math.random() * _pop.length);
			double fitness = _pop[rand].getFitness();
			
			if(fitness < min) {
				winner = _pop[rand];
				min = fitness;
			}
		}
		
		return winner;
	}
	
	/** Returns the average fitness of the population. */
	public double getAverageFitness() {
		double avg = 0.0;
		for(int i = 0; i < _pop.length; i++)
			avg += _pop[i].getFitness();
		return avg / _pop.length;
	}
	
	/** Returns the most fit chromosome in the population. */
	public GeneticChromosome getBestChromosome() {
		return _pop[0];
	}
	
	/**
	 * This class is responsible for comparing two chromosomes. It is used
	 * by the evolve function to sort the population by their fitness values.
	 * 
	 * @author ashwin
	 */
	private class ChromosomeComparator implements Comparator<GeneticChromosome> {
		public int compare(GeneticChromosome o1, GeneticChromosome o2) {
			Double f1 = o1.getFitness();
			Double f2 = o2.getFitness();			
			return f1.compareTo(f2);
		}
	}
}
