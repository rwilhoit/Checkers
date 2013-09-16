package com.seniorproject.checkers;

import java.io.IOException;

import com.seniorproject.checkers.ai.AI_Probability;
import com.seniorproject.checkers.ai.AI_Random;

public class Checkers {
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		AI_Probability ai = new AI_Probability('B');
		AI_Random ai_r;
		String inMove;
		boolean isWinner;
		int count = 0;
		boolean multiJump;
		String ai_move;
		
		//ai.readFiletoHash();
		
		
		for (int i = 0; i < 10000; i++){
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
						
						if(ai_move.length() <= 4){
							multiJump = false;
						}
						ai_move = ai_move.substring(2);
					}
					else if (ai.getColor() == game.getCurrentPlayer()){
						ai_move = game.getValidMoves().get(ai.makeMove());
						
						if (ai_move.length() > 5){
							multiJump = true;
						}
						
						inMove = ai_move.substring(1, 5);
						ai_move = ai_move.substring(3);
					}
					else {
						inMove = game.getValidMoves().get(ai_r.makeMove()).substring(1, 5);	
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
			
			if (isWinner) count++;
		}
		
		ai.saveHashtoFile();
		System.out.println("Total wins: " + count);
	}
}
