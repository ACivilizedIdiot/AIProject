import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

//Class in which all the data from the game passes
public class TetrisAI {

	HashSet<State> stateTable = new HashSet<State>(1000);

	private final double exploration = .1;

	// Will need to sort this data occasionally so that the odds of this taking
	// a long time
	// will be small
	ArrayList<TableEntry> recordedData = new ArrayList<TableEntry>();

	private int lastScoreValue = 0;

	public TetrisAI()  {
		if(recordedData.isEmpty()){
			readFile("table.txt");
		}
	}

	


	public void executeMove(State current, int newScoreValue, State right, State up, State left, State down) {
		String move = makeMove(current, newScoreValue, right, up, left, down);
		System.out.println(move);
		Robot AIControl = null;
		try {
			AIControl = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (move.equals("Up")) {
			AIControl.keyPress(38);
			System.out.println("AI: UP");
		} else if (move.equals("Down")) {
			AIControl.keyPress(40);
			System.out.println("AI: DOWN");
		} else if (move.equals("Right")) {
			AIControl.keyPress(39);
			System.out.println("AI: RIGHT");
		} else if (move.equals("Left")) {
			AIControl.keyPress(37);
			System.out.println("AI: LEFT");
		}
	}

	// I have not written a state extraction method yet, but what will happen is
	// All of these other possible moves will be the game making that state and
	// then
	// The state extraction method taking it and saving, then taking back the
	// changes
	public String makeMove(State current, int newScoreValue, State right, State up, State left,
			State down) {
		int reward = newScoreValue - lastScoreValue;
		lastScoreValue = newScoreValue;
		String move = "Up";
		TableEntry tableEntry = null;
		TableEntry rightTable = null;
		TableEntry leftTable = null;
		TableEntry upTable = null;
		TableEntry downTable = null;
		
		if(hasState(right)){
			for(int i = 0; i < recordedData.size(); i++){
				if(recordedData.get(i).checkState(right)){
					rightTable = recordedData.get(i);
				}
			}
		}
		if(hasState(left)){
			for(int i = 0; i < recordedData.size(); i++){
				if(recordedData.get(i).checkState(left)){
					leftTable = recordedData.get(i);
				}
			}
		}
		if(hasState(up)){
			for(int i = 0; i < recordedData.size(); i++){
				if(recordedData.get(i).checkState(up)){
					upTable = recordedData.get(i);
				}
			}
		}
		if(hasState(down)){
			for(int i = 0; i < recordedData.size(); i++){
				if(recordedData.get(i).checkState(down)){
					downTable = recordedData.get(i);
				}
			}
		}
		if (hasState(current)) {
			for (int i = 0; i < recordedData.size(); i++) {

				if (recordedData.get(i).checkState(current)) {
					tableEntry = recordedData.get(i);

					i = recordedData.size();
					Random RNG = new Random();

					if (RNG.nextDouble() > (1 - exploration)) {
						move = makeRandomMove();
					}

					else {
						move = tableEntry.getBestMove(rightTable,upTable,leftTable,downTable);
					}
				}
			}
		}
		//If it is a new state
		else{
			stateTable.add(current);
			move = makeRandomMove();
			tableEntry = new TableEntry(current);
			recordedData.add(tableEntry);
		}
		State nextState = null;
		if(move.equals("Up")){
			nextState = up;
		}
		else if(move.equals("Down")){
			nextState = down;
		}
		else if(move.equals("Right")){
			nextState = right;
		}
		else{
			nextState = left;
		}
		double nextTurnQValue = 0;
		if(hasState(nextState)){
			for(int i = 0; i < recordedData.size(); i++){
				if(recordedData.get(i).checkState(nextState)){
					nextTurnQValue = recordedData.get(i).actions.getMaxValue();
				}
			}
		}
		else{
			TableEntry newState = new TableEntry(nextState);
			recordedData.add(newState);
			nextTurnQValue = 0;
		}
		if(move == null){
			move = "Up";
		}
		
		System.out.println(recordedData.size());
		tableEntry.updateValue(move, reward, nextTurnQValue);
		return move;
		
	}

	private boolean hasState(State current) {
		for (int i = 0; i < recordedData.size(); i++) {
			State old = recordedData.get(i).state;
			if (current.equals(old)) {
				return true;
			}
		}
		return false;
	}

	private String makeRandomMove() {
		Random RNG = new Random();
		double randDecimal = RNG.nextDouble();
		if (randDecimal < 0.25) {

			return "Up";
		} else if (randDecimal >= 0.25 && randDecimal < 0.5) {
			return "Down";
		} else if (randDecimal >= 0.5 && randDecimal < 0.75) {
			return "Left";
		} else {
			return "Right";
		}
	}

	public void exportTable() {
		String textFile = "";
		for (int i = 0; i < recordedData.size(); i++) {
			textFile += getStateValue(i);
			textFile += getActionValue(i);
		}

		FileWriter writer = null;

		try {
			writer = new FileWriter("table.txt");
			writer.write(textFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
		System.out.println("Works");
	}

	private String getActionValue(int i) {
		String actionValue = "Action:";
		actionValue += System.getProperty("line.separator");
		ActionTable thisTable = recordedData.get(i).actions;
		double[] qValues = thisTable.qValues;
		actionValue += "Up: " + Double.toString(qValues[0]) + System.getProperty("line.separator");
		actionValue += "Left: " + Double.toString(qValues[1]) + System.getProperty("line.separator");
		actionValue += "Right: " + Double.toString(qValues[2]) + System.getProperty("line.separator");
		actionValue += "Down: " + Double.toString(qValues[3]) + System.getProperty("line.separator");
		return actionValue;
	}

	private String getStateValue(int i) {
		String stateValue = "State:";
		stateValue += System.getProperty("line.separator");
		State thisState = recordedData.get(i).state;
		int[][] grid = thisState.grid;
		for (int j = 0; j < grid.length; j++) {
			for (int k = 0; k < grid[j].length; k++) {
				stateValue += Integer.toString(grid[j][k]);
			}
			stateValue += System.getProperty("line.separator");
			
		}

		return stateValue;
	}
	
	//reads in state info, action table, table entry objects from text file	
	public void readFile(String fileName) {
		State readState = null;
		TableEntry entry = null;
		String line = "";
		Scanner scanner = null;
		int [] tempRow = new int[Tetris.COLUMNS];
		int[][] tempGrid = new int[Tetris.ROWS][Tetris.COLUMNS];
		int i = 0;
		int j = 0;
		
		//read file by line
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    while ((line = br.readLine()) != null) {
					scanner = new Scanner(line);
					i = 0;
					//READ IN STATE grid
				if(line.contains("State:")){
					for(j = 0; j < Tetris.ROWS; j++){
						
						line = br.readLine();	//read next line, first line of grid
						System.out.println(line);
						line.trim();
						
						for(int k = 0; k < line.length() && i < 10; k++){
							String ch = line.substring(k, k+1);
							if(ch.contains("-")){
								
							}
							tempRow[i] = Integer.parseInt(ch);	//read in column values
						     i++;
							
						}

						tempGrid[j] = tempRow;	//add to new grid 
						System.out.println(Arrays.toString(tempRow));
					
					}
					//state grid is read, create state/table entry object
					readState = new State(tempGrid);
					entry = new TableEntry(readState);
				}	
				
				//read in actiontable
				else if(line.contains("Action:")){
					line = br.readLine();	//read in first line, UP
					System.out.println("");
					entry.actions.setValue("Up", Integer.parseInt(line.replaceAll("[\\D]", "")));
					line = br.readLine();	//read in first line, LEFT
					entry.actions.setValue("Left", Integer.parseInt(line.replaceAll("[\\D]", "")));
					line = br.readLine();	//read in first line, RIGHT
					entry.actions.setValue("Right", Integer.parseInt(line.replaceAll("[\\D]", "")));
					line = br.readLine();	//read in first line, DOWN
					entry.actions.setValue("Down", Integer.parseInt(line.replaceAll("[\\D]", "")));

				
				}
				//add this read table entry, action table pair into recorded data 
				recordedData.add(entry);
				
				}
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		
	

	public void cleanData() {
		for (int i = 0; i < recordedData.size(); i++) {
			while (i < recordedData.size() && recordedData.get(i).allZeros()) {
				recordedData.remove(i);
			}
		}

	}

}
