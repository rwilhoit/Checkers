package com.seniorproject.checkers.ai;

import java.util.ArrayList;
import java.util.Arrays;

import com.seniorproject.checkers.Game;
import com.seniorproject.checkers.Piece;

public class AI_Genetic{
	//Stores the number of variable types used as weights
	final private int numTypeWeights = 25;
	
	//Amount to vary the weights by
	final private float weightVariance = 150.0f;
	
	//Number of turns to wait for no pieces to change
	//before a draw is called
	final private int maxTurn_NoPieceChange = 100;
	
	//Array of the genetic players
	//Number of players equals twice the number type of weights
	//If x is the weight type
	//then players at 2*x are decreased for that weight
	//and players at 2*x+1 are increased for that weight
	private AI_Genetic_Player[] players;
	final private int numPlayers = numTypeWeights * 2;
	
	//Number of players to average into the parent
	final private int numTopPlayers = 15;
	
	//Scores of each player
	//+10 for wins
	//-10 for losses
	//+1 for ties
	private int[] playerScores;
	
	private float[] weights;
	
	//Constructors
	public AI_Genetic(){
		players = new AI_Genetic_Player[numPlayers];
		playerScores = new int[numPlayers];
		weights = new float[numTypeWeights];
		
		//Preset the weight to what I think they should be because the starting AI is RETARDED
		weights[0] = 1650.0f;
		weights[1] = 1650.0f;
		weights[2] = 1600.0f;
		weights[3] = 1630.0f;
		weights[4] = 1630.0f;
		weights[5] = 1630.0f;
		weights[6] = 1560.0f;
		weights[7] = 1570.0f;
		weights[8] = 1700.0f;
		weights[9] = 1520.0f;
		weights[10] = 1520.0f;
		weights[11] = 1770.0f;
		weights[12] = 1520.0f;
		weights[13] = 1510.0f;
		weights[14] = 1520.0f;
		weights[15] = 1550.0f;
		weights[16] = 1800.0f;
		weights[17] = 1880.0f;
		weights[18] = 1620.0f;
		weights[19] = 1500.0f;
		weights[20] = 1500.0f;
		weights[21] = 1500.0f;
		weights[22] = 1500.0f;
		weights[23] = 1500.0f;
		weights[24] = 1500.0f;
		
		
		
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
			
			//Decrement the chosen weight 
			weightsCopy1[weightIndex] -= weightVariance;
			
			players[playerIndex] = new AI_Genetic_Player(weightsCopy1, numTypeWeights);
			
			//Copies the weight array
			float[] weightsCopy2 = Arrays.copyOf(weights, numTypeWeights);
			
			//Increment the chosen weight and increments everything else
			weightsCopy2[weightIndex] += weightVariance;

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
				if (winner != -1){	
					playerScores[winner] += 10;
				}
			}
		}
		
		//After the tournament, take the top players and changes the parent weights to their average 
		int[] sortedScores = Arrays.copyOf(playerScores, playerScores.length);
		Arrays.sort(sortedScores);
		
		//Searches and stores the index of the top players using the sorted scores list
		int[] indexOfTopPlayers = new int[numTopPlayers];
		for (int i = 0; i < numTopPlayers; i++){
			for (int j = 0; j < playerScores.length; j++){
				if (playerScores[j] == sortedScores[numPlayers - (i + 1)]){
					indexOfTopPlayers[i] = j;
					playerScores[j] = -1;
					break;
				}
			}
		}
		
		//Reset the parent weights
		for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
			weights[weightIndex] = 0.0f;
		}
		
		//Averages the top players together and stores them as the weights
		for (int topIndex = 0; topIndex < numTopPlayers; topIndex++){
			for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
				float weight = players[indexOfTopPlayers[topIndex]].getWeights()[weightIndex];
				weights[weightIndex] += weight/(float)numTopPlayers;
			}
		}
		

	}
	
	//Given the index of the two players to be played, return the index of the winning player
	//Returns -1 if it was a draw
	private int getWinner(int player1, int player2){
		Game game = new Game();
		game.start();
		
		players[player1].setGame(game);
		players[player2].setGame(game);
		
		//player1 is always first to move
		int currentPlayer = player1;
		int lastNumPieces = 0;
		int lastNumKings = 0;
		int numMoves_noChanges = 0;
		
		while(game.getValidMoves().size() != 0){
			//Makes a move for that player
			game.makeMove(players[currentPlayer].makeMove());
			
			int numPieces = 0;
			int numKings = 0;
			
			for (int i = 0; i < 8; i++){
				for (int j = 0; j < 8; j++){
					if (game.getBoard()[i][j] == Piece.RED || game.getBoard()[i][j] == Piece.BLACK){
						numPieces++;
					}
					else if (game.getBoard()[i][j] == Piece.RED_KING || game.getBoard()[i][j] == Piece.BLACK_KING){
						numKings++;
					} 
				}
			}
			
			if (numPieces == lastNumPieces && numKings == lastNumKings){
				numMoves_noChanges++;
			}
			else {
				numMoves_noChanges = 0;
				lastNumPieces = numPieces;
				lastNumKings = numKings;
			}
			
			if (numMoves_noChanges > maxTurn_NoPieceChange){
				return -1;
			}
			
			//Switches the current player
			if (game.getCurrentPlayer() == 'R'){
				currentPlayer = player1;
			}
			else{
				currentPlayer = player2;
			}
			
			players[player1].setGame(game);
			players[player2].setGame(game);
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