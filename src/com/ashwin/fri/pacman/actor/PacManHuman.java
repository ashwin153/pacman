package com.ashwin.fri.pacman.actor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.List;

import com.ashwin.fri.pacman.grid.Grid;

public class PacManHuman extends PacMan implements KeyListener {
	
	private Orientation _next;

	public PacManHuman(Point2D initial, double speed) {
		this(initial, speed, Orientation.LEFT);
	}
	
	public PacManHuman(Point2D initial, double speed, Orientation dir) {
		super(initial, speed, dir);
		_next = dir;
	}
	
	@Override
	public Orientation getNextOrientation(List<Actor> actors, Grid grid) {
		Point2D adj = grid.adjacent(getCurrentPosition(), _next, 1);
		
		if(!canMove(grid.get(adj)))
			_next = getCurrentOrientation();
		
		return _next;
	}

	public void keyPressed(KeyEvent e) {		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_DOWN:	_next = Orientation.DOWN;  break;
			case KeyEvent.VK_UP:	_next = Orientation.UP;    break;
			case KeyEvent.VK_LEFT:	_next = Orientation.LEFT;  break;
			case KeyEvent.VK_RIGHT:	_next = Orientation.RIGHT; break;
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	
}
