package com.ashwin.fri.pacman.actor;

import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.Game;
import com.ashwin.fri.pacman.display.AnimatedSprite;
import com.ashwin.fri.pacman.grid.Grid;
import com.ashwin.fri.pacman.grid.Terrain;

/**
 * Ghosts are a particular kind of actors that move without any human input. They use a very simple
 * form of artificial intelligence to determine where to move next. Each time a ghost enters a new tile,
 * it computes a new target tile based on some determined algorithm. Then it moves toward that tile and
 * repeats the process over again.
 * 
 * @author ashwin
 * 
 */
public abstract class Ghost extends Actor {
	
	private static final int DURATION_CHASE   = 10000;
	private static final int DURATION_SCATTER = 5000;
	
	private Point2D _scatter, _exit;
	private Mode _mode;
	private Difficulty _difficulty;
	
	public Ghost(Point2D initial, Point2D scatter, Point2D exit, 
			Mode mode, AnimatedSprite sprite, Orientation dir, Difficulty difficulty) {
		super(initial, sprite, dir, 0.0);
		
		_exit = exit;
		_scatter = scatter;
		_difficulty = difficulty;
		
		setMode(mode);
	}
	
	@Override
	public void reset() {
		super.reset();
		_mode = Mode.IDLE;
	}
	
	@Override
	public boolean canMove(Terrain terrain) {
		// Ghosts can move through gates when they are in the eaten or exit states.
		// Otherwise, ghosts move exactly the same way that Actors move.
		return super.canMove(terrain)
				|| ((_mode.equals(Mode.EATEN) || _mode.equals(Mode.EXIT)) 
						&& terrain.equals(Terrain.GATE));
	}
	
	@Override
	public boolean move(List<Actor> actors, Grid grid) {
		PacMan pacman = (PacMan) actors.get(0);
		
		// If the ghost is currently in the center of a cell, then perform the targeting mechanism
		// to determine any orientation changes. Note: Unlike classic PacMan, the target cell is 
		// calculated right before the ghost is about to switch directions and not when it enters the cell.	
		if(_mode.equals(Mode.IDLE) && getMoveCounter() > Game.frames(getIdleDuration()))
			setMode(Mode.EXIT);
		else if(_mode.equals(Mode.EATEN) && getCurrentPosition().equals(getInitialPosition()))
			setMode(Mode.EXIT);
		else if(_mode.equals(Mode.EXIT) && getCurrentPosition().equals(getExitPosition()))
			setMode(Mode.CHASE);
		
		else if(_mode.equals(Mode.CHASE) && getMoveCounter() > Game.frames(getChaseDuration()))
			setMode(Mode.SCATTER);
		else if(_mode.equals(Mode.SCATTER) && getMoveCounter() > Game.frames(getScatterDuration()))
			setMode(Mode.CHASE);
		
		else if(getMoveCounter() > pacman.getMoveCounter() && (_mode.equals(Mode.CHASE) || _mode.equals(Mode.SCATTER)) && pacman.isEnergized())
			setMode(Mode.FRIGHTENED);
		else if(_mode.equals(Mode.FRIGHTENED) && !pacman.isEnergized())
			setMode(Mode.CHASE);
		
		return super.move(actors, grid);
	}
	
	/**
	 * The number of milliseconds that the chase states lasts. The higher the
	 * difficulty, the longer the chase state will last.
	 * 
	 * @return millisecond duration of chase state
	 */
	public int getChaseDuration() {
		return (int) (Ghost.DURATION_CHASE * _difficulty.getMultipler());
	}
	
	/**
	 * The number of milliseconds that the scatter state lasts. The higher the
	 * difficulty, the shorter the scatter state will last.
	 * 
	 * @return millisecond duration of scatter state
	 */
	public int getScatterDuration() {
		return (int) (Ghost.DURATION_SCATTER / _difficulty.getMultipler());
	}
	
	/**
	 * The number of milliseconds that the idles state lasts. The idle state 
	 * duration is independent of difficulty. Certain ghosts will always take
	 * longer to appear than other ghosts no matter the difficulty.
	 * 
	 * @return millisecond duration of the idle state
	 */
	abstract public int getIdleDuration();
	
	/**
	 * The Ghost AI uses a simple targeting mechanism to determine which direction
	 * to move in. Each ghost implementation performs chase targeting differently,
	 * but all other ghosts have the same targeting mechanism in the other states.
	 * 
	 * @param grid current grid
	 * @param point point to move to
	 * @return target orientation
	 */
	@Override
	public Orientation getNextOrientation(List<Actor> actors, Grid grid) {
		switch(_mode) {
			case CHASE: 	 return getFixedTarget(grid, getChaseTarget(actors, grid));
			case SCATTER: 	 return getFixedTarget(grid, getScatterPosition());
			case FRIGHTENED: return getRandomTarget(grid, getCurrentPosition());
			case EATEN: 	 return getFixedTarget(grid, getInitialPosition());
			case EXIT: 		 return getFixedTarget(grid, getExitPosition());
			default: 		 return getFixedTarget(grid, getInitialPosition());
		}
	}
	
	/**
	 * Returns the target orientation in the chase state. Implementations of ghosts
	 * must define this behavior.
	 * 
	 * @param grid current game
	 * @param pacman pacman for reference
	 * @return target orientation in chase state
	 */
	abstract protected Point2D getChaseTarget(List<Actor> actors, Grid grid);
	
	/**
	 * Selects a random valid tile to move to. Unlike fixed target tile, a ghost that uses
	 * randomized target tiling can move back the way that it came.
	 * 
	 * @return random valid target tile
	 */
	private Orientation getRandomTarget(Grid grid, Point2D source) {
		Point2D adj = null;
		Orientation dir = null;
		
		do {
			int rand = (int) (Math.random() * 4);
			dir = Orientation.values()[rand];
			adj = grid.adjacent(source, dir, 1);
		} while(dir.isReverse(getCurrentOrientation()) || !canMove(grid.get(adj)));
		
		return dir;
	}
	
	/**
	 * Selects a valid tile to move to using fixed target tile selection. In fixed target tile
	 * selection, a the tile that minimizes the euclidean distance between it and the destination
	 * is considered optimal. In the case that multiple tiles have the same distance, the tile with
	 * the lower ordinal (greater precedence) is selected.
	 * 
	 * @param dest
	 * @return fixed valid target tile
	 */
	private Orientation getFixedTarget(Grid grid, Point2D dest) {	
		Orientation best = null;
		double min = Double.MAX_VALUE;

		for(Orientation oth : Orientation.values()) {
			Point2D adj = grid.adjacent(getCurrentPosition(), oth, 1);
			double dist = adj.distance(dest);
						
			// The ghost cannot move backward during fixed target tile selection, so ignore
			// orientations that would cause the ghost to move in reverse.
			if(!getCurrentOrientation().isReverse(oth) && canMove(grid.get(adj)) && dist < min) {
				min = dist;
				best = oth;
			}
		}
		
		return best;
	}
	
	/**
	 * The current state that the ghost is in. The way that the ghost moves
	 * and the way that PacMan interacts with ghosts is largely dependent
	 * on the state that the ghost is in. Note: The original PacMan also includes
	 * a SCATTER state which I did not include for simplicity's sake.
	 * 
	 * @return current state of the ghost
	 */
	public Mode getMode() {
		return _mode;
	}
	
	public void setMode(Mode mode) {
		setSpeed(mode.getSpeed() * _difficulty.getMultipler());
		_mode = mode;
		setMoveCounter(0);
	}
	
	/**
	 * The exit position represents the exit location for ghosts in the ghost pen.
	 * This position is used as a fixed target to help ghosts exit.
	 * 
	 * @return exit position
	 */
	public Point2D getExitPosition() {
		return _exit;
	}
	
	/**
	 * The scatter position represents the scatter location for ghosts in the
	 * ghost pen. This position is used as a fixed target to help ghosts
	 * scatter.
	 * 
	 * @return scatter position
	 */
	public Point2D getScatterPosition() {
		return _scatter;
	}
	
	@Override
	public AnimatedSprite getSprite() {
		switch(_mode) {
			case FRIGHTENED: return new AnimatedSprite(50, getCurrentOrientation());
			case EATEN: 	 return new AnimatedSprite(62, getCurrentOrientation());
			default: 		 return super.getSprite();
		}
	}
	
	/**
	 * Ghosts exist in one of four states. They are either chasing, scattering,
	 * frightened, or being eaten. Which state they are in is dependent on game
	 * parameters. In the chase state, the ghosts chase after PacMan, in the
	 * scatter state they return to their corners, in the frightened state they
	 * move randomly, and in the eaten state they return back to the ghost pen.
	 * 
	 * @author ashwin
	 */
	public enum Mode {
		CHASE(0.95),
		SCATTER(0.95),
		FRIGHTENED(0.60),
		EATEN(0.60),
		EXIT(0.95),
		IDLE(0.00);
		
		private double _speed;
		
		private Mode(double speed) {
			_speed = speed;
		}
		
		public double getSpeed() {
			return _speed;
		}
	}
	
	/**
	 * The ghost AI performs differently at each difficulty setting. At successively
	 * higher settings, ghosts will move faster and will stay in chase mode for longer
	 * and frightened and scatter mode for less. 
	 * 
	 * @author ashwin
	 *
	 */
	public enum Difficulty {
		LOW(0.50),
		MEDIUM(0.65),
		HIGH(0.80),
		IMPOSSIBLE(1.0);
		
		private double _multiplier;
		
		private Difficulty(double multiplier) {
			_multiplier = multiplier;
		}
		
		public double getMultipler() {
			return _multiplier;
		}
	}
}
