package com.ashwin.fri.pacman.display;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * A sprite represents a location within a sprite sheet. 
 * 
 * @author ashwin
 */
public class Sprite {

	/** The location of the sprite sheet. */
	public static final String SHEET = "./assets/sprites/sprites.png";
	public static final int SHEET_WIDTH = 10;
	public static final int SHEET_HEIGHT = 10;
	
	/** The width of each tile in the sprite sheet. */
	public static final int SIZE = 100;
	
	/** 
	 * The x and y coordinates of the sprite and the number of rows
	 * and columns that this particular sprite spans. 
	 */
	private int _index, _xSpan, _ySpan;
	
	public Sprite(int index) {
		this(index, 1, 1);
	}
	
	public Sprite(int index, int xSpan, int ySpan) {
		_xSpan = xSpan;
		_ySpan = ySpan;
		setIndex(index);
	}
	
	/** Set the current sprite index. */
	public void setIndex(int index) {
		if(index < 0 || index >= SHEET_WIDTH * SHEET_HEIGHT)
			throw new IllegalArgumentException("Invalid sprite index");
		_index = index;
	}
	
	/**
	 * This method returns a rectangle defining the location of this sprite
	 * within the sprite sheet. It is used to render the sprite by the graphics
	 * engine.
	 * 
	 * @return location of the sprite within sprite sheet
	 */
	public Rectangle getDimensions() {
		int x = (_index % SHEET_WIDTH) * SIZE;
		int y = (_index / SHEET_WIDTH) * SIZE;
		return new Rectangle(x, y, _xSpan * SIZE, _ySpan * SIZE);
	}
	
	public void draw(Graphics g, BufferedImage sheet, Rectangle dest) {
		Rectangle rect = this.getDimensions();
		g.drawImage(sheet, dest.x, dest.y, dest.x + dest.width, dest.y + dest.height, 
				rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
	}
	
}
