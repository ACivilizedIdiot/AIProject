import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

//Class in which all the data from the game passes
public class TetrisAI {

	HashSet stateTable = new HashSet();

	private final double exploration = .1;

	// Will need to sort this data occasionally so that the odds of this taking
	// a long time
	// will be small
	ArrayList<TableEntry> recordedData = new ArrayList<TableEntry>();

	private int lastScoreValue = 0;

	public TetrisAI() {

	}
	
	public void executeMove(State current, int newScoreValue, State right, State up, State left,
			State down){
		String move = makeMove(current, newScoreValue, right, up, left,down);
		Robot AIControl = null;
		try {
			AIControl = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(move.equals("Up")){
			AIControl.keyPress(38);
		}
		else if(move.equals("Down")){
			AIControl.keyPress(40);
		}
		else if(move.equals("Right")){
			AIControl.keyPress(38);
		}
		else if(move.equals("Left")){
			AIControl.keyPress(37);
		}
	}

	//I have not written a state extraction method yet, but what will happen is
	//All of these other possible moves will be the game making that state and then
	//The state extraction method taking it and saving, then taking back the changes
	public String makeMove(State current, int newScoreValue, State right, State up, State left,
			State down) {
		int reward = newScoreValue - lastScoreValue;
		lastScoreValue = newScoreValue;
		String move = "";
		TableEntry tableEntry = null;
		if (stateTable.contains(current)) {

			for (int i = 0; i < recordedData.size(); i++) {

				if (recordedData.get(i).checkState(current)) {
					tableEntry = recordedData.get(i);

					i = recordedData.size();
					Random RNG = new Random();

					if (RNG.nextDouble() > (1 - exploration)) {
						move = makeRandomMove();
					}

					else {
						move = tableEntry.getBestMove();
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
		else if(move.equals("Left")){
			nextState = left;
		}
		double nextTurnQValue = 0;
		if(stateTable.contains(nextState)){
			for(int i = 0; i < recordedData.size(); i++){
				if(recordedData.get(i).checkState(nextState)){
					nextTurnQValue = recordedData.get(i).actions.getMaxValue();
				}
			}
		}
		else{
			TableEntry newState = new TableEntry(nextState);
			nextTurnQValue = 0;
		}
		
		
		tableEntry.updateValue(move, reward, nextTurnQValue);
		return move;
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

}
