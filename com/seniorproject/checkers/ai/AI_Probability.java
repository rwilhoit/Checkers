package com.seniorproject.checkers.ai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.seniorproject.checkers.Game;

public class AI_Probability{
	//A hashmap that stores the probability array for each board state
	private HashMap<String, int[]> boardMoves = new HashMap<String, int[]>();
	private char color;
	private ArrayList<String> gameBoards = new ArrayList<String>();
	private ArrayList<Integer> gameMoves = new ArrayList<Integer>();
	private int countRandomMoves = 0;
	
	//The game currently being played
	private Game game;
	
	//Empty constructor
	public AI_Probability(){
	}
	
	//Basic game constructor
	public AI_Probability(char c){
		color = c;
	}
	
	public void setGame(Game game){
		this.game = game;
	}
	
	//Returns the index within the current validMoves array of the desired move
	public int makeMove(){
		int[] moveStats = getHashMoves();
		ArrayList<Integer> moveChances = new ArrayList<Integer>();
		
		for (int i = 0; i < moveStats.length; i++){
			for (int j = 0; j < moveStats[i]; j++){
				moveChances.add(i);
			}
		}
		
		int move;
		if(moveChances.size() == 0){
			countRandomMoves++;
			move = (int)(Math.random()*game.getValidMoves().size());
		}
		else{
			countRandomMoves = 0;
			move = moveChances.get((int)(Math.random()*moveChances.size()));
		}
			
		gameMoves.add(move);
		return move;
	}
	
	public char getColor(){
		return color;
	}
	
	//Returns the int[] value for the current board
	//If there is none for the current board, add a new int[] array for the board key
	private int[] getHashMoves(){
		String boardSerial = game.serializeBoard(color);
		int[] moveStats = boardMoves.get(boardSerial);
		gameBoards.add(boardSerial);
		
		if (moveStats == null){
			moveStats = new int[game.getValidMoves().size()];
			Arrays.fill(moveStats, 10);
			boardMoves.put(boardSerial, moveStats);
		}
		
		return moveStats;
	}
	
	public void endGame(boolean isWinner){
		int[] moveStats;
		int change = isWinner ? 1 : -1;
		
		for (int i = 0; i < gameBoards.size(); i++){
			moveStats = boardMoves.get(gameBoards.get(i));
			moveStats[gameMoves.get(i)] += change;
			moveStats[gameMoves.get(i)] = moveStats[gameMoves.get(i)] + change < 0 ? 0 : moveStats[gameMoves.get(i)] + change; 
			boardMoves.put(gameBoards.get(i), moveStats);
		}
		
		gameBoards.clear();
		gameMoves.clear();
	}
	
	public boolean isEndless(){
		return countRandomMoves == 5;
	}
	
	public void saveHashtoFile() throws IOException{
		File file = new File("Hash File");
		FileOutputStream f = new FileOutputStream(file);
		ObjectOutputStream s = new ObjectOutputStream(f);
		s.writeObject(boardMoves);
		s.close();
	}
	
	public void readFiletoHash() throws IOException, ClassNotFoundException{
		File file = new File("Hash File");
		FileInputStream f = new FileInputStream(file);
		ObjectInputStream s = new ObjectInputStream(f);
		boardMoves = (HashMap<String, int[]>) s.readObject();
		s.close();
	}
}


