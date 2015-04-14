package com.ashwin.fri.pacman.display;

import com.ashwin.fri.pacman.actor.Actor.Orientation;

/**
 * Animated sprites are associated with actors and have additional properties
 * that allow them to be animated and oriented.
 * 
 * @author ashwin
 *
 */
public class AnimatedSprite extends Sprite {

	private int _frames, _counter, _base;
	
	public AnimatedSprite(int base, Orientation dir) {
		this(base, dir, 1);
	}
	
	public AnimatedSprite(int base, Orientation dir, int frames) {
		this(base, dir, 1, 1, frames);
	}
	
	public AnimatedSprite(int base, Orientation dir, int xSpan, int ySpan, int frames) {
		super(base, xSpan, ySpan);
		
		_base = base;
		_frames = frames;
		_counter = -1;
		nextFrame(dir);
	}
	
	/**
	 * Move to the next frame of the animation. This method increments
	 * the counter and sets the index of the sprite to the proper value.
	 */
	public AnimatedSprite nextFrame(Orientation dir) {
		int index = _base + dir.ordinal() * _frames;
		_counter = (_counter + 1) % _frames;
		setIndex(index + _counter);
		return this;
	}
}
