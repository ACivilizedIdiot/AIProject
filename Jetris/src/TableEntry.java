//This is the class used to store both the state and the actions in a simple format
//This class has a specific state and a specific action table each time a new one
//Is added to recorded data
//The position of the blocks falling is also this
public class TableEntry {
	
	protected State state = null;
	protected ActionTable actions = null;
	protected int timesEncountered = 0;
	
	public TableEntry(State newState){
		state = newState.clone();
		actions = new ActionTable();
	}
	
	public boolean checkState(State current){
		if(state.equals(current)){
			return true;
		}
		return false;
	}
	
	public void updateValue(String action, double value, double nextTurnQValue){
		actions.updateValue(action, value,nextTurnQValue);
		timesEncountered = 0;
	}

	public String getBestMove() {
		return actions.getBestMove();
		
	}

}
