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
	 * Array of the wieghts to be used
	 * 
	 * 0. Number of regular pieces -DONE
	 * 1. Number of kings -DONE
	 * 2. Number of possible moves
	 * 3. Number of pieces along the sides
	 * 4. Number of pieces along the bottom
	 * 5. Number of possible moves that avoid a threat(possible jump) 
	 * 6. Number of pieces next to an ally
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
			
			//Here we call each attribute score and add them to the total score
			//which we then add to the weightedScore arraylist
			float totalScore = 0.0f;
			totalScore += getScore_TotalPieces(gameClone, currentPlayer);
			totalScore += getScore_TotalKings(gameClone, currentPlayer);
			totalScore += getScore_TotalSides(gameClone, currentPlayer);
			
			weightedScore.add(totalScore);
		}
	}
	
	/****************************************
	 * getScore methods used to total points
	 ****************************************/
	
	//Counts the total pieces not including kings and add the scores
	private float getScore_TotalPieces(Game game, char currentPlayer){
		Piece[][] board = game.getBoard();
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
	private float getScore_TotalKings(Game game, char currentPlayer){
		Piece[][] board = game.getBoard();
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
	
	//Counts the total pieces including kings along the side 
	private float getScore_TotalSides(Game game, char currentPlayer){
		Piece[][] board = game.getBoard();
		int numSide = 0;
		
		for (int i = 0; i < 8; i++){
			//Left side has sides at 0,0 0,2 0,4 and 0,8
			if(i%2 == 0){
				if (currentPlayer == 'R'){
					if(board[0][i] == Piece.RED || board[0][i] == Piece.RED_KING){
						numSide++;
					}
				}
				else{
					if(board[0][i] == Piece.BLACK || board[0][i] == Piece.BLACK_KING){
						numSide++;
					} 
				}
			}
			else if(i%2 == 1){
				if (currentPlayer == 'R'){
					if(board[7][i] == Piece.RED || board[7][i] == Piece.RED_KING){
						numSide++;
					}
				}
				else{
					if(board[7][i] == Piece.BLACK || board[7][i] == Piece.BLACK_KING){
						numSide++;
					} 
				}
			}
		}
		
		return weights[2] * (float)numSide;
	}
	
}


