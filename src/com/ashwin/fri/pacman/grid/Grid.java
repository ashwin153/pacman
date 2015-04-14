package com.ashwin.fri.pacman.grid;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.ashwin.fri.pacman.actor.Actor;
import com.ashwin.fri.pacman.actor.Actor.Orientation;

public class Grid {

	public static final int TILE_SIZE = 16;
	
	private Terrain[][] _grid, _init;
	
	public Grid(Terrain[][] grid) {
		_grid = grid;
		
		_init = new Terrain[_grid.length][_grid[0].length];
		for(int i = 0; i < _grid.length; i++)
			System.arraycopy(_grid[i], 0, _init[i], 0, _grid[i].length);
	}
	
	public Point2D getNearest(Point2D point, Terrain terrain) {
		List<Point2D> visited = new ArrayList<Point2D>();
		Queue<Point2D> queue = new LinkedList<Point2D>();
		visited.add(point);
		queue.add(point);
		
		while(!queue.isEmpty())  {
			Point2D top = queue.remove();
			if(get(top).equals(terrain))
				return top;
			
			for(Orientation dir : Orientation.values()) {
				Point2D adj = adjacent(top, dir, 1);
				if(!visited.contains(adj)) {
					visited.add(adj);
					queue.add(adj);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the terrain at the specified coordinates. These coordinates are
	 * internally translated into a tile.
	 * 
	 * @param pos position (pixels)
	 * @return terrain at the specified position
	 */
	public Terrain get(Point2D pos) {
		Point tile = tile(pos);
		return _grid[tile.y][tile.x];
	}
	
	public void set(Point2D point, Terrain terrain) {
		Point tile = tile(point);
		_grid[tile.y][tile.x] = terrain;
	}
	
	/** @return the number of rows in the grid. */
	public int getHeight() {
		return _grid.length;
	}
	
	/** @return the number of columns in the grid. */
	public int getWidth() {
		return _grid[0].length;
	}
	
	public void reset() {
		_grid = new Terrain[_init.length][_init[0].length];
		for(int i = 0; i < _init.length; i++)
			System.arraycopy(_init[i], 0, _grid[i], 0, _init[i].length);
	}
	
	/**
	 * Collisions in PacMan are determined when two points tiles are equal.
	 * Simply perform an equivalence check on the tiles of each point and
	 * return the resulting value.
	 * 
	 * @param p1
	 * @param p2
	 * @return whether or not the points collide
	 */
	public boolean isCollision(Point2D p1, Point2D p2) {
		return tile(p1).equals(tile(p2));
	}
	
	public boolean isCollision(Actor a1, Actor a2) {
		return isCollision(a1.getCurrentPosition(), a2.getCurrentPosition());
	}
	
	/**
	 * Returns true if there are still food and energizer terrain remaining
	 * in the grid and false otherwise. This method is used to determine if the
	 * player has won.
	 * 
	 * @return whether or not there is food/energizers remaining
	 */
	public boolean isEmpty() {
		for(int i = 0; i < _grid.length; i++)
			for(int j = 0; j < _grid[i].length; j++)
				if(_grid[i][j].equals(Terrain.FOOD) || 
						_grid[i][j].equals(Terrain.ENERGIZER))
					return false;
		return true;
	}
	
	/**
	 * Returns the tile that the coordinates are currently on. The tile that is
	 * returned is dependent on the size and shape of the grid.
	 * 
	 * @param pos
	 * @return tile that the coordinate is on
	 */
	private Point tile(Point2D point) {
		Point2D wrap = wrap(point);
		int pWidth  = Grid.TILE_SIZE * getWidth();
		int pHeight = Grid.TILE_SIZE * getHeight();
		
		return new Point((int) (wrap.getX() / pWidth * getWidth()),
				(int) (wrap.getY() / pHeight * getHeight()));
	}
	
	/**
	 * Performs wrap around on the given tile. This method prevents OutOfBoundsExceptions
	 * and enables the use of the teleportation tunnel.
	 * 
	 * @param point
	 * @return
	 */
	public Point2D wrap(Point2D point) {
		int pWidth  = Grid.TILE_SIZE * getWidth();
		int pHeight = Grid.TILE_SIZE * getHeight();
		return new Point2D.Double((point.getX() % pWidth + pWidth) % pWidth, 
								  (point.getY() % pHeight + pHeight) % pHeight);
	}
	
	/**
	 * Performs a snap to grid on the specified point. First, it translates
	 * the point into a tile coordinate, and then it determines and returns
	 * the center point of the specified tile.
	 * 
	 * @param point
	 * @return center point of current tile
	 */
	public Point2D snap(Point2D point) {
		return snap(tile(point));
	}
	
	public Point2D snap(Point tile) {
		return wrap(new Point2D.Double(tile.x * Grid.TILE_SIZE + Grid.TILE_SIZE / 2.0,
				tile.y * Grid.TILE_SIZE + Grid.TILE_SIZE / 2.0));
	}
	
	/**
	 * Returns the center Point2D of the tile in the specified direction
	 * that is dist tiles away. This is used in the targeting mechanisms
	 * for the various types of ghosts.
	 * 
	 * @param src
	 * @param dir
	 * @return center Point2D of the tile in the adjacent direction
	 */
	public Point2D adjacent(Point2D src, Orientation dir, double dist) {
		double pDist = dist * Grid.TILE_SIZE;
		
		switch(dir) {
			case UP: 	return snap(new Point2D.Double(src.getX(), src.getY() - pDist));
			case DOWN: 	return snap(new Point2D.Double(src.getX(), src.getY() + pDist));
			case LEFT: 	return snap(new Point2D.Double(src.getX() - pDist, src.getY()));
			default: 	return snap(new Point2D.Double(src.getX() + pDist, src.getY()));
		}
	}
	
	public Point2D adjacent(Actor actor, double dist) {
		return adjacent(actor.getCurrentPosition(), actor.getCurrentOrientation(), dist);
	}
	
	/**
	 * Loads a PacMap from a text file. The format for a PacMap file is defined as
	 * a text file (.txt) in which each character represents the ordinal of the desired
	 * terrain in the Terrain enum. The file must begin with two numbers that specify
	 * the width and height of the PacMap.
	 * 
	 * @param file file to load from
	 * @param tileSize size of each tile in pixels
	 * @return PacMap representation of the file's contents
	 * @throws IOException the file cannot be read
	 */
	public static Grid load(String map, int width, int height) throws IOException {
		Terrain[][] terrain = new Terrain[height][width];
		for(int i = 0; i < terrain.length; i++)
			for(int j = 0; j < terrain[i].length; j++)
				terrain[i][j] = Terrain.values()[map.charAt(i * width + j) - 48];
		return new Grid(terrain);
	}
}
