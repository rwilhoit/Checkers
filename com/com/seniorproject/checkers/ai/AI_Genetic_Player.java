package com.seniorproject.checkers.ai;

import java.util.ArrayList;

import com.seniorproject.checkers.Game;
import com.seniorproject.checkers.Piece;
/*
 * Used by the AI_Genetic to play the game given a weighted set of attributes
 */
public class AI_Genetic_Player {
	//The game currently being played
	private Game game;

	/*
	 * Array of the weights to be used
	 * 
	 * 0. Number of pawns -DONE
	 * 1. Number of kings -DONE
	 * 2. Number of pawns on the side -DONE
	 * 3. Number of kings on the side -DONE
	 * 4. Number of possible pawn moves - DONE
	 * 5. Number of possible king moves - DONE
	 * 6. Total distance of pawns to the other edge -DONE
	 * 7. Number of empty slots on the other edge -DONE
	 * 8. Number of pieces on the bottom two rows (Defenders) -DONE
	 * 9. Number of pieces on the top three rows (Attacker) -DONE
	 * 10. Number of pawns in the center eight 
	 * 11. Number of kings in the center eight
	 * 12. Number of pawns on the main diagonal
	 * 13. Number of kings on the main diagonal
	 * 14. Number of pawns on the double diagonal
	 * 15. Number of kings on the double diagonal
	 * 16. Number of pawns surrounded by no one
	 * 17. Number of kings surrounded by no one
	 * 18. Number of holes (an empty spot surrounded by 3 of the same color
	 * 19. Triangle
	 * 20. Oreo
	 * 21. Bridge
	 * 22. Dog
	 * 23. Pawn in corner
	 * 24. King in opposite corner
	 */
	private float[] weights;
	
	//A weight score array corresponding to the current valid move list
	private ArrayList<Float> weightedScore;
	
	//Basic game constructor
	public AI_Genetic_Player(float[] weights, int numTypeWeights){
		this.weights = new float[numTypeWeights];
		
		for (int i = 0 ; i < numTypeWeights; i++){
			this.weights[i] = weights[i];
		}
	}
	
	public void setGame(Game game){
		this.game = game;
	}
	
	public float[] getWeights(){
		return weights;
	}
	
	//Returns the index within the current validMoves array of the desired move
	public int makeMove() {
		//Sets the score for each move
		setWeightedScores();
		
		float max = Float.MIN_VALUE;
		int index = -1;
		
		//Return the move with the highest code
		for (int i = 0; i < weightedScore.size(); i++){
			if (weightedScore.get(i) > max){
				max = weightedScore.get(i);
				index = i;
			}
		}
		
		return index;
	}
	
	/*
	 * Sets all the scores for all the valid moves for the current player
	 * Does this by cloning the board and performing the move and adding the scores
	 * from the resultant board
	 */
	private void setWeightedScores(){
		weightedScore = new ArrayList<Float>();
		ArrayList<String> moveList = game.getValidMoves();
		
		//Perform each valid move to compute the attributes for the next board
		//Add the score for the new board to the weightedScore list
		//Performs multiple jumps until completion
		for (int i = 0; i < moveList.size(); i++){
			Game gameClone = game.clone();
			
			char currentPlayer = gameClone.getCurrentPlayer();
			gameClone.makeMove(i);
			
			//This part checks to see if the current player didn't change
			//This only happens for double or greater jumps.
			//So it will perform all the jumps until completion so only 
			//the final board is checked
			while(gameClone.getCurrentPlayer() == currentPlayer){
				gameClone.makeMove(0);
			}
			
			Piece[][] board = gameClone.getBoard();
			//Here we call each attribute score and add them to the total score
			//which we then add to the weightedScore arraylist
			float totalScore = 0.0f;
			totalScore += getScore_TotalPieces(board, currentPlayer);
			totalScore += getScore_TotalKings(board, currentPlayer);
			totalScore += getScore_TotalPawnSides(board, currentPlayer);
			totalScore += getScore_TotalKingSides(board, currentPlayer);
			totalScore += getScore_TotalNewPawnMoves(gameClone, currentPlayer);
			totalScore += getScore_TotalNewKingMoves(gameClone, currentPlayer);
			totalScore += getScore_TotalDistanceFromEdge(board, currentPlayer);
			totalScore += getScore_TotalEmptyPromotionTiles(board, currentPlayer);
			totalScore += getScore_TotalDefenders(board, currentPlayer);
			totalScore += getScore_TotalAttackers(board, currentPlayer);
			totalScore += getScore_Triangle(board, currentPlayer);
			totalScore += getScore_Oreo(board, currentPlayer);
			totalScore += getScore_Bridge(board, currentPlayer);
			totalScore += getScore_Dog(board, currentPlayer);
			totalScore += getScore_CornerPawn(board, currentPlayer);
			totalScore += getScore_CornerKing(board, currentPlayer);

			//Here we add the opponent's score to maximize our gain and minimize theirs
			//NEW CODE <- This part was added to use a min max method 
			//ALSO IMPORTANT - I changed all the methods to accept the board instead of the
			//since all but two just read the board anyways
			//This should speed up the process since getBoard is only called once now instead once 
			//for each method
			float opponentScore = 0.0f;
			char opponentPlayer = (currentPlayer == 'R') ? 'B' :  'R';
			opponentScore += getScore_TotalPieces(board, opponentPlayer);
			opponentScore += getScore_TotalKings(board, opponentPlayer);
			opponentScore += getScore_TotalPawnSides(board, opponentPlayer);
			opponentScore += getScore_TotalKingSides(board, opponentPlayer);
			opponentScore += getScore_TotalNewPawnMoves(gameClone, opponentPlayer);
			opponentScore += getScore_TotalNewKingMoves(gameClone, opponentPlayer);
			opponentScore += getScore_TotalDistanceFromEdge(board, opponentPlayer);
			opponentScore += getScore_TotalEmptyPromotionTiles(board, opponentPlayer);
			opponentScore += getScore_TotalDefenders(board, opponentPlayer);
			opponentScore += getScore_TotalAttackers(board, opponentPlayer);
			opponentScore += getScore_Triangle(board, opponentPlayer);
			opponentScore += getScore_Oreo(board, opponentPlayer);
			opponentScore += getScore_Bridge(board, opponentPlayer);
			opponentScore += getScore_Dog(board, opponentPlayer);
			opponentScore += getScore_CornerPawn(board, opponentPlayer);
			opponentScore += getScore_CornerKing(board, opponentPlayer);

			weightedScore.add(totalScore - opponentScore);
		}
	}
	
	/****************************************
	 * getScore methods used to total points
	 ****************************************/
	
	//Counts the total pieces not including kings and add the scores
	private float getScore_TotalPieces(Piece[][] board, char currentPlayer){
		
		int numPieces = 0;
		
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if((board[i][j] == Piece.RED && currentPlayer == 'R')
					|| (board[i][j] == Piece.BLACK && currentPlayer == 'B'))
					numPieces ++;
			}
		}
		
		return weights[0] * (float)numPieces;
	}
	
	//Counts the total kings and add the scores
	private float getScore_TotalKings(Piece[][] board, char currentPlayer){
			
		int numKings = 0;
		
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if((board[i][j] == Piece.RED_KING && currentPlayer == 'R')
					|| (board[i][j] == Piece.BLACK_KING && currentPlayer == 'B'))
					numKings ++;
			}
		}
		
		return weights[1] * (float)numKings;
	}
	
	//Counts the total pieces not including kings along the side 
	private float getScore_TotalPawnSides(Piece[][] board, char currentPlayer){

		int numSide = 0;
		
		for (int i = 0; i < 8; i++){
			//Left side has sides at 0,0 0,2 0,4 and 0,8
			if(i%2 == 0){
				if (currentPlayer == 'R'){
					if(board[0][i] == Piece.RED){
						numSide++;
					}
				}
				else{
					if(board[0][i] == Piece.BLACK){
						numSide++;
					} 
				}
			}
			else if(i%2 == 1){
				if (currentPlayer == 'R'){
					if(board[7][i] == Piece.RED){
						numSide++;
					}
				}
				else{
					if(board[7][i] == Piece.BLACK){
						numSide++;
					} 
				}
			}
		}
		
		return weights[2] * (float)numSide;
	}
	
	//Counts the total kings along the side 
	private float getScore_TotalKingSides(Piece[][] board, char currentPlayer){
		
		int numSide = 0;
		
		for (int i = 0; i < 8; i++){
			//Left side has sides at 0,0 0,2 0,4 and 0,8
			if(i%2 == 0){
				if (currentPlayer == 'R'){
					if(board[0][i] == Piece.RED_KING){
						numSide++;
					}
				}
				else{
					if(board[0][i] == Piece.BLACK_KING){
						numSide++;
					} 
				}
			}
			else if(i%2 == 1){
				if (currentPlayer == 'R'){
					if(board[7][i] == Piece.RED_KING){
						numSide++;
					}
				}
				else{
					if(board[7][i] == Piece.BLACK_KING){
						numSide++;
					} 
				}
			}
		}
		
		return weights[3] * (float)numSide;
	}
	
	//Counts the total number of new moves available for pawns after performing the previous move
	private float getScore_TotalNewPawnMoves(Game game, char currentPlayer){
		return weights[4] * (float)game.getNumValidMoves(currentPlayer, false);
	}
	
	//Counts the total number of new moves available for pawns after performing the previous move
	private float getScore_TotalNewKingMoves(Game game, char currentPlayer){
		return weights[5] * (float)game.getNumValidMoves(currentPlayer, true);
	}
	
	//Counts the total distance of all pawns from the edge
	private float getScore_TotalDistanceFromEdge(Piece[][] board, char currentPlayer){
		
		int totalDistance = 0;
		
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (currentPlayer == 'R' && board[i][j] == Piece.RED){
					//The distance for red is 7 minus the y position
					totalDistance += (7 - j);
				}
				else if (currentPlayer == 'B' && board[i][j] == Piece.BLACK){
					//The distance for black is it's y position
					totalDistance += j;
				}
			}
		}
		
		return weights[6] * (float)totalDistance;
	}
	
	//Counts the total empty spots on the promotion line
	private float getScore_TotalEmptyPromotionTiles(Piece[][] board, char currentPlayer){
		
		int totalEmpty = 0;
		
		for (int i = 0; i < 8; i++){
			if (currentPlayer == 'R' && board[i][7] == Piece.EMPTY){
				totalEmpty++;
			}
			else if (currentPlayer == 'B' && board[i][0] == Piece.EMPTY){
				totalEmpty++;
			}
		}
		
		return weights[7] * (float)totalEmpty;
	}
	
	//Counts the total defending pieces (pieces on the bottom two rows)
	private float getScore_TotalDefenders(Piece[][] board, char currentPlayer){
		
		int totalDefenders = 0;
		
		for (int i = 0; i < 8; i++){
			if (currentPlayer == 'R'){
				if (board[i][0] == Piece.RED || board[i][0] == Piece.RED_KING)
					totalDefenders++;
				
				if (board[i][1] == Piece.RED || board[i][1] == Piece.RED_KING)
					totalDefenders++;
			}
			else if (currentPlayer == 'B'){
				if (board[i][7] == Piece.BLACK || board[i][7] == Piece.BLACK_KING)
					totalDefenders++;
				
				if (board[i][6] == Piece.BLACK || board[i][6] == Piece.BLACK_KING)
					totalDefenders++;
			}
		}
		
		return weights[8] * (float)totalDefenders;
	}
	
	//Counts the total attacking pieces (pieces on the top three rows)
	private float getScore_TotalAttackers(Piece[][] board, char currentPlayer){
		
		int totalAttacker = 0;
		
		for (int i = 0; i < 8; i++){
			if (currentPlayer == 'R'){
				if (board[i][7] == Piece.RED || board[i][7] == Piece.RED_KING)
					totalAttacker++;
				
				if (board[i][6] == Piece.RED || board[i][6] == Piece.RED_KING)
					totalAttacker++;
				
				if (board[i][5] == Piece.RED || board[i][5] == Piece.RED_KING)
					totalAttacker++;
			}
			else if (currentPlayer == 'B'){
				if (board[i][0] == Piece.BLACK || board[i][0] == Piece.BLACK_KING)
					totalAttacker++;
				
				if (board[i][1] == Piece.BLACK || board[i][1] == Piece.BLACK_KING)
					totalAttacker++;
				
				if (board[i][2] == Piece.BLACK || board[i][2] == Piece.BLACK_KING)
					totalAttacker++;
			}
		}
		
		return weights[9] * (float)totalAttacker;
	}
	
	//Six pattern features - features (20)-(25) can take only boolean values. 
	//(20) A Triangle - white pawns on squares 27, 31 and 32; 
	//(21) An Oreo - white pawns on squares 26, 30 and 31; 
	//(22) A Bridge - white pawns on squares 30 and 32; 
	//(23) A Dog - white pawn on square 32 and a black one on square 28; 
	//(24) A Pawn in the Corner - white man on square 29; 
	//(25) A King in the Corner - white king on square 4;
	//Checks if there is are any triangle formations
	
	//(20) A Triangle - white pawns on squares 27, 31 and 32; 
	private float getScore_Triangle(Piece[][] board, char currentPlayer) {
		int triangle = 0;
		
		if(currentPlayer == 'R' && board[4][0] == Piece.RED && board[5][1] == Piece.RED && board[6][0] == Piece.RED) {
			triangle++;
		}
		else if(currentPlayer == 'B' && board[1][7] == Piece.BLACK && board[2][6] == Piece.BLACK && board[3][7] == Piece.BLACK) {
			triangle++;
		}
		else {
			// Else nothing
		}
		return weights[19] * (float)triangle;
	}
	
	//(21) An Oreo - white pawns on squares 26, 30 and 31; 
	private float getScore_Oreo(Piece[][] board, char currentPlayer) {
		int oreo = 0;
		
		if(currentPlayer == 'R' && board[2][0] == Piece.RED && board[3][1] == Piece.RED && board[4][0] == Piece.RED) {
			oreo++;
		}
		else if(currentPlayer == 'B' && board[3][7] == Piece.BLACK && board[4][6] == Piece.BLACK && board[5][7] == Piece.BLACK) {
			oreo++;
		}
		else {
			// Else nothing
		}
		
		return weights[20] * (float)oreo;

	}
	
	//(22) A Bridge - white pawns on squares 30 and 32; 
	private float getScore_Bridge(Piece[][] board, char currentPlayer) {
		int bridge = 0;
		
		if(currentPlayer == 'R' && board[2][0] == Piece.RED && board[6][0] == Piece.RED) {
			bridge++;
		}
		else if(currentPlayer == 'B' && board[1][7] == Piece.BLACK && board[5][7] == Piece.BLACK) {
			bridge++;
		}
		else {
			// Else nothing
		}
		
		return weights[21] * (float)bridge;

	}
	
	//(23) A Dog - white pawn on square 32 and a black one on square 28; 
	private float getScore_Dog(Piece[][] board, char currentPlayer) {
		int dog = 0;
		
		if(currentPlayer == 'R' && board[6][0] == Piece.RED && board[7][1] == Piece.BLACK){
			dog++;
		}
		else if(currentPlayer == 'B' && board[1][7] == Piece.BLACK && board[0][6] == Piece.RED) {
			dog++;
		}
		else {
			// Else nothing
		}
		
		return weights[22] * (float)dog;

	}
	
	//(24) A Pawn in the Corner - white man on square 29; 
	private float getScore_CornerPawn(Piece[][] board, char currentPlayer) {
		int cornerPawn = 0;
		
		if (currentPlayer == 'R' && board[0][0] == Piece.RED) {
			cornerPawn++;
		}
		else if (currentPlayer == 'B' && board[7][7] == Piece.BLACK) {
			cornerPawn++;
		}
		else {
			// Else nothing
		}
		
		return weights[23] * (float)cornerPawn;

	}
	
	//(25) A King in the Corner - white king on square 4;
	private float getScore_CornerKing(Piece[][] board, char currentPlayer) {
		int cornerKing = 0;
		
		if (currentPlayer == 'R' && board[7][7] == Piece.RED_KING) {
			cornerKing++;
		}
		else if (currentPlayer == 'B' && board[0][0] == Piece.BLACK_KING) {
			cornerKing++;
		}
		else {
			// Else nothing
		}
		
		return weights[24] * (float)cornerKing;

	}
	
}


