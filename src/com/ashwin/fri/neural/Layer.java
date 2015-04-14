package com.ashwin.fri.neural;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A layer represents a collection of neurons. A layer takes a set of
 * inputs, runs them through its neurons, and outputs a collection of
 * outputs ranging from 0 to 1.
 * 
 * @author ashwin
 *
 */
public class Layer implements Serializable {

	private static final long serialVersionUID = -152148321829417267L;
	
	private List<Neuron> _neurons;
	private int _inputs;
	
	/**
	 * Creates a layer with the specified number of neurons that each
	 * take the specified number of inputs. The neurons weights are
	 * initialized to random values.
	 * 
	 * @param size
	 */
	public Layer(int inputs, int nodes) {
		_neurons = new ArrayList<Neuron>();
		for(int i = 0; i < nodes; i++)
			_neurons.add(new Neuron(inputs));
		_inputs = inputs;
	}
	
	public Layer(List<Neuron> neurons) {
		_neurons = neurons;
	}
	
	/** @return number of weights in the layer */
	public int size() {
		return _neurons.size() * (_inputs + 1);
	}
	
	public List<Neuron> getNeurons() {
		return _neurons;
	}
	
	/**
	 * Returns the outputs of the layer given the set of inputs.
	 * The output list contains the action potential of each neuron when
	 * supplied with the given input values.
	 * 
	 * @param inputs
	 * @return
	 */
	public List<Double> getOutputs(List<Double> inputs) {
		List<Double> outputs = new ArrayList<Double>();
		for(int i = 0; i < _neurons.size(); i++)
			outputs.add(_neurons.get(i).getActionPotential(inputs));
		return outputs;
	}
}
