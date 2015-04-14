package com.ashwin.fri.neural;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NeuralNets are a collection of Layers. They can be executed on a series
 * of inputs to produce a series of outputs. While the NeuralNet itself is
 * abstract and generalized, it can be used in any number of situations.
 * The NeuralNet implements Serializable so that trained nets can be saved to
 * file and used in the future with the PacManAi.
 * 
 * @author ashwin
 */
public class NeuralNet implements Serializable {

	private static final long serialVersionUID = -4747208554472473154L;
	
	private List<Layer> _layers;
	
	public NeuralNet(List<Layer> layers) {
		_layers = layers;
	}
	
	public NeuralNet(int... nodes) {
		_layers = new ArrayList<Layer>();
		for(int i = 1; i < nodes.length; i++)
			_layers.add(new Layer(nodes[i-1], nodes[i]));
	}
	
	public List<Layer> getLayers() {
		return _layers;
	}
	
	public List<Double> getWeights() {
		List<Double> weights = new ArrayList<Double>();
		
		for(Layer layer : _layers)
			for(Neuron neuron : layer.getNeurons())
				weights.addAll(neuron.getWeights());
		
		return weights;
			
	}
	
	public void setWeights(double[] weights) {		
		int index = 0;
		for(Layer layer : _layers) {
			for(Neuron neuron : layer.getNeurons()) {
				double[] range = Arrays.copyOfRange(weights, index, index + neuron.size());
				List<Double> wrap = new ArrayList<Double>();
				for(double val : range)
					wrap.add(val);
				
				neuron.setWeights(wrap);
				index += neuron.size();
			}
		}
	}
	
	public int size() {
		int sum = 0;
		for(Layer layer : _layers)
			sum += layer.size();
		return sum;
	}
	
	/**
	 * Executes the entire neural net and returns the output of the
	 * top most layer in the net.
	 * 
	 * @param inputs
	 * @return
	 */
	public List<Double> execute(List<Double> inputs) {
		return execute(_layers.size() - 1, inputs);
	}
	
	/**
	 * Executes the neural net up to the specified layer and returns
	 * the output of the top most layer.
	 * 
	 * @param layer
	 * @param inputs
	 * @return
	 */
	public List<Double> execute(int layer, List<Double> inputs) {
		if(layer == 0)
			return _layers.get(layer).getOutputs(inputs);
		else
			return _layers.get(layer).getOutputs(execute(layer - 1, inputs));
	}
	
	/**
	 * Saves a NeuralNet to file. This method performs Object Serialization
	 * using the Serialization interface provided by Java.
	 * 
	 * @param file save location
	 * @throws IOException write error
	 */
	public void save(File file) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this);
		oos.close();
	}
	
	/**
	 * Loads a NeuralNet from file. This method performs Object Deserialization
	 * using the Serialization interface provided by Java.
	 * 
	 * @param file
	 * @return
	 * @throws IOException read error
	 * @throws ClassNotFoundException object serializer error
	 */
	public static NeuralNet load(File file) throws IOException, ClassNotFoundException {
		 ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		 NeuralNet net = (NeuralNet) in.readObject();
         in.close();
         return net;
	}
}
