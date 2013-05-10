import java.util.ArrayList;
import java.util.Scanner;

public class Checkers {
	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		Game game = new Game();
		game.start();
		String inMove;
		
		System.out.println("Welcome to Checkers");
		
		while (game.getValidMoves().size() != 0){
			do{
				System.out.println("-----------------------");
				System.out.println("Current Player: " + game.getCurrentPlayer());
				System.out.println("");
				game.printBoard();
				System.out.println("");
				System.out.print("Move: ");
				inMove = scan.nextLine();
			}
			while (game.getValidMoves().size() != 0 && !game.makeMove(Character.getNumericValue(inMove.charAt(0)), Character.getNumericValue(inMove.charAt(1)), Character.getNumericValue(inMove.charAt(2)), Character.getNumericValue(inMove.charAt(3))));
		}
		
		scan.close();
	}
}

//Enum for the pieces in the game
enum Piece{
EMPTY, RED, RED_KING, BLACK, BLACK_KING, OUTSIDE
}

//Used to represent the data of the board
class Board implements Cloneable{
	/*
	 * A 2D array representation of the board
	 * The inner array is the x position
	 * The outer array is the y position
	 * 
	 * 	7   B   B   B   B
	 * 	6 B   B   B   B
	 * 	5   B   B   B   B
	 * 	4 
	 * 	3
	 * 	2 R   R   R   R
	 * 	1   R   R   R   R
	 * 	0 R   R   R   R
	 * 	  0 1 2 3 4 5 6 7
	 * 
	 * So board[0][0] is a red piece at the bottom left corner
	 */
	Piece[][] board = new Piece[8][8];
	
	//Default empty constructor 
	public Board(){	
	}
	
	//Initializes the board with starting set up
	public void init(){
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				//Sets red pieces
				if ((y == 0 || y == 2) && x%2 == 0) board[x][y] = Piece.RED;
				else if (y == 1 && x%2 == 1) board[x][y] = Piece.RED;
				//Sets black pieces
				else if ((y == 5 || y == 7) && x%2 == 1) board[x][y] = Piece.BLACK;
				else if (y == 6 && x%2 == 0) board[x][y] = Piece.BLACK;
				//Sets empty spots
				else if ((y == 4 && x%2 == 0) || (y == 3 && x%2 == 1)) board[x][y] = Piece.EMPTY;
				//Sets remainder of board to outside the playable area
				else board[x][y] = Piece.OUTSIDE;
			}
		}
	}

	public Board clone(){
		Board cloneBoard = new Board();
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				cloneBoard.setPiece(x, y, board[x][y]);
			}
		}
		
		return cloneBoard;
	}
	
	//Clone constructor
	public Board(Board inBoard){
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				board[x][y] = inBoard.getPiece(x, y);
			}
		}
	}
	
	//Returns the piece at the specified coordinate
	public Piece getPiece(int x, int y){
		return board[x][y];
	}
	
	//Sets the spot at the coordinates to the specified piece
	public void setPiece(int x, int y, Piece inPiece){
		board[x][y] = inPiece;
	}
	
	//Print the board to the terminal
	public void printBoard(){
		for (int y = 7; y >= 0 ; y--){
			System.out.print("" + y + " ");
			for (int x = 0; x < 8; x++){
				if (board[x][y] == Piece.RED_KING) System.out.print("R ");
				else if (board[x][y] == Piece.BLACK_KING) System.out.print("B ");
				else if (board[x][y] == Piece.RED) System.out.print("r ");
				else if (board[x][y] == Piece.BLACK) System.out.print("b ");
				else System.out.print("  ");
			}
			System.out.println();	
		}
		System.out.println("  0 1 2 3 4 5 6 7");
	}
}

/* 
 * Class that implements the actual checkers games
 * Handles all the rules for the game and controls for the current player
 * Moves must be passed through the Game class
 */
class Game implements Cloneable{
	Board board = new Board();
	char currentPlayer = 'R';
	
	/*	
	 * An arraylist used to store the valid moves of the current player
	 * Moves are stored as numbers of the positions
	 * The first char is a bool for whether that move is a jumping move or a normal move
	 * The next two chars are x and y of the piece to be moved
	 * The next two chars are the x and y of new valid location
	 * Every pair of chars after are the x and y of the jumps for multiple jumps
	 * The valid moves must be performed completely meaning if that string is chosen
	 * 	the piece from follow every jump listed in the string
	 *	
	 * Example: f0213 means a valid move is to move the piece from (0,2) to (1,3)
	 * Example: t022446 means a valid move is a double jumping move from (0,2) to (2,4) to (4,6)
	 */
	private ArrayList<String> validMoves = new ArrayList<String>();
		
	//Used to ensure that if a jump is possible, 
	private boolean hasJumps = false;
	//Used to ensure that the same jump aren't repeated in an endless cycle
	//The arraylist stores strings of the coordinates of the already jumped spots as a 2-char string
	private ArrayList<String> alreadyJumped = new ArrayList<String>();
	
	//Empty constructor
	public Game(){}
	
	//Clone constructor
	public Game(Board board, char currentPlayer, ArrayList<String> validMoves){
		this.board = board.clone();
		this.currentPlayer = currentPlayer;
		
		for (int i = 0; i < validMoves.size(); i++){
			this.validMoves.add(validMoves.get(i));
		}
	}
	
	//Clone method
	public Game clone(){
		return new Game(board, currentPlayer, validMoves);
	}
	
	//Starts the game
	public void start(){
		board.init();
		currentPlayer = 'R';
		setValidMoves();
	}
	
	//Returns the current player
	public char getCurrentPlayer(){
		return currentPlayer;
	}
	
	//Returns the arraylist of valid moves
	public ArrayList<String> getValidMoves(){
		return validMoves;
	}
	
	//Prints out the board
	public void printBoard(){
		board.printBoard();
	}
	
	//Performs the given move returning true if successful
	public boolean makeMove(int startX, int startY, int endX, int endY){
		//Checks the start piece is for the correct player
		if(((board.getPiece(startX, startY) == Piece.BLACK || 
				board.getPiece(startX, startY) == Piece.BLACK_KING) &&
				currentPlayer == 'R') ||
				((board.getPiece(startX, startY) == Piece.RED || 
				board.getPiece(startX, startY) == Piece.RED_KING) &&
				currentPlayer == 'B'))
			return false;
		
		//String representation of the first move
		String firstMove = "" + startX + startY + endX + endY;
		
		//Checks to see if the move is normal single spot move and is valid
		//If so, perform the move, then switch the player and find the new valid moves
		//To speed up the search, it first checks if the validMoves list contains a jump
		//If it does have a jump, then the list for sure won't have the single move
		if (Math.abs(endX - startX) == 1 && validMoves.get(0).charAt(0) != 't' && validMoves.contains("f" + firstMove)){
			//First copy the original piece to the new spot
			board.setPiece(endX, endY, checkKing(endY, board.getPiece(startX, startY)));
			//Then set the original spot to empty
			board.setPiece(startX, startY, Piece.EMPTY);
			//Change the current player
			currentPlayer = (currentPlayer == 'R' ? 'B' : 'R');
			//Set the valid moves for the new player
			setValidMoves();
			return true;
		}
		
		//The rest of the code is for potential jumps
		//Stores all valid moves that have the inputted first move into reducedValidMoves
		ArrayList<String> reducedValidMoves = new ArrayList<String>();
		for (String move: validMoves)
			if (move.substring(1, 5).equals(firstMove))
				reducedValidMoves.add(move);
		
		//If reducedValidMoves has no entry, then the move was not valid
		if (reducedValidMoves.size() == 0)
			return false;
		/*
		 * Checks if the jump was a single jump
		 * If so, simply perform the jump, change players, and reset validMoves
		 */
		else if (reducedValidMoves.get(0).length() == 5){
			//First copy the original piece to the new spot
			board.setPiece(endX, endY, checkKing(endY, board.getPiece(startX, startY)));
			//Then set the original spot to empty
			board.setPiece(startX, startY, Piece.EMPTY);
			//Then set the jumped spot to empty
			board.setPiece((startX + endX)/2, (startY + endY)/2, Piece.EMPTY);
			//Change the current player
			currentPlayer = (currentPlayer == 'R' ? 'B' : 'R');
			//Set the valid moves for the new player
			setValidMoves();
			return true;
		}
		/*
		 * If the move was a double jump or larger
		 * Perform the first jump, then remove that first jump
		 * from all the jump moves in the reducedValidMoves list.
		 * Finally setValidMoves to be this reducedValidMoves list
		 */
		else{
			//First copy the original piece to the new spot
			board.setPiece(endX, endY, board.getPiece(startX, startY));
			//Then set the original spot to empty
			board.setPiece(startX, startY, Piece.EMPTY);
			//Then set the jumped spot to empty
			board.setPiece((startX + endX)/2, (startY + endY)/2, Piece.EMPTY);
			
			for(int i = 0; i < reducedValidMoves.size(); i++)
				reducedValidMoves.set(i, "t" + reducedValidMoves.get(i).substring(3));
			
			validMoves = reducedValidMoves;
			return true;
		}
		
		
	}
	
	/*
	 * Checks if the end y position would king the piece
	 * If so, return the king'ed version
	 */
	private Piece checkKing(int endY, Piece piece){
		if (piece == Piece.RED && endY == 7)
			return Piece.RED_KING;
		else if (piece == Piece.BLACK && endY == 0)
			return Piece.BLACK_KING;
		else 
			return piece;
	}
	
	//Finds all the valid moves and stores them into validMove list
	private void setValidMoves(){
		//Resets hasJumps to false and clears validMoves
		hasJumps = false;
		validMoves.clear();
		
		
		//Searches through the entire board
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				//Checks to only get the moves of pieces for the current player
				if ((currentPlayer == 'B' && (board.getPiece(x, y) == Piece.BLACK || board.getPiece(x, y) == Piece.BLACK_KING)) ||
					(currentPlayer == 'R' && (board.getPiece(x, y) == Piece.RED || board.getPiece(x, y) == Piece.RED_KING))){
					//Resets the alreadyJumped list
					alreadyJumped.clear();
					validMoves.addAll(checkSurroundings(x, y, board.getPiece(x, y)));
				}
			}
		}
		
		//If hasJumps was ever enabled, then all the non-jump moves need to removed
		if (hasJumps)
			for (int i = 0; i < validMoves.size(); i++)
				if (validMoves.get(i).charAt(0) == 'f')
					validMoves.remove(i);
	}
	
	/*
	 * Given a position and the color of the piece at that position
	 * Return all the possible moves for that piece store in the list
	 * If a jump is possible, then the method will change the global variable hasJumps to true
	 * Later if hasJumps is true, then all the non-jump moves will be removed
	 */
	private ArrayList<String> checkSurroundings(int x, int y, Piece color){
		ArrayList<String> moves = new ArrayList<String>();
		
		//Used to set the opponent of the colored piece
		Piece 	opponent = Piece.EMPTY, 
				opponentKing = Piece.EMPTY;
		
		if ((color == Piece.RED) || color == Piece.RED_KING){
			opponent = Piece.BLACK;
			opponentKing = Piece.BLACK_KING;
		}
		else{
			opponent = Piece.RED;
			opponentKing = Piece.RED_KING;
		}
		
		//Checks all four squares around the piece
		for (int i = -1; i <= 1; i+=2){ //Modifier for x
			for (int j = -1; j <= 1; j+=2){ //Modifier for y
				//First makes sure that the four squares are still within the board
				if (x + i >= 0 && x + i < 8 && y + j >= 0 && y + j < 8){
					//Checks to make sure the piece is moving in the correct direction
					if ((color == Piece.RED && j == 1) || color == Piece.RED_KING || (color == Piece.BLACK && j == -1) || color == Piece.BLACK_KING){
						//First checks for possible jumps
						if ((board.getPiece(x+i, y+j) == opponent || board.getPiece(x+i, y+j) == opponentKing) && !alreadyJumped.contains("" + (x+i) + (y+j))){
							if (x + (2*i) >= 0 && x + (2*i) < 8 && 
								y + (2*j) >= 0 && y + (2*j) < 8 &&
								board.getPiece(x + (2*i), y + (2*j)) == Piece.EMPTY){
								
								//Sets has jumps to be true and adds the jumped location to alreadyJumped
								hasJumps = true;
								alreadyJumped.add("" + (x+i) + (y+j));
								
								//Initialize the inputMove to contain the jump boolean, the first coord, and the first jump
								String inputMove = "t" + x + y + (x + (2*i)) + (y + (2*j));
								
								//Obtain a list of all following jumps
								ArrayList<String> extraJumps = new ArrayList<String>(); 
								
								//If the jump kings the piece, than there can be no further jumps
								if (!((color == Piece.RED && y + (2*j) == 7) || (color == Piece.BLACK && y + (2*j) == 0)))
									extraJumps = checkSurroundings(x + (2*i), y + (2*j), color);
								
								//Adds the original jump if there are no extra jumps
								if (extraJumps.size() == 0)
									moves.add(inputMove);
								/*
								 * Otherwise for each extra jump entry, remove the normal front input
								 * add the rest of the jump moves to a copy of inputMove
								 * and add that string chain to the moves list
								 */
								else
									for (int k = 0; k < extraJumps.size(); k++)
										moves.add(inputMove + extraJumps.get(k).substring(3));
							}//Checks spot past piece is empty
						}//Checks for jumps
						//Checks for single moves
						else if (!hasJumps && board.getPiece(x+i, y+j) == Piece.EMPTY){
							moves.add("f" + x + y + (x + i) + (y + j));
						}
					} //Check correct direction
				} //Check inside board
			} //for j -1 to 1
		} //for i -1 to 1
		
		return moves;
	}
	
	
	
	
	
}

