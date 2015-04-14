package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.Game;
import com.ashwin.fri.pacman.actor.Ghost.Mode;
import com.ashwin.fri.pacman.display.AnimatedSprite;
import com.ashwin.fri.pacman.grid.Grid;
import com.ashwin.fri.pacman.grid.Terrain;

/**
 * PacMan is the main character in the classic game PacMan. PacMan can move
 * just like other Actors, but also has an energized state in which it can
 * consume ghosts. PacMan can earn also points as he consumes food, ghosts and energizers.
 * 
 * @author ashwin
 */
public abstract class PacMan extends Actor {
	
	/** The number of points earned for eating food. */
	private static final int POINTS_FOOD  = 10;
	/** The number of points earned for eating a ghost * 2 ^ number of ghosts. */
	private static final int POINTS_GHOST = 200;
	/** The number of points earned for eating an energizer. */
	private static final int POINTS_ENERGIZER = 50;
	
	/** The duration that PacMan is energized for. */
	private static final int DURATION_ENERGIZED = 3000;
	
	private boolean _isEnergized;
	private int _points, _ghosts;
	
	public PacMan(Point2D initial, double speed) {
		this(initial, speed, Orientation.LEFT);
	}
	
	public PacMan(Point2D initial, double speed, Orientation dir) {
		super(initial, new AnimatedSprite(10, dir, 6), dir, speed);
	}
	
	@Override
	public boolean move(List<Actor> actors, Grid grid) {		
		// Process the value on the current tile and then move just like an
		// actor would. We process the current point and then move to ensure that
		// the point we are processing is a valid point.		
		Point2D cur = getCurrentPosition();
		switch(grid.get(cur)) {
			case FOOD:
				grid.set(cur, Terrain.EMPTY);
				_points += POINTS_FOOD;
				break;
			case ENERGIZER:
				grid.set(cur, Terrain.EMPTY);
				setEnergized(true);
				_points += POINTS_ENERGIZER;
				break;
			default: break;
		}
		
		// If the PacMan is in the energized state and it has surpassed the
		// dot threshold, then move it out of the energized state.
		if(_isEnergized && getMoveCounter() > Game.frames(DURATION_ENERGIZED))
			setEnergized(false);
		
		return super.move(actors, grid);
	}
	
	@Override
	public void reset() {
		super.reset();
		_points = 0;
		setEnergized(false);
	}
	
	/**
	 * Returns the number of points that the PacMan has collected so far.
	 * @return score
	 */
	public int getPoints() {
		return _points;
	}
	
	/**
	 * Attempts to consume the ghost. Returns false if consumption was unsuccessful (PacMan was eaten
	 * by the ghosts) and true otherwise (PacMan ate the ghosts or nobody ate anyone).
	 * 
	 * @param ghost
	 * @return whether or not PacMan successfully consumed the ghost
	 */
	public boolean consume(Ghost ghost) {
		if(ghost.getMode().equals(Mode.CHASE) || ghost.getMode().equals(Mode.SCATTER))
			return false;
		
		else if(ghost.getMode().equals(Mode.FRIGHTENED)) {
			_points += POINTS_GHOST * Math.pow(2, _ghosts);
			ghost.setMode(Mode.EATEN);
		}
		
		return true;
	}
	
	/**
	 * Returns whether or not PacMan is in an energized state or not. In the energized state
	 * he can consume ghosts for extra points.
	 * 
	 * @return whether or not PacMan is in the energized state
	 */
	public boolean isEnergized() {
		return _isEnergized;
	}
	
	public void setEnergized(boolean isEnergized) {
		_isEnergized = isEnergized;
		_ghosts = 0;
		setMoveCounter(0);
	}
}
