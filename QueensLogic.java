/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.util.*;

import net.sf.javabdd.*;

public class QueensLogic {
    private int x = 0;
    private int y = 0;
    private int[][] board;
    private final int nodes = 2000000;
    private final int cache = 200000;
    private int positions;
    
    private HashSet<Integer> marriedQueens = new HashSet<Integer>();
    
    //initialize with suggested amount of nodes and cache
    BDDFactory boardBDD = JFactory.init(nodes,cache);
    
    //initialize BDD to be true
    private BDD rules = boardBDD.one();
    
    private BDD prenupt = boardBDD.one();

    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.board = new int[x][y];
        this.positions = x*y;
        
        setRules();
    }

   
    public int[][] getGameBoard() {
        return board;
    }
    
    
    public void setRules(){
    	boardBDD.setVarNum(positions);
    	for(int i=0; i<x; i++){
    		for (int j=0; j<y; j++){
    			// i -> cols; j -> rows
	    		addPositionRules(i,j);
    		}
    	}
    	rules.andWith(oneQueenPerRowRule());
//    	boardBDD.printAll();
//    	return rule;
    }

    private BDD addPositionRules(int col, int row){
    	BDD rule = boardBDD.ithVar(row * x + col);
//    	System.out.println((row * x + col) + " ###################### ");
//    	System.out.println(rule);    	
//    	System.out.println("RULE FOR: " + row + " / " + col + " -- " + (row * x + col));
    	
    	// position implies all those conjunctions of nith
    	rule.andWith(horizontalRule(row * x + col));
    	rule.andWith(verticalRule(row * x + col));
    	rule.andWith(diagonalRule(col, row));
//      	System.out.println(rule);
    	return rule;
    }
    
    
    private BDD horizontalRule(int varId){
    	BDD rule = boardBDD.one();
    	for(int col = 0; col < x; col++){
    		int curPos = col + x * (varId / y);
    		if (curPos == varId) continue;
    		rule.andWith(boardBDD.nithVar(curPos));
    	}
    	rules.andWith(boardBDD.ithVar(varId).imp(rule));
    	return rule;
    }
    
    private BDD verticalRule(int varId){
    	BDD rule = boardBDD.one();
    	// loop in the same column and add each position as nith to the conjunction
    	int col = varId % x;
    	for(int row = 0; row < y; row++) {
	    	if(varId != row * x + col) {
//	    		System.out.println(row + " / " + col + " -- " + (row * x + col));
	    		rule.andWith(boardBDD.nithVar(row * x + col));
	    	}
    	}
    	rules.andWith(boardBDD.ithVar(varId).imp(rule));
    	return rule;
    }
    
    private BDD diagonalRule(int positionCol, int positionRow){
    	BDD rule = boardBDD.one();
    	// loop in both diagonals, add each place as nith to the conjunction
    	int[][] directions = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
    	//System.out.println("---- " + positionRow  + " -- " + positionCol + " ---");
    	for (int[] vector : directions) {
	    	int row = positionRow + vector[0];
	    	int col = positionCol + vector[1];
	    	while (row >= 0 && row < y && col >= 0 && col < x){
		    	rule.andWith(boardBDD.nithVar(row * x + col));		    	
		    	row += vector[0];
		    	col += vector[1];
	    	}
    	}
    	rules.andWith(boardBDD.ithVar(positionRow * x + positionCol).imp(rule));
    	return rule;
    }
        
    public BDD oneQueenPerRowRule() {
		BDD rule = boardBDD.one();
		for (int row = 0; row < y; row++) { 
			BDD rowRule = boardBDD.zero(); 

			for (int col = 0; col < x; col++) { 
				rowRule.orWith(boardBDD.ithVar(row * x + col));
			}
			rule.andWith(rowRule);
		}
		rules.andWith(boardBDD.one().imp(rule));
		return rule;
	}
    
    private BDD getPrenuptClauses(){
    	BDD r = boardBDD.one();
    	for (int q : marriedQueens){
    		r.andWith(boardBDD.ithVar(q));
    	}
    	return r;
    }
    
    private void updatePrenuptial(){
    	prenupt = rules.restrict(getPrenuptClauses());
    }
    
    private void divorceQueen(int varId){
    	marriedQueens.remove(varId);
    	updatePrenuptial();
    }
    
    private void marryQueen(int varId){
    	marriedQueens.add(varId);
    	updatePrenuptial();
    }
    
    public boolean insertQueen(int column, int row) {        
        // loop through positions to either add crosses or place the rest of the queens in
      
         if (board[column][row] == -1) { //clicked red cross, do nothing
             return true;
         }
         
         //if satcount == 1 put queens in their places
         if (board[column][row] == 1) { //if queen is already here, remove it
             board[column][row] = 0;
             divorceQueen(row * x + column);
         } else { 	 
        	 board[column][row] = 1; 
             marryQueen(row * x + column);
             marriedQueens.add(row * x + column);
         }
         
         boolean solved = prenupt.pathCount() == 1; //if there's only one path leading to TRUE in the restricted BDD, consider the problem solved
         
         for(int r = 0; r < y; r++) { // column
			 for(int c = 0; c < x; c++) { // row
				 if(board[c][r] == 1) continue; //if queen is present, skip this space

				//if a queen here makes the problem unsolvable, add a red cross to the board
				 if(prenupt.restrict(boardBDD.ithVar(r * x + c)).isZero()) { 
					 board[c][r] = -1;
				 } else if (solved) {
					 board[c][r] = 1;
					 marryQueen(r * x + c);
				 } else {
					 board[c][r] = 0; //remove red cross that might be leftover form removed queen
				 }
			 }
         }
        
         return true;
    }
}
