package com.seniorproject.checkers.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.seniorproject.checkers.Game;
import com.seniorproject.checkers.Piece;

public class Checkerboard extends JFrame {
    
    private static final double PIECE_DIMENSION = 111.875;
    private static Game game;
    private static Point selectedLocation;
        
    public void setupBoard() {
	    ImagePanel panel = new ImagePanel(new ImageIcon("checkerboard.png").getImage());
	    this.getContentPane().add(panel);
	    this.pack();
	    this.setVisible(true);
    }

    public void setMouseListener() {
		// Setup the mouse listeners
	    this.addMouseListener(new MouseListener() {
	    
	    	@Override
	    	public void mouseClicked(MouseEvent event)
	    	{
	    		int selectedX = (int)Math.ceil((event.getPoint().x/PIECE_DIMENSION));
	    		int selectedY = (int)Math.ceil((event.getPoint().y/PIECE_DIMENSION));	    		
	    		
	    		// If selected location is in bounds
	    		if(selectedLocationIsInBounds(selectedX,selectedY)) {
	    			// Check if we have already selected a piece
	    			if(selectedLocation != null) {
	    				// If we have selected a piece see 
	    				//if the new location we have selected is the location of a legal move
	    				if(true) {
	    					game.makeMove(selectedLocation.x, selectedLocation.y, selectedX, selectedY);
	    					
	    					// Redraw the board
	    				}
	    				
	    			} 
	    			else {
	    				selectedLocation = new Point(selectedX,selectedY);
	    			}
	    		}
	    		
	    		
	    		
	    		//game.selectPiece(selectedLocation);
	    		//game.makeMove(0,0,(int)Math.ceil((event.getPoint().x/111.875)),(int)Math.ceil((event.getPoint().y/111.875)));	    		
	    		System.out.println((int)Math.ceil((event.getPoint().x/111.875)) + "," + (int)Math.ceil((event.getPoint().y/111.875)));
	    	}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
    }
	
    public void drawPieces(Game game) {
    	// Draw the board based on the pieces
    	System.out.println(game.serializeBoard('r'));
    }
    
    public void drawBoard(Game game) {
	    this.setupBoard();
	    this.drawPieces(game);
    }
    
    public boolean selectedLocationIsInBounds(int selectedX, int selectedY) {
    	
    	boolean locationIsInBounds = true;
    	
    	if(selectedX < 1 || selectedX > 8 || selectedY < 1 || selectedY > 8) {
    		locationIsInBounds = false;
    	}
    	else {
    		locationIsInBounds = true;
    	}
    	
		return false;
    }
    
    public void selectBoardLocation(int x, int y) {
    	// Check the board location
    	Piece piece = game.getStatusOfBoardLocation(x, y);
    	// EMPTY, RED, RED_KING, BLACK, BLACK_KING, OUTSIDE
    	if(piece == Piece.EMPTY) {
    		
    	} 
    	else if(piece == Piece.RED || piece == Piece.BLACK || piece == Piece.BLACK_KING || piece == Piece.RED_KING) {
    		
    	}
    	else if(piece == Piece.OUTSIDE) {
    		// Selected outside or something
    	}
    	else {
    		// Selected outside or something
    	}
    	
    	// Calls the mouse listener. If there is a piece there already then do nothing. 
    	
    	// If a piece has been selected then move the selected piece to the new spot
    	
    	// Null out selected piece
    	selectedLocation= null;
    }
    
    public void selectBoardPiece(int x, int y) {
    	selectedLocation = new Point(x,y);
    }
    
	public static void main(String[] args) throws IOException {
	
	    Checkerboard checkerboard = new Checkerboard();
	    checkerboard.setMouseListener();
	    game = new Game();
	    checkerboard.drawBoard(game);
	    

	}
}

class ImagePanel extends JPanel {

  private Image img;

  public ImagePanel(String img) {
    this(new ImageIcon(img).getImage());
  }

  public ImagePanel(Image img) {
    this.img = img;
    
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }

  public void paintComponent(Graphics g) {
	super.paintComponent(g);
    g.drawImage(img, 0, 0, null);
  }

}
