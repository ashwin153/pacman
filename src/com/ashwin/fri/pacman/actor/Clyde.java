package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.display.AnimatedSprite;
import com.ashwin.fri.pacman.grid.Grid;

/**
 * Clyde is the orange ghost in classic PacMan. Clyde targets the same way as Blinky
 * when he is greater than 8 tiles away from PacMan and targets the same as Scatter Mode
 * when he is closer than that range.
 * 
 * @author ashwin
 */
public class Clyde extends Ghost {
	

	public Clyde(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Difficulty difficulty) {
		this(initial, scatter, exit, mode, Orientation.UP, difficulty);
	}
	
	public Clyde(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Orientation dir, Difficulty difficulty) {
		super(initial, scatter, exit, mode, new AnimatedSprite(38, dir), dir, difficulty);
	}
	
	@Override
	public int getIdleDuration() {
		return 2000; // (int) (Math.random() * 2000);
	}
	
	@Override
	public Point2D getChaseTarget(List<Actor> actors, Grid grid) {
		// If the distance between Clyde and PacMan's tile is greater than or equal
		// to eight, then Clyde targets PacMan exactly like Blinky. Otherwise, Clyde
		// targets the tile that he uses during scatster mode.
		Point2D point = actors.get(0).getCurrentPosition();
		double dist = getCurrentPosition().distance(point);
		return (dist >= 8 * Grid.TILE_SIZE) ? point : getScatterPosition();
	}

}
