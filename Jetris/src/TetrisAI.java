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

	public String makeMove(State current, int newScoreValue, State rightMove, State upMove, State leftMove,
			State downMove) {
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
			nextState = upMove;
		}
		else if(move.equals("Down")){
			nextState = downMove;
		}
		else if(move.equals("Right")){
			nextState = rightMove;
		}
		else if(move.equals("Left")){
			nextState = leftMove;
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
