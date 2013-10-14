package com.seniorproject.checkers.ai;

import com.seniorproject.checkers.Game;
import com.seniorproject.checkers.ai.AI_Genetic_Player;

public class AI_Genetic{
	//Stores the number of variable types used as weights
	final int numTypeWeights = 2;
	
	//Amount to vary the weights by
	final float weightVariance = 0.5f;
	
	//Array of the genetic players
	//Number of players equals twice the number type of weights
	//If x is the weight type
	//then players at 2*x are decreased for that weight
	//and players at 2*x+1 are increased for that weight
	AI_Genetic_Player[] players;
	final int numPlayers = numTypeWeights * 2;
	
	//Number of players to average into the parent
	final int numTopPlayers = 3;
	int[] topPlayersIndex = new int[numTopPlayers];
	int[] topPlayersScores = new int[numTopPlayers];
	
	//Scores of each player
	//+10 for wins
	//-10 for losses
	//+1 for ties
	int[] playerScores;
	
	//Used to store the weights of the current parent that the child players are made from
	//weights[0] = numPieces
	float[] weights;
	
	//Constructors
	public AI_Genetic(){
		players = new AI_Genetic_Player[numPlayers];
		playerScores = new int[numPlayers];
		
		//Sets all weights to the default 1 and scores to 0
		weights[0] = 1.0f;
		for (int i = 0; i < numPlayers; i++){
			playerScores[i] = 0;
		}
	}
	
	public AI_Genetic(float weight_numPieces){
		players = new AI_Genetic_Player[numPlayers];
		playerScores = new int[numPlayers];
		
		weights[0] = weight_numPieces;
		for (int i = 0; i < numPlayers; i++){
			playerScores[i] = 0;
		}
	}
	
	private void createPlayers(){
		int playerIndex = 0;
		for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
			//Creates two players for each weight
			//One with increased and one decreased values
			weights[weightIndex] -= weightVariance;
			players[playerIndex] = new AI_Genetic_Player(weights);
			
			weights[weightIndex] += 2*weightVariance;
			players[playerIndex + 1] = new AI_Genetic_Player(weights);
			
			//Restores the weights back to the parent's default values
			weights[weightIndex] -= weightVariance; 
			
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
					playerScores[player1] += 0.1;
					playerScores[player2] += 0.1;
				}
			}
		}
		
		//After the tournament, take the top players and changes the parent weights to their average 
		
		//Reset the topPlayers scores and indexes
		for(int index = 0; index < numTopPlayers; index++){
			topPlayersScores[index] = 0;
			topPlayersIndex[index] = -1;
		}
		
		//Finds and store the top players
		for(int playerIndex = 0; playerIndex < numPlayers; playerIndex++){
			for(int topPlayersArrayIndex = 0; topPlayersArrayIndex < numTopPlayers; topPlayersArrayIndex++){
				if (playerScores[playerIndex] > topPlayersScores[topPlayersArrayIndex]){
					topPlayersScores[topPlayersArrayIndex] = playerScores[playerIndex];
					topPlayersIndex[topPlayersArrayIndex] = playerIndex;
				}
			}
		}
		
		//Reset the weights
		for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
			weights[weightIndex] = 0;
		}
		
		//Averages the top players together and stores them as the weights
		for (int topPlayersArrayIndex = 0; topPlayersArrayIndex < numTopPlayers; topPlayersArrayIndex++){
			for (int weightIndex = 0; weightIndex < numTypeWeights; weightIndex++){
				float weight = players[topPlayersIndex[topPlayersArrayIndex]].getWeights()[weightIndex];
				weights[weightIndex] += weight/numTypeWeights; 
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
		
		while(game.getValidMoves().size() != 0){
			//Makes a move for that player
			game.makeMove(players[currentPlayer].makeMove());
			//Switches the current player
			currentPlayer = currentPlayer == player1 ? player2 : player1;
		}
		
		//The winner is the player that forced the other player to run out of moves
		//Thus if the while loop exits, the current player is the winner.
		return currentPlayer;
		
		//TODO Implement a draw detection
	}
	
	public float[] getCurrentWeights(){
		return weights;
	}
}