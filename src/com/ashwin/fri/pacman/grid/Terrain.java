package com.ashwin.fri.pacman.grid;

import com.ashwin.fri.pacman.display.Sprite;

/**
 * The various kinds of Terrains that are used to construct the Pac-Man map.
 * Each terrain type is mapped to a sprite in the terrain.png file.
 * 
 * @author ashwin
 * 
 */
public enum Terrain {
	
	EMPTY(true),
	FOOD(true),
	ENERGIZER(true),
	
	HORIZONTAL(false),
	VERTICAL(false),
	UPPER_LEFT(false),
	UPPER_RIGHT(false),
	LOWER_LEFT(false),
	LOWER_RIGHT(false),
	GATE(false);
	
	private Sprite _sprite;
	private boolean _isPassable;
	
	private Terrain(boolean isPassable) {
		_sprite = new Sprite(this.ordinal());
		_isPassable = isPassable;
	}
	
	/**
	 * Returns whether or not actors can always move through this type of
	 * terrain. Note: Terrain that actors can sometimes move through are not
	 * considered passable, they are only interpreted as passable when certain
	 * conditions are met.
	 * 
	 * @return true if always passable, false otherwise
	 */
	public boolean isPassable() {
		return _isPassable;
	}
	
	/**
	 * Returns the sprite associated with this terrain object. This is used to
	 * render the game.
	 * 
	 * @return sprite associated with terrain
	 */
	public Sprite getSprite() {
		return _sprite;
	}
}
