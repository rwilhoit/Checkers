package com.seniorproject.checkers.ai;

import java.util.ArrayList;
import java.util.Arrays;

import com.seniorproject.checkers.Game;
import com.seniorproject.checkers.Piece;

public class AI_Genetic{
	//Stores the number of variable types used as weights
	final private int numTypeWeights = 3;
	
	//Amount to vary the weights by
	final private float weightVariance = 0.01f;
	
	//Number of turns to wait for no pieces to change
	//before a draw is called
	final private int maxTurn_NoPieceChange = 50;
	
	//Array of the genetic players
	//Number of players equals twice the number type of weights
	//If x is the weight type
	//then players at 2*x are decreased for that weight
	//and players at 2*x+1 are increased for that weight
	private AI_Genetic_Player[] players;
	final private int numPlayers = numTypeWeights * 2;
	
	//Number of players to average into the parent
	final private int numTopPlayers = 3;
	
	//Scores of each player
	//+10 for wins
	//-10 for losses
	//+1 for ties
	private int[] playerScores;
	
	//Used to store the weights of the current parent that the child players are made from
	//weights[0] = numPieces
	//weights[1] = numKings
	//weights[2] = numSide
	private float[] weights;
	
	//Constructors
	public AI_Genetic(){
		players = new AI_Genetic_Player[numPlayers];
		playerScores = new int[numPlayers];
		weights = new float[numTypeWeights];
		
		//Sets all weights to an equal percentage and scores to 0
		for (int i = 0; i < numTypeWeights; i++){
			weights[i] = 1.0f/(float)numTypeWeights;
		}
		
		for (int i = 0; i < numPlayers; i++){
			playerScores[i] = 0;
		}
	}
	
	public AI_Genetic(float weight_numPieces, float weight_numKings){
		players = new AI_Genetic_Player[numPlayers];
		playerScores = new int[numPlayers];
		
		weights[0] = weight_numPieces;
		weights[1] = weight_numKings;
		
		for (int i = 0; i < numPlayers; i++){
			playerScores[i] = 0;
		}
	}
	
	//For each weight type, create two players: one with an increased weight, one with a decreased weight
	//For the increased weight, all weight types except for the current one are decreased by weightVarience/(numTypeWeights - 1)
	// then the current weight is increased by weightVariance
	//For the decreased weight, the reverse is done
	private void createPlayers(){
		int playerIndex = 0;
		for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
			//Copies the weight array
			float[] weightsCopy1 = Arrays.copyOf(weights, numTypeWeights);
			
			//Decrement the chosen weight and increments everything else
			weightsCopy1[weightIndex] -= weightVariance;
			for (int weightIndex_NotCurrent = 0; weightIndex_NotCurrent < numTypeWeights; weightIndex_NotCurrent++){
				if (weightIndex_NotCurrent != weightIndex){
					weightsCopy1[weightIndex_NotCurrent] += weightVariance/(float)(numTypeWeights - 1);
				}
			}
			
			players[playerIndex] = new AI_Genetic_Player(weightsCopy1, numTypeWeights);
			
			//Copies the weight array
			float[] weightsCopy2 = Arrays.copyOf(weights, numTypeWeights);
			
			//Increment the chosen weight and increments everything else
			weightsCopy2[weightIndex] += weightVariance;
			for (int weightIndex_NotCurrent = 0; weightIndex_NotCurrent < numTypeWeights; weightIndex_NotCurrent++){
				if (weightIndex_NotCurrent != weightIndex){
					weightsCopy2[weightIndex_NotCurrent] -= weightVariance/(float)(numTypeWeights - 1);
				}
			}
			players[playerIndex + 1] = new AI_Genetic_Player(weightsCopy2, numTypeWeights);
			
			playerIndex += 2;
		}
	}
	
	//Plays every player against every player and changes the current weights array
	//to be a combination of the top three weight changes based on score
	public void run(){
		//Creates the players
		createPlayers();
		
		//For every player except the last one...
		for (int player1 = 0; player1 < numPlayers - 1; player1++){
			//play against every other player except the ones already played
			for (int player2 = player1 + 1; player2 < numPlayers; player2++){
				int winner = getWinner(player1, player2);
				int loser = winner == player1 ? player2 : player1;
				
				if (winner != -1){	
					playerScores[winner] += 10;
					playerScores[loser] -= 10;
				}
				else{
					playerScores[player1] += 1;
					playerScores[player2] += 1;
				}
			}
		}
		
		//After the tournament, take the top players and changes the parent weights to their average 
		ArrayList<Integer> topPlayersScores = new ArrayList<Integer>();
		ArrayList<Integer> topPlayersIndex = new ArrayList<Integer>();
		
		topPlayersScores.add(playerScores[0]);
		topPlayersIndex.add(0);
		
		//Finds and store the top players
		for(int playerIndex = 1; playerIndex < numPlayers; playerIndex++){
			for (int topIndex = 0; topIndex < topPlayersScores.size(); topIndex++){
				if(playerScores[playerIndex] > topPlayersScores.get(topIndex)){
					topPlayersScores.add(topIndex, playerScores[playerIndex]);
					topPlayersIndex.add(topIndex, playerIndex);
					playerIndex++;
				}
				else if (topPlayersScores.size() < numTopPlayers){
					topPlayersScores.add(playerScores[playerIndex]);
					topPlayersIndex.add(playerIndex);
					playerIndex++;
				}
			}
		}
		
		//Reset the weights
		for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
			weights[weightIndex] = 0.0f;
		}
		
		//Averages the top players together and stores them as the weights
		for (int topIndex = 0; topIndex < numTopPlayers; topIndex++){
			for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
				float weight = players[topPlayersIndex.get(topIndex)].getWeights()[weightIndex];
				weights[weightIndex] += weight/(float)numTopPlayers;
			}
		}
		

	}
	
	//Given the index of the two players to be played, return the index of the winning player
	//Returns -1 if it was a draw
	private int getWinner(int player1, int player2){
		Game game = new Game();
		game.start();
		int numTurns_NoPieceChange = 0;
		int numPieces_Player1 = 12;
		int numKings_Player1 = 0;
		int numPieces_Player2 = 12;
		int numKings_Player2 = 0;
		
		players[player1].setGame(game);
		players[player2].setGame(game);
		
		//player1 is always first to move
		int currentPlayer = player1;
		
		while(game.getValidMoves().size() != 0){
			//Makes a move for that player
			game.makeMove(players[currentPlayer].makeMove());
			
			//Draw Detection
			//If no pieces change for the max turn, return -1
			int numPieces = 0;
			int numKings = 0;
			Piece[][] board = game.getBoard();
			
			//Increments for each piece and king found for the current player
			for (int i = 0; i < 8; i++){
				for (int j = 0; j < 8; j++){
					if(currentPlayer == player1){
						if(board[i][j] == Piece.RED){
							numPieces++;
						}
						else if (board[i][j] == Piece.RED_KING){
							numKings++;
						}
					}
					else if (currentPlayer == player2){
						if(board[i][j] == Piece.BLACK){
							numPieces++;
						}
						else if (board[i][j] == Piece.BLACK_KING){
							numKings++;
						}
					}
				}
			}
			
			//If the number of pieces and kings matches the last amount
			//increment draw detection
			if (currentPlayer == player1){
				if (numPieces_Player1 == numPieces && numKings_Player1 == numKings){
					numTurns_NoPieceChange++;
				}
				
				//Set the number pieces and king for the current king to the current board's numbers
				numPieces_Player1 = numPieces;
				numKings_Player1 = numKings;
			}
			else {
				if (numPieces_Player2 == numPieces && numKings_Player2 == numKings){
					numTurns_NoPieceChange++;
				}
				
				//Set the number pieces and king for the current king to the current board's numbers
				numPieces_Player2 = numPieces;
				numKings_Player2 = numKings;
			}
			
			//If numTurns_NoPieceChange reaches the max, return -1
			if (numTurns_NoPieceChange == maxTurn_NoPieceChange){
				return -1;
			}
			
			//Switches the current player
			currentPlayer = currentPlayer == player1 ? player2 : player1;
		}
		
		//The winner is the player that forced the other player to run out of moves
		//Thus if the while loop exits, the current player is the winner.
		return currentPlayer;
	}
	
	public float[] getCurrentWeights(){
		return weights;
	}
	
	public int getNumWeights(){
		return numTypeWeights;
	}
}