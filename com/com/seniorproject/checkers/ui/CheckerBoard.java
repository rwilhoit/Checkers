package com.seniorproject.checkers.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.seniorproject.checkers.Game;
import com.seniorproject.checkers.Piece;

public class Checkerboard extends JFrame {
	
	JLayeredPane layeredPane;
	JPanel checkerboardPanel;  
	JLabel checkerPiece;
	static Dimension boardSize = new Dimension(600, 600);
	int xAdjustment;
	int yAdjustment;
	
    private static double PIECE_DIMENSION = 111.75;
    private static String CHECKERBOARD = "checkerboard.png";
    private static String RED_CHECKER = "red_checker.png";
    private static String BLACK_CHECKER = "black_checker.png";
    private static String RED_KING = "red_king.png";
    private static String BLACK_KING = "black_king.png";
    static Game game;
    Point selectedLocation = null;
        
    public void setMouseListener() {
		// Setup the mouse listeners
	    this.addMouseListener(new MouseListener() {
	    
	    	@Override
	    	public void mouseClicked(MouseEvent event)
	    	{
	    		int selectedX = (int)Math.ceil((event.getPoint().x/PIECE_DIMENSION));
	    		int selectedY = (int)Math.ceil((event.getPoint().y/PIECE_DIMENSION));	    		
	    		System.out.println(selectedX + "," + selectedY);
	    		
	    		// If selected location is in bounds
	    		if(selectedLocationIsInBounds(selectedX,selectedY)) {
	    			System.out.println("Location was in bounds and selectedLocation is " + selectedLocation);
	    			// Check if we have already selected a piece
	    			if(selectedLocation != null) {
	    				// If we have then make the move and redraw the board
	    				game.makeMove(selectedLocation.x, selectedLocation.y, selectedX, selectedY);
	    				drawBoard(game);
	    				selectedLocation = null;
	    			}	
	    			else {
		    			selectedLocation = new Point(selectedX,selectedY);
	    			}
	    		} 
	    		else {
	    			System.out.println("Location was out of bounds");
	    		}	    		
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
    	 
    	for (int i = 0; i < 64; i++) {
    		  JPanel square = new JPanel( new BorderLayout() );
    		  checkerboardPanel.add(square);
    		 
    		  // Color the pieces
    		  int row = (i / 8) % 2;
    		  if (row == 0) {
    			  square.setBackground(i % 2 == 0 ? Color.darkGray : Color.red);  
    		  }
    		  else {
    			  square.setBackground(i % 2 == 0 ? Color.red : Color.darkGray);  
    		  }	  
    	 }
    	
    	Piece pieceAtLocation;
    	
    	 // Draw pieces based on where they are on the board
    	for(int i=0;i<8;i++) {
    		for(int j=0;j<8;j++) {
				pieceAtLocation = game.getStatusOfBoardLocation(j,i);
				if(pieceAtLocation == Piece.BLACK) {
					// Draw a black piece
					System.out.println("Drawing a black piece");
					drawPieceAtLocation(i,j,BLACK_CHECKER);
				}
				else if(pieceAtLocation == Piece.RED) {
					// Draw a red piece
					System.out.println("Drawing a red piece");
					drawPieceAtLocation(i,j,RED_CHECKER);
				}
				else if(pieceAtLocation == Piece.BLACK_KING) {
					// Draw a black king
					drawPieceAtLocation(i,j,BLACK_KING);
				}
				else if(pieceAtLocation == Piece.RED_KING) {
					// Draw a red 
					drawPieceAtLocation(i,j,RED_KING);
				}
				else {
					System.out.println("Empty or out of bounds location" + pieceAtLocation);
					// Else draw nothing
				}
    		}
    	}
    }
    
    private void drawPieceAtLocation(int x, int y, String pieceType) {
    	int componentLocation = x*8 + y;
    	System.out.println("x = " + x + " y = " + y);
    	System.out.println("Component location: " + componentLocation);
    	// Draw the piece at the location 
		JLabel piece = new JLabel(new ImageIcon(pieceType));
		
		// Figure out how to write the right number component
		JPanel panel = (JPanel)checkerboardPanel.getComponent(componentLocation);
		panel.add(piece);
	}

	public void drawBoard(Game game) {
		this.setupGameWindow();
		this.redrawBoard(game);
    }
    
	private void redrawBoard(Game game) {
	    this.setupBoard();
	    this.drawPieces(game);
	}
    private void setupGameWindow() {
    	layeredPane = new JLayeredPane();
    	getContentPane().add(layeredPane);
    	layeredPane.setPreferredSize(boardSize);	
	}
    
    public void setupBoard() {
    	// Add the board to the layered pane
    	checkerboardPanel = new JPanel();
    	layeredPane.add(checkerboardPanel, JLayeredPane.DEFAULT_LAYER);
    	checkerboardPanel.setLayout(new GridLayout(8, 8));
    	checkerboardPanel.setPreferredSize( boardSize );
    	checkerboardPanel.setBounds(0, 0, boardSize.width, boardSize.height);
    }

	public boolean selectedLocationIsInBounds(int selectedX, int selectedY) {    	
    	if(selectedX < 1 || selectedX > 8 || selectedY < 1 || selectedY > 8) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    public void selectBoardPiece(int x, int y) {
    	selectedLocation = new Point(x,y);
    }
    
	public static void main(String[] args) throws IOException {
	    Checkerboard checkerboard = new Checkerboard();
	    checkerboard.setMouseListener();
	    game = new Game();
		game.start();
	    checkerboard.drawBoard(game);
	    checkerboard.setDefaultCloseOperation(DISPOSE_ON_CLOSE );
	    checkerboard.pack();
	    checkerboard.setResizable(true);
	    checkerboard.setLocationRelativeTo(null);
	    checkerboard.setVisible(true);
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
