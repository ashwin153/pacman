package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.ashwin.fri.neural.NeuralNet;
import com.ashwin.fri.pacman.actor.Ghost.Mode;
import com.ashwin.fri.pacman.grid.Grid;
import com.ashwin.fri.pacman.grid.Terrain;

public class PacManAi extends PacMan {
	
	private NeuralNet _neural;
	
	public PacManAi(Point2D initial, double speed, NeuralNet neural) {
		this(initial, speed, Orientation.LEFT, neural);
	}
		
	public PacManAi(Point2D initial, double speed, Orientation dir, NeuralNet neural) {
		
		super(initial, speed, dir);
		_neural = neural;
	}

	public NeuralNet getNeuralNet() {
		return _neural;
	}
	
	@Override
	public Orientation getNextOrientation(List<Actor> actors, Grid grid) {
		List<Double> inputs = new ArrayList<Double>();
		Point2D pos = getCurrentPosition();
		Orientation dir = getCurrentOrientation();
		
		// Add the coordinates of the nearest food and the nearest energizer
		// to the input list. Normalize values to between zero and 1.
		Point2D food = grid.getNearest(pos, Terrain.FOOD);
		Point2D energizer = grid.getNearest(pos, Terrain.ENERGIZER);
		if(energizer == null)
			energizer = food;
		
		inputs.add(food.getX() / grid.getWidth() / Grid.TILE_SIZE);
		inputs.add(food.getY() / grid.getHeight() / Grid.TILE_SIZE);
		inputs.add(energizer.getX() / grid.getWidth() / Grid.TILE_SIZE);
		inputs.add(energizer.getY() / grid.getHeight() / Grid.TILE_SIZE);
		
		double widthSq  = Math.pow(grid.getWidth() * Grid.TILE_SIZE, 2);
		double heightSq = Math.pow(grid.getHeight() * Grid.TILE_SIZE, 2);
		double maxDist  = Math.sqrt(widthSq + heightSq);
		double numModes = Mode.values().length;
		
		for(Actor actor : actors) {
			if(actor instanceof Ghost) {
				Ghost ghost = (Ghost) actor;
				double dist = pos.distance(ghost.getCurrentPosition());

				inputs.add(dist / maxDist);
				inputs.add(ghost.getMode().ordinal() / numModes);
			}
		}
				
		// Each of the four outputs of the neural net corresponds to a different direction.
		// Select the largest output that represents a direction that PacMan can move in.
		// If no such direction exists (which is impossible), then PacMan will continue in its
		// current direction.
		List<Double> outputs = _neural.execute(inputs);
		Orientation best = getCurrentOrientation();
		double fitness = Double.MIN_VALUE;
		
		for(int i = 0; i < outputs.size(); i++) {
			Orientation oth = Orientation.values()[i];
			Point2D adj = grid.adjacent(getCurrentPosition(), oth, 1);

			if(!dir.isReverse(oth) && 
					canMove(grid.get(adj)) && outputs.get(i) > fitness) {
				fitness = outputs.get(i);
				best = oth;
			}
		}
		
		return best;
	}

}
