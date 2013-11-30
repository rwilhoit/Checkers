package com.seniorproject.checkers;

import java.io.IOException;

import com.seniorproject.checkers.ai.AI_Genetic;

public class Checkers {
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		/*
		Game game = new Game();
		game.start();
		Scanner scan = new Scanner(System.in);
		
		float[] weights = new float[25];
		for (int i = 0; i < 25; i++){
			weights[i] = 30.0f;
		}
		
		AI_Genetic_Player ai = new AI_Genetic_Player(weights, 25);
		ai.setGame(game);
		
		while(true){
			if (game.currentPlayer == 'R'){
				game.printBoard();
				for (int i = 0; i < game.getValidMoves().size(); i++){
					System.out.println("Move " + i + ": " + game.getValidMoves().get(i));
				}
				int move = scan.nextInt();
				
				game.makeMove(move);
			}
			
			if (game.currentPlayer == 'B'){
				ai.setGame(game);
				
				game.printBoard();
				int aiMove = ai.makeMove();
				for (int i = 0; i < game.getValidMoves().size(); i++){
					System.out.println("Move " + i + ": " + game.getValidMoves().get(i));
				}
				System.out.println("AI's move = " + aiMove);
				game.makeMove(aiMove);
			}
		}
		*/
		
		AI_Genetic ai_genetic = new AI_Genetic();
		
		while(true){
			float[] weight = ai_genetic.getCurrentWeights();
			
			for (int j = 0; j < ai_genetic.getNumWeights(); j++){
				System.out.println("Weight " + j + ": " + weight[j]);
			}
			System.out.println();
				
			
			ai_genetic.run();
		}
		
		
		
		
		/*
		AI_Probability ai = new AI_Probability('B'); 	// AI class
		AI_Random ai_r;									// AI Random class 
		String inMove;									// The current move
		boolean isWinner;								// If the player has won
		int count = 0;									// The count
		boolean multiJump;								// If we are performing a multijump
		String ai_move;									// The AI's move
		
		//ai.readFiletoHash();
		
		for (int i = 0; i < 10000; i++) {
			Game game = new Game();
			game.start();
			
			ai_r = new AI_Random(game, 'R');
			ai.setGame(game);
			
			multiJump = false;
			ai_move = "";
			
			System.out.println("Welcome to Checkers");
			while (game.getValidMoves().size() != 0){
				do{
					System.out.println("-----------------------");
					System.out.println("Current Player: " + game.getCurrentPlayer());
					System.out.println("");
					game.printBoard();
					System.out.println("");
					
					if (multiJump){
						inMove = ai_move.substring(0,4);
						
						if(ai_move.length() <= 4) {
							multiJump = false;
						}
						ai_move = ai_move.substring(2);
					}
					else if (ai.getColor() == game.getCurrentPlayer()) {
						ai_move = game.getValidMoves().get(ai.makeMove());
						
						if (ai_move.length() > 5) {
							multiJump = true;
						}
						
						inMove = ai_move.substring(1, 5);
						ai_move = ai_move.substring(3);
					}
					else {
						inMove = game.getValidMoves().get(ai_r.makeMove()).substring(1,5);	
					}
					
					System.out.println();
					System.out.print("Possible Moves: ");
					for(String move: game.getValidMoves())
						System.out.print(move + " ");
					System.out.println("Move: " + inMove);
				}
				while (!ai.isEndless() && game.getValidMoves().size() != 0 && !game.makeMove(Character.getNumericValue(inMove.charAt(0)), Character.getNumericValue(inMove.charAt(1)), Character.getNumericValue(inMove.charAt(2)), Character.getNumericValue(inMove.charAt(3))));
			}
			
			isWinner = game.getCurrentPlayer() != ai.getColor();
			ai.endGame(isWinner && !ai.isEndless());
			
			if (isWinner) {
				count++;
			}
		}
		
		ai.saveHashtoFile();
		System.out.println("Total wins: " + count);
		*/
	}
}
