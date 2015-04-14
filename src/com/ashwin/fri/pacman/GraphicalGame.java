package com.ashwin.fri.pacman;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.ashwin.fri.pacman.actor.Actor;
import com.ashwin.fri.pacman.actor.Ghost;
import com.ashwin.fri.pacman.actor.PacManHuman;
import com.ashwin.fri.pacman.display.Sprite;
import com.ashwin.fri.pacman.grid.Grid;

public class GraphicalGame extends JFrame {

	private static final long serialVersionUID = 5424617868101358688L;

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("./pacman.properties")));
		
		new GraphicalGame(Game.load(props));
	}
	
	public GraphicalGame(Game game) throws IOException, FontFormatException, URISyntaxException {
		this.setContentPane(new GamePanel(game));
		this.setTitle("PacMan - Ashwin Madavan");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocation(200, 100);
		this.pack();
		this.setVisible(true);
	}
	
	private class GamePanel extends JPanel implements ActionListener {
		
		private static final long serialVersionUID = -8880591303686090656L;
		private static final String FONT_FILE = "./assets/fonts/namco.ttf";
		
		private BufferedImage _sprites;
		private Game _game;
		private Font _font;
		
		public GamePanel(Game game) throws IOException, FontFormatException, URISyntaxException {		
			
			_sprites = ImageIO.read(new File(Sprite.SHEET));
			_font = Font.createFont(Font.TRUETYPE_FONT, 
					new File(GamePanel.FONT_FILE))
					.deriveFont(12f);
	        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(_font);
	        
			_game = game;
			_game.addActionListener(this);
			_game.start();
			
			this.setPreferredSize(_game.getDimensions());
			this.setBackground(Color.BLACK);

			// If the PacMan is a human player, then register its key listener so that it
			// can receive and respond to key events.
			if(_game.getPacMan() instanceof PacManHuman) {
				this.addKeyListener((PacManHuman) _game.getPacMan());
				this.setFocusable(true);
				this.requestFocusInWindow();
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			this.repaint();
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			drawGrid(g, _sprites, _game.getGrid());
			drawScore(g);
			
			int actorSize = (int) (Grid.TILE_SIZE * 1.5);
			drawActor(g, _sprites, _game.getPacMan(), actorSize);
			for(Ghost ghost : _game.getGhosts())
				drawActor(g, _sprites, ghost, actorSize);
		}

		public void drawActor(Graphics g, BufferedImage sheet, Actor actor, int size) {
			Point2D pos = actor.getCurrentPosition();
			Rectangle dest = new Rectangle((int) (pos.getX() - size / 2), 
										   (int) (pos.getY() - size / 2), 
										   size, size);
			actor.getSprite().draw(g, sheet, dest);
		}
		
		public void drawGrid(Graphics g, BufferedImage sheet, Grid grid) {		
			for(int x = 0; x < grid.getWidth() * Grid.TILE_SIZE; x += Grid.TILE_SIZE) {
				for(int y = 0; y < grid.getHeight() * Grid.TILE_SIZE; y += Grid.TILE_SIZE) {
					Point2D.Double pos = new Point2D.Double(x, y);				
					Rectangle dest = new Rectangle((int) pos.x, (int) pos.y, Grid.TILE_SIZE, Grid.TILE_SIZE);
					grid.get(pos).getSprite().draw(g, sheet, dest);
				}
			}
		}
		
		public void drawScore(Graphics g) {
			g.setFont(_font);
			g.setColor(Color.YELLOW);
			g.drawString("score: " + _game.getPacMan().getPoints(), 10, 20);
		}
	}
}
