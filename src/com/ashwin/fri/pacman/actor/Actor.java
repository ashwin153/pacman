package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.display.AnimatedSprite;
import com.ashwin.fri.pacman.grid.Grid;
import com.ashwin.fri.pacman.grid.Terrain;

/**
 * Actors are the superclass of all movable objects in PacMan (aka PacMan and ghosts).
 * Unlike Terrain, actors are not confined to a single tile. However, for the purposes
 * of collisions, a center tile is computed based on the center of the sprite.
 * 
 * @author ashwin
 *
 */
public abstract class Actor {
	
	private static final double MAX_SPEED = 0.40;

	private Point2D _curPos, _initPos;
	private Orientation _curDir, _initDir;
	
	private AnimatedSprite _sprite;
	private double _curSpeed;
	private int _moves;
	
	/**
	 * @param grid grid that the actor lives in
	 * @param maxSpeed maximum possible speed for an actor
	 * @param speed percentage of max speed that the Actor travels at
	 * @param sprite the default sprite associated with the Actor
	 * @param initial the initial TILE that the Actor begins on
	 */
	public Actor(Point2D pos, AnimatedSprite sprite, Orientation dir, double curSpeed) {
		_curPos = _initPos = pos;
		_curDir = _initDir = dir;
		_curSpeed = curSpeed;
		_sprite = sprite;
	}
	
	public void reset() {
		_curPos = getInitialPosition();
		_curDir = getInitialOrientation();
		setMoveCounter(0);
	}
	
	/**
	 * Moves the actor to its next location in the grid. The next position
	 * is specified by the getNextPosition method. The method returns true
	 * if the actor's position was changed by the move and false otherwise.
	 * 
	 * @param game
	 * @return whether or not the actor could move
	 */
	public boolean move(List<Actor> actors, Grid grid) {
		// Increment the move counter for this actor
		// This counter is used for timing purposes
		_moves++;

		// Check that the actor can move through the terrain at
		// the desired point. This ensures that actors are not
		// moved into invalid tiles.
		Point2D next = getNextPosition(grid);
		if(!canMove(grid.get(next)))
			return false;
		
		// If the actor can move through the terrain, then set
		// the new point as the current position, handle the
		// new position, and update the associated sprite.
		if(grid.snap(next).equals(next))
			_curDir = getNextOrientation(actors, grid);
		
		_curPos = next;		
		getSprite().nextFrame(getCurrentOrientation());
		return true;
	}
	
	/**
	 * Returns whether or not the actor can move through the specified terrain.
	 * By default, actors can only move through passable terrain. However, under
	 * certain circumstances actors can move through impassable terrain (ex. gates)
	 * 
	 * @return whether or not the actor can move through the terrain
	 */
	public boolean canMove(Terrain terrain) {
		return terrain.isPassable();
	}
	
	/**
	 * Returns the initial position of the actor. Used to reset the actor back
	 * to its original position.
	 * 
	 * @return
	 */
	public Point2D getInitialPosition() {
		return _initPos;
	}
	
	/**
	 * The center Point2D of the actor. The tile that the center 
	 * Point2D is in is used to determine collisions. Actors are 
	 * allowed to take up more than just their own.
	 * 
	 * @return center Point2D of the actor
	 */
	public Point2D getCurrentPosition() {
		return _curPos;
	}
	
	/**
	 * The next position that the actor will go to on its next move. The actor will
	 * move a certain number of pixels determined by its speed and maxSpeed in the
	 * direction of its current orientation.
	 * 
	 * @return next position of the actor
	 */
	public Point2D getNextPosition(Grid grid) {
		// If the ghost is closer to the center of the tile then its speed
		// then snap the ghost to the center of the tile. This forces the ghosts'
		// ai to work (its only executed at the center point of the grid)
		Point2D center = grid.snap(_curPos);		
		Point2D adj	   = grid.adjacent(this, 1);
		boolean valid  = canMove(grid.get(adj));
		
		double dx = _curPos.getX() - center.getX();
		double dy = _curPos.getY() - center.getY();
		double pixels = getPixelSpeed();
		
		switch(getCurrentOrientation()) {
			case LEFT:  
				if(dx > 0 && Math.abs(dx) < pixels || dx == 0 && !valid) 
					return center;
				return grid.wrap(new Point2D.Double(_curPos.getX() - pixels, _curPos.getY()));
			case RIGHT:
				if(dx < 0 && Math.abs(dx) < pixels || dx == 0 && !valid)
					return center;
				return grid.wrap(new Point2D.Double(_curPos.getX() + pixels, _curPos.getY()));
			case UP: 
				if(dy > 0 && Math.abs(dy) < pixels || dy == 0 && !valid)
					return center;
				return grid.wrap(new Point2D.Double(_curPos.getX(), _curPos.getY() - pixels));
			default:
				if(dy < 0 && Math.abs(dy) < pixels || dy == 0 && !valid)
					return center;
				return grid.wrap(new Point2D.Double(_curPos.getX(), _curPos.getY() + pixels));
		}
	}
	
	/**
	 * Returns the initial direction in which the actor was facing.
	 * 
	 * @return
	 */
	public Orientation getInitialOrientation() {
		return _initDir;
	}
	
	/** 
	 * Returns the current direction in which the Actor is facing.
	 * 
	 * @return the direction in which the actor is facing.
	 */
	public Orientation getCurrentOrientation() {
		return _curDir;
	}
	
	/**
	 * Returns the next direction that the actor will face once it reaches
	 * the midpoint of a tile.
	 * 
	 * @return next orientation
	 */
	abstract public Orientation getNextOrientation(List<Actor> actors, Grid grid);
	
	/**
	 * Get the number of moves that this Actor has performed since the last
	 * reset. This is used for timing purposes. Events occur after a certain number
	 * of move cycles.
	 *
	 * @return number of moves
	 */
	public int getMoveCounter() {
		return _moves;
	}
	
	public void setMoveCounter(int moves) {
		_moves = moves;
	}

	/**
	 * The current speed of the actor is defined as a percentage of its maxSpeed.
	 * During movement calculations the speed and max speed are multiplied together, 
	 * but we treat them separately so that we can alter them independently.
	 * 
	 * @return current speed (between 0.0 and 1.0)
	 */
	public double getPixelSpeed() {
		return Grid.TILE_SIZE * _curSpeed * Actor.MAX_SPEED;
	}
	
	public void setSpeed(double speed) {
		_curSpeed = speed;
	}
	
	/**
	 * Returns the default sprite for this Actor. Subclasses can override the
	 * default behavior to implement different sprites for different states.
	 * 
	 * @return default sprite
	 */
	public AnimatedSprite getSprite() {
		return _sprite;
	}
	
	public enum Orientation {
		// Orientations are listed in decreasing precedence. Therefore, during fixed target
		// selection, if the distances are equal then the ghost will try to move up first, 
		// then left, etc.
		UP, LEFT, DOWN, RIGHT;
		
		public boolean isReverse(Orientation oth) {
			if(this.equals(UP) && oth.equals(DOWN))
				return true;
			else if(this.equals(DOWN) && oth.equals(UP))
				return true;
			else if(this.equals(LEFT) && oth.equals(RIGHT))
				return true;
			else if(this.equals(RIGHT) && oth.equals(LEFT))
				return true;
			return false;
		}
	}
}
