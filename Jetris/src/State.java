
//This is the class that is stored as a representation of that state
//I may have gotten the dimesions of the tetris field wrong so you can change them as
//as you wish
public class State {
	
	int[][] grid = new int[Tetris.ROWS][Tetris.COLUMNS];
	int score = 0;
	
	
	//values to assess utility of state
	int[] columnHeights = new int[10];	//heighest occupied cell in each column 
	int maxHeight = 0;	//heighest occupied cell in ANY column 
	int emptyCells = 0;
		
	public State(int[][] grid, int score, int empty){
		this.grid = grid;
		this.score = score;
		this.emptyCells = empty;
		
	}
	
	public State(int[] columnHeights, int maxHeight, int emptyCells, int[][] grid, int score){
		this.columnHeights = columnHeights;
		this.maxHeight = maxHeight;
		this.emptyCells = emptyCells;
		this.grid = grid;
		this.score = score;
		
	}
	
	private int[][] deepClone(int[][] grid2){
		int[][] newArray = new int[grid2.length][grid2[0].length];
		for(int i = 0; i < grid2.length; i++){
			newArray[i] = grid2[i].clone();
		}
		return newArray;
	}
		
	public boolean equals(State otherState){
		int[][] otherGrid = otherState.grid;
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				if(grid[i][j] != otherGrid[i][j]){
					return false;
					
				}
			}
		}		
		return true;
	}
	
	public int[][] getGrid(){
		return grid;
	}
	
	public State clone(){
		return new State(deepClone(grid), score, emptyCells);
	}
	
	public void printState(){
		//printing out max height of state before next piece enters game
//		System.out.println("Max Height: " + maxHeight);
//				System.out.println("Column Height Data: " + Arrays.toString(columnHeights));
//				System.out.println("Empty Cells: " + emptyCells);
				System.out.println("Score :" + score);
	}
	
	

}
