package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.display.AnimatedSprite;
import com.ashwin.fri.pacman.grid.Grid;

/**
 * Blinky is the red ghost in PacMan. His target location is simply PacMan's current
 * position. In the classic game, Blinky speeds up into an accelerated state called "Elroy"
 * once there are a certain number of dots remaining in the maze.
 * 
 * @author ashwin
 * 
 */
public class Blinky extends Ghost {
				
	public Blinky(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Difficulty difficulty) {
		this(initial, scatter, exit, mode, Orientation.UP, difficulty);
	}
	
	public Blinky(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Orientation dir, Difficulty difficulty) {
		super(initial, scatter, exit, mode, new AnimatedSprite(34, dir), dir, difficulty);
	}
	
	@Override
	public int getIdleDuration() {
		// Blinky exits the ghost pen immediately after the game begins
		return 0;
	}
	
	@Override
	public Point2D getChaseTarget(List<Actor> actors, Grid grid) {
		// Blinky targets PacMan's current tile using the built in fixed targeting mechanism
		return actors.get(0).getCurrentPosition();
	}

}
