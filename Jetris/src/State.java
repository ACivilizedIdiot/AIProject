
//This is the class that is stored as a representation of that state
//I may have gotten the dimesions of the tetris field wrong so you can change them as
//as you wish
public class State {
	
	static int[][] grid = new int[Tetris.ROWS][Tetris.COLUMNS];
	static int[] maxColumnHeights = new int[Tetris.COLUMNS];	//used to store max heights for each column (state utility)

	
	
	//values to assess utility of state
	int[] columnHeights = new int[10];	//heighest occupied cell in each column 
	int maxHeight = 0;	//heighest occupied cell in ANY column 
	int emptyCells = 0;
		
	public State(int[][] grid){
		State.grid = grid;
		this.emptyCells = numOfEmptyCells();
		
	}
	
	public State(int[] columnHeights, int maxHeight, int emptyCells, int[][] grid, int score){
		this.columnHeights = columnHeights;
		this.maxHeight = maxHeight;
		this.emptyCells = emptyCells;
		this.grid = grid;
		
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
		return new State(deepClone(grid));
	}
	
	public void printState(){
		//printing out max height of state before next piece enters game
//		System.out.println("Max Height: " + maxHeight);
//				System.out.println("Column Height Data: " + Arrays.toString(columnHeights));
//				System.out.println("Empty Cells: " + emptyCells);
				//System.out.println("Score :" + score);
	}
	
	//NICOLAS
		//returns maximum height of any column in current state
		//this will be used for state representation and utility eval
		private int maxHeight(){
			int max = 0;
			 for(int i = 0; i < Tetris.COLUMNS; i++){
				 if(max < maxColumnHeights[i]){
					 max = maxColumnHeights[i];
				 }
			 }
			return max;
		}
		
		//NICOLAS
		//returns array with max height values for each column
		//this will be used for state representation and utility eval
		private static int[] maxColumnHeights(){
			
			int max = 0;
			for(int i = Tetris.COLUMNS-1; i >= 0; i--){
				for(int j = Tetris.ROWS-1; j >= 0; j--){
					if(grid[j][i] != -1){
						max = Tetris.ROWS - j;
					}
					maxColumnHeights[i] =  max;				
				}
				max = 0;			
			}		
			return maxColumnHeights;
		}
		
		//NICOLAS
		//returns number of unoccupied cells on the board
		//count underneath every columns max height, do not count empty cells above 
		private static int numOfEmptyCells(){
			int emptyCells = 0;
			maxColumnHeights();
			for(int i = 0; i < Tetris.COLUMNS; i++){
				for(int j = Tetris.ROWS - maxColumnHeights[i]; j < Tetris.ROWS ; j++){
					if(grid[j][i] == -1){
						emptyCells++;
					}
				}
			}
			//number of unfilled cells underneath max heights. 
			return emptyCells;	
		}
	
	

}
