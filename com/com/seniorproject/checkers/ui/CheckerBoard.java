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
	
    private static double PIECE_DIMENSION = 75;
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
	    		int selectedX = (int)Math.ceil((event.getPoint().x/PIECE_DIMENSION)) - 1; // (Subtract 1 to compensate for array index)
	    		int selectedY = (int)Math.ceil((event.getPoint().y/PIECE_DIMENSION)) - 1;	    		
	    		System.out.println(selectedX + "," + selectedY);
	    		
	    		if(selectedLocation == null && selectedLocationIsEmpty(selectedX,selectedY)) {
	    			System.out.println("Selected an empty location");
	    		}
	    		else if(selectedLocation == null && validatePieceSelection(selectedX,selectedY)) {
	    			selectedLocation = new Point(selectedX,selectedY);
	    			System.out.println("Selected a " + game.getPlayerForPiece(selectedLocation.x, selectedLocation.y) + " piece with location: " + selectedLocation);
	    		}
	    		else if(validateMoveSelection(selectedX,selectedY)) {
    				game.makeMove(selectedLocation.x, selectedLocation.y, selectedX, selectedY);
	    			game.printBoard();
	    			drawBoard(game);
	    			//redrawBoard(game);

    				System.out.println("Setting selectedLocation back to null");
    				selectedLocation = null;	
	    		}
	    		else {
	    			// Else nothing
	    		}
	    	}

			private boolean selectedLocationIsEmpty(int selectedX, int selectedY) {
				if(selectedLocationIsInBounds(selectedX,selectedY) && !pieceExistsInSelectedLocation(selectedX,selectedY)) {
					return true;
				}
				return false;
			}

			private boolean validateMoveSelection(int selectedX, int selectedY) {
				return (!(pieceExistsInSelectedLocation(selectedX,selectedY)) && selectedLocationIsInBounds(selectedX,selectedY));
			}

			private boolean validatePieceSelection(int selectedX, int selectedY) {
				if(selectedLocationIsInBounds(selectedX,selectedY) && pieceExistsInSelectedLocation(selectedX,selectedY) && selectedPieceIsAlly(selectedX,selectedY)) {
					return true;
				}
				
				return false;
			}

			private boolean selectedPieceIsAlly(int selectedX, int selectedY) {
				
				//Checks the start piece is for the correct player
				System.out.println("Checking if the move is valid, the current player is: " + game.getCurrentPlayer());
				if(((game.getStatusOfBoardLocation(selectedX, selectedY) == Piece.BLACK || 
					 game.getStatusOfBoardLocation(selectedX, selectedY) == Piece.BLACK_KING) &&
				   				  				 game.getCurrentPlayer() == 'B') ||
				   ((game.getStatusOfBoardLocation(selectedX, selectedY) == Piece.RED || 
				   	 game.getStatusOfBoardLocation(selectedX, selectedY) == Piece.RED_KING) &&
				   	 							 game.getCurrentPlayer() == 'R')) {
					
					return true;
				}
									
				return false;
			}

			private boolean pieceExistsInSelectedLocation(int selectedX, int selectedY) {
				if(game.getStatusOfBoardLocation(selectedX, selectedY) == null || game.getStatusOfBoardLocation(selectedX, selectedY) == Piece.EMPTY || game.getStatusOfBoardLocation(selectedX, selectedY) == Piece.OUTSIDE) {
					System.out.println("Piece did not exist at location: " + selectedX + "," + selectedY);
					return false;
				}
				return true;
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
					drawPieceAtLocation(i,j,BLACK_CHECKER);
				}
				else if(pieceAtLocation == Piece.RED) {
					// Draw a red piece
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
					// Empty or out of bounds location
				}
    		}
    	}
    }
    
    private void drawPieceAtLocation(int x, int y, String pieceType) {
    	System.out.println("Drawing a piece at x: " + x + " y: " + y);
    	int componentLocation = x*8 + y;
    	
    	// Draw the piece at the location 
		JLabel piece = new JLabel(new ImageIcon(pieceType));
		
		// Figure out how to write the right number component
		JPanel panel = (JPanel)checkerboardPanel.getComponent(componentLocation);
		panel.add(piece);
	}

	public void drawBoard(Game game) {
		this.setupGameWindow();
		this.redrawBoard(game);
		System.out.println("Finished drawing board");
    }
    
	private void redrawBoard(Game game) {
	    this.setupBoard();
	    this.drawPieces(game);
	    System.out.println("Redrew board");
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
    	if(selectedX < 0 || selectedX > 7 || selectedY < 0 || selectedY > 7) {
    		System.out.println("Selected location is out of bounds");
    		return false;
    	}
    	System.out.println("Selected location is in bounds");
    	return true;
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
