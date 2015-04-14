package com.ashwin.fri.genetic;

import java.math.BigInteger;

public class GeneticGene {

	private String _name;
	private int _bits;
	private double _lower, _upper;
	
	public GeneticGene(String name, int bits, double lower, double upper) {
		_name = name;
		_bits = bits;
		_lower = lower;
		_upper = upper;
	}
	
	public String getName() {
		return _name;
	}
	
	public int getBits() {
		return _bits;
	}
	
	public double getLowerBound() {
		return _lower;
	}
	
	public double getUpperBound() {
		return _upper;
	}
	
	public String encode(double val) {
		double norm = (val - _lower) / (_upper - _lower);
		BigInteger bin = BigInteger.valueOf((long) Math.round(norm * (Math.pow(2, _bits) - 1)));
		String bits = bin.toString(2);
		
		while(bits.length() < _bits)
			bits = "0" + bits;
		
		return bits;
	}
	
	public double decode(String bitstring) {
		double bin = new BigInteger(bitstring, 2).doubleValue();
		return _lower + (_upper - _lower) * bin / (Math.pow(2, _bits) - 1);
	}
}
