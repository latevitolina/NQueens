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
    
    //initialize with suggested amount of nodes and cache
    BDDFactory boardBDD = JFactory.init(nodes,cache);
    
    //initialize BDD to be true
    private BDD rules = boardBDD.one();

    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.board = new int[x][y];
        this.positions = x*y;
    }

   
    public int[][] getGameBoard() {
        return board;
    }
    
    
    public BDD setRules(){
    	boardBDD.setVarNum(positions);
    	for(int i=0; i<x; i++){
    		for (int j=0; j<y; j++){
    		rules.andWith(addPositionRules(i,j));
    		}
    	}
    	rules.andWith(oneQueenPerRowRule());
    	return rules;
    }

    private BDD addPositionRules(int col, int row){
    	BDD rule = boardBDD.one();
    	//TODO implement implications here
    	// position implies all those conjunctions of nith
    	rule.andWith(horizontalRule(row));
    	rule.andWith(verticalRule(col));
    	rule.andWith(diagonalRule(col,row));
    	
    	return boardBDD.ithVar(x*row +col).imp(rule);
    }
    
    private BDD horizontalRule(int position){
    	BDD rule = boardBDD.one();

    	//TODO loop in the same row, add each place as nith to the conjunction
    	return rule;
    }
    
    private BDD verticalRule(int position){
    	BDD rule = boardBDD.one();

    	//TODO loop in the same column, add each place as nith to the conjunction
    	return rule;
    }
    
    private BDD diagonalRule(int col, int row){
    	BDD rule = boardBDD.one();

    	//TODO loop in both diagonals, add each place as nith to the conjunction
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

		return rule;
	}
    
    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        
        board[column][row] = 1;
        
        //TODO restrict the BDD with queens position
        
        //if satcount == 1 put queens in their places
        
        //TODO loop through positions to either add crosses or place the rest of the queens in
      
        return true;
    }
}
