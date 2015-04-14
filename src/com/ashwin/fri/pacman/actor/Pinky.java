package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.display.AnimatedSprite;
import com.ashwin.fri.pacman.grid.Grid;

/**
 * Pinky is the pink ghost in classic PacMan. Pinky targets a tile that is four tiles
 * in front of PacMan's current position.
 * 
 * @author ashwin
 */
public class Pinky extends Ghost {
			
	public Pinky(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Difficulty difficulty) {
		this(initial, scatter, exit, mode, Orientation.UP, difficulty);
	}
	
	public Pinky(Point2D initial, Point2D scatter, Point2D exit, Mode mode, Orientation dir, Difficulty difficulty) {
		super(initial, scatter, exit, mode, new AnimatedSprite(46, dir), dir, difficulty);
	}
	
	@Override
	public int getIdleDuration() {
		return 10000; // (int) (Math.random() * 10000);
	}
	
	@Override
	public Point2D getChaseTarget(List<Actor> actors, Grid grid) {
		// Pinky targets a space four tiles in front of PacMan. In the classic
		// PacMan, there is an overflow bug that causes the target tile to actually
		// be four up and four to the left of where PacMan is when he is facing UP.
		// However, for the sake of consistency we do not include this bug.
		return grid.adjacent(actors.get(0), 4);
	}

}
