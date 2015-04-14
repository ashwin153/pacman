package com.ashwin.fri.genetic;

import java.util.Arrays;

public class GeneticChromosome {
	
	// Chromosomes are stored in their decoded state as a bit array
	private boolean[] _genome;
	private GeneticDecoder _decoder;
	private double _fitness;
	
	/**
	 * Creates a new GeneticChromosome with the specified number of 
	 * randomized bits. This constructor is used to generate the initial
	 * population.
	 * 
	 * @param bits number of bits
	 */
	public GeneticChromosome(GeneticDecoder decoder, int bits) {
		_decoder = decoder;
		_genome = new boolean[bits];
		for(int i = 0; i < _genome.length; i++)
			_genome[i] = Math.random() < 0.5;
		_fitness = decoder.getFitness(getGenotype());
	}
	
	/**
	 * Creates a new GeneticChromosome from a specified bit array.
	 * 
	 * @param genome bit array
	 */
	public GeneticChromosome(GeneticDecoder decoder, boolean[] genome) {
		_decoder = decoder;
		_genome = genome;
		_fitness = decoder.getFitness(getGenotype());
	}
	
	public double getFitness() {
		return _fitness;
	}
	
	/**
	 * Returns a bit string representation (true = '1' and false = '0')
	 * of the underlying bit array.
	 * 
	 * @return bit string
	 */
	public String getGenotype() {
		StringBuilder sb = new StringBuilder();
		for(boolean bit : _genome)
			sb.append(bit ? '1' : '0');
		return sb.toString();
	}
	
	/**
	 * This method mates this chromosome with a given chromosome. The specified
	 * crossover rate determines the likelihood that single-point crossover will
	 * occur between the two chromosomes. This method returns an array containing
	 * two offspring.
	 * 
	 * @param othr other parent chromosome
	 * @param rate crossover probability
	 * @return offspring chromosomes
	 */
	public GeneticChromosome[] mate(GeneticChromosome othr, double rate) {
		boolean[] c1 = Arrays.copyOf(this._genome, this._genome.length);
		boolean[] c2 = Arrays.copyOf(othr._genome, othr._genome.length);
		
		if(Math.random() <= rate) {
			int index = (int) (Math.random() * this._genome.length);
			System.arraycopy(this._genome, 0, c2, 0, index);
			System.arraycopy(othr._genome, 0, c1, 0, index);
		}
		
		return new GeneticChromosome[] { new GeneticChromosome(_decoder, c1),
				 						 new GeneticChromosome(_decoder, c2) };
	}
	
	/**
	 * This method randomly flips bits in the genome based on a specified 
	 * mutation probability. The greater the mutation rate, the more likely 
	 * mutations will occur.
	 * 
	 * @param rate mutation probability
	 */
	public void mutate(double rate) {
		for(int i = 0; i < _genome.length; i++)
			if(Math.random() <= rate)
				_genome[i] = !_genome[i];
		_fitness = _decoder.getFitness(getGenotype());
	}
}
