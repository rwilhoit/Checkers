package com.seniorproject.checkers.ai;

import com.seniorproject.checkers.Game;

public class AI_Random{
	private Game game;
	private char color; 
	
	public AI_Random(){
		
	}
	
	public AI_Random(Game g, char c){
		game = g;
		color = c;
	}
	
	public int makeMove(){
		return (int)(Math.random() * game.getValidMoves().size());
	}
	
	public char getColor(){
		return color;
	}
}
