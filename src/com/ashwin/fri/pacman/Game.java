package com.ashwin.fri.pacman;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.Timer;

import com.ashwin.fri.neural.NeuralNet;
import com.ashwin.fri.pacman.actor.Actor;
import com.ashwin.fri.pacman.actor.Blinky;
import com.ashwin.fri.pacman.actor.Clyde;
import com.ashwin.fri.pacman.actor.Ghost;
import com.ashwin.fri.pacman.actor.Ghost.Difficulty;
import com.ashwin.fri.pacman.actor.Ghost.Mode;
import com.ashwin.fri.pacman.actor.Inky;
import com.ashwin.fri.pacman.actor.PacMan;
import com.ashwin.fri.pacman.actor.PacManAi;
import com.ashwin.fri.pacman.actor.PacManHuman;
import com.ashwin.fri.pacman.actor.Pinky;
import com.ashwin.fri.pacman.grid.Grid;

public class Game implements ActionListener {
	
	/** The number of frames per second that will be rendered. */
	private static final int FRAMES_PER_SECOND = 30;
	
	private PacMan _pacman;
	private List<Ghost> _ghosts;
	private Grid _grid;

	private Timer _timer;
	
	public Game(Grid grid, PacMan pacman, Blinky blinky, Clyde clyde, Inky inky, Pinky pinky) {
		this(grid, pacman, blinky, clyde, inky, pinky, FRAMES_PER_SECOND);
	}
	
	public Game(Grid grid, PacMan pacman, Blinky blinky, Clyde clyde, Inky inky, Pinky pinky, int frames) {
		_grid = grid;
		_pacman = pacman;
		_ghosts = Arrays.asList(blinky, clyde, inky, pinky);
		_timer = new Timer(1000 / frames, this);
	}
	
	/**
	 * Move the actors within the grid and perform collision detection between actors. 
	 * This method also checks for win/loss conditions.
	 */
	public void actionPerformed(ActionEvent e) {
		_pacman.move(getActors(), _grid);
		detectCollision();
		 
		// If there is a collision and pacman cannot consume the ghost, then
		// the game is over. Otherwise, move the ghosts and continue execution.
		 for(Ghost ghost : _ghosts)
			 ghost.move(getActors(), _grid);
		 
		 detectCollision();
		 detectWinCondition();
	}
	
	private void detectWinCondition() {
		if(hasWon())
			stop();
	}
	
	private void detectCollision() {
		for(Ghost ghost : _ghosts)
			if(_grid.isCollision(_pacman, ghost) 
					&& !_pacman.consume(ghost))
				stop();
	}
	
	/** 
	 * Add an action listener to the timer so that other classes can
	 * get updates from when the game refreshes. This is used by the
	 * graphical interface as a cue to update the UI.
	 * 
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		_timer.addActionListener(listener);
	}
		
	/** Start execution of the game. */
	public void start() {
		_timer.start();
	}
	
	/** Returns whether or not the game is executing. */
	public boolean isRunning() {
		return _timer.isRunning();
	}

	/** Returns whether or not the game has been won. */
	public boolean hasWon() {
		return _grid.isEmpty();
	}
	
	/** Terminate execution of the game. */
	public void stop() {
		_timer.stop();
	}
	
	public void reset() {
		for(Actor actor : getActors())
			actor.reset();
		_grid.reset();
	}
	
	public Grid getGrid() {
		return _grid;
	}
	
	public PacMan getPacMan() {
		return _pacman;
	}
	
	public List<Ghost> getGhosts() {
		return _ghosts;
	}
	
	public List<Actor> getActors() {
		List<Actor> actors = new ArrayList<Actor>();
		actors.add(_pacman);
		
		for(Ghost ghost : _ghosts)
			actors.add(ghost);
		
		return actors;
	}
		
	public Dimension getDimensions() {
		return new Dimension(_grid.getWidth() * Grid.TILE_SIZE,
				_grid.getHeight() * Grid.TILE_SIZE);
	}
	
	/**
	 * Returns the number of frames that the specified time takes. This is dependent
	 * on the game's frame rate. High frame rate games will take more frames over a
	 * specified period of time.
	 * 
	 * @param millis time
	 * @return number of frames
	 */
	public static int frames(long millis) {
		return (int) (millis / 1000.0 * Game.FRAMES_PER_SECOND);
	}
	
	/**
	 * Create a game using properties defined in the properties file. Changing these properties allows for
	 * dynamic construction of games. It also decouples the execution code from the props. This way the game
	 * can be played on any number of valid PacMan game boards.
	 * 
	 * @param props
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws ClassNotFoundException 
	 */
	public static final Game load(Properties props) throws IOException, URISyntaxException, ClassNotFoundException {
		int frames = Integer.valueOf(props.getProperty("game.fps"));
		props.load(new FileInputStream(new File(props.getProperty("game.map.properties"))));
		
		// Load Map File
		FileInputStream fis = new FileInputStream(new File(props.getProperty("grid.file")));
		byte[] bytes = new byte[fis.available()];
		fis.read(bytes);
		fis.close();
		String text = new String(bytes, "UTF-8").replaceAll("\\s", "");
		
		// Initialize Grid
		String map = text.replaceAll("\\D", "0");
		int width  = Integer.valueOf(props.getProperty("grid.width"));
		int height = Integer.valueOf(props.getProperty("grid.height"));
		Grid grid  = Grid.load(map, width, height);
		
		// Initialize PacMan
		double pacmanSpeed	= Double.valueOf(props.getProperty("pacman.speed"));
		Boolean enableAi    = Boolean.valueOf(props.getProperty("game.enable.ai"));
		NeuralNet neural    = (!enableAi) ? null : NeuralNet.load(new File(props.getProperty("game.neural")));
		
		PacMan pacman = (enableAi) ? 
				new PacManAi   (find(grid, text, 'M'), pacmanSpeed, neural) : 
				new PacManHuman(find(grid, text, 'M'), pacmanSpeed);
				
		// Ghost Properties
		Point2D ghostExit = find(grid, text, 'E');
		Difficulty difficulty = Difficulty.valueOf(props.getProperty("ghost.difficulty"));
		Blinky blinky = new Blinky(find(grid, text, 'B'), find(grid, text, 'b'), ghostExit, Mode.IDLE, difficulty);
		Clyde clyde   = new Clyde (find(grid, text, 'C'), find(grid, text, 'c'), ghostExit, Mode.IDLE, difficulty);
		Inky inky 	  = new Inky  (find(grid, text, 'I'), find(grid, text, 'i'), ghostExit, Mode.IDLE, difficulty);
		Pinky pinky   = new Pinky (find(grid, text, 'P'), find(grid, text, 'p'), ghostExit, Mode.IDLE, difficulty);
	
		return new Game(grid, pacman, blinky, clyde, inky, pinky, frames);
	}
	
	private static final Point2D find(Grid grid, String text, char val) {
		int index = text.indexOf(val);
		int width = grid.getWidth();
		
		return grid.snap(new Point(index % width, index / width));
	}
}
