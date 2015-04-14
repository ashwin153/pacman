package com.ashwin.fri.genetic;

public abstract class GeneticDecoder {

	protected GeneticGene[] _genes;
	
	public GeneticDecoder(GeneticGene[] genes) {
		_genes = genes;
	}
	
	/**
	 * Get the total number of bits required to store the chromosomes genes.
	 * This is used to construct the chromosomes that make up the initial population.
	 * 
	 * @return total bits required to store the chromosome
	 */
	public int getTotalBits() {
		int total = 0;
		for(int i = 0; i < _genes.length; i++)
			total += _genes[i].getBits();
		return total;
	}
	
	/**
	 * Returns the phenotype (actual values) of the various genes in the genotype.
	 * This is used by the fitness function to determine the fitness of a parameter set.
	 * 
	 * @param genotype bitstring
	 * @return phenotype
	 */
	public double[] getPhenotype(String genotype) {
		double[] phenotype = new double[_genes.length];
		int index = 0;
		
		for(int i = 0; i < _genes.length; i++) {
			String bitstring = genotype.substring(index, index + _genes[i].getBits());
			index += _genes[i].getBits();
			phenotype[i] = _genes[i].decode(bitstring);
		}
		
		return phenotype;
	}
	
	/**
	 * Returns the fitness of a genotype (bitstring). This method internally determines
	 * the phenotype of the genotype and then calls the getFitness(double[] phenotype) method.
	 * 
	 * @param genotype bitstring
	 * @return fitness
	 */
	public double getFitness(String genotype) {
		return getFitness(getPhenotype(genotype));
	}
	
	/**
	 * Returns the fitness of a phenotype. This method must be described by concrete
	 * implementations of this abstract class.
	 * 
	 * @param phenotype actual values
	 * @return fitness
	 */
	abstract public double getFitness(double[] phenotype);
}
