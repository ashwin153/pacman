package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.display.AnimatedSprite;
import com.ashwin.fri.pacman.grid.Grid;

/**
 * Inky is the blue ghost in classic PacMan. Inky targets a location that is twice
 * the magnitude of the vector from Blinky's current position to the tile two in front
 * of PacMan's current position.
 * 
 * @author ashwin
 */
public class Inky extends Ghost {
	
	public Inky(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Difficulty difficulty) {
		this(initial, scatter, exit, mode, Orientation.UP, difficulty);
	}
	
	public Inky(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Orientation dir, Difficulty difficulty) {
		super(initial, scatter, exit, mode, new AnimatedSprite(42, dir), dir, difficulty);
	}
	
	@Override
	public int getIdleDuration() {
		return 6000; // (int) (Math.random() * 6000);
	}
	
	@Override
	public Point2D getChaseTarget(List<Actor> actors, Grid grid) {
		// Inky calculates a vector from Blinky's position to a tile two in front of
		// PacMan. Then he doubles the magnitude of that vector and moves to the tile
		// in which the vector terminates.
		Point2D t1 = actors.get(1).getCurrentPosition();
		Point2D t2 = grid.adjacent(actors.get(0), 2);
		return new Point2D.Double(t1.getX() + 2 * (t2.getX() - t1.getX()), t1.getY() - 2 * (t1.getX() - t2.getY()));
	}

}
