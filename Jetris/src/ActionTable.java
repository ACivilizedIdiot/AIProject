//The table for each action of a state
//This is also where I put the decision making
public class ActionTable {
	
	
	double[] qValues = new double[4];
	
	private final double learningRate = .4;
	private final double diminishingRate = .8;
	
	public ActionTable(){
		
	}
	
	public void updateValue(String action, double value, double nextTurnQValue){
		if(action.equals("Up")){
			qValues[0] = markovUpdate(value, nextTurnQValue, 0); 
		}
		else if(action.equals("Left")){
			qValues[1] = markovUpdate(value, nextTurnQValue, 1);
		}
		else if(action.equals("Right")){
			qValues[2] = markovUpdate(value, nextTurnQValue, 2);
		}
		else if(action.equals("Down")){
			qValues[3] = markovUpdate(value, nextTurnQValue, 3);
		}
	}

	private double markovUpdate(double value, double nextTurnQValue, int index) {
		return qValues[index] + learningRate * (value + (diminishingRate * nextTurnQValue) - qValues[index]);
	}
	
	public double getMaxValue(){
		double maxValue = 0;
		for(int i = 0; i < qValues.length; i++){
			if(qValues[i] > maxValue){
				maxValue = qValues[i];
			}
		}
		
		return maxValue;
	}

	public String getBestMove() {
		double maxValue = getMaxValue();
		int moveIndex = 0;
		for(int i = 0; i < qValues.length; i++){
			if(maxValue == qValues[i]){
				moveIndex = i;
				i = qValues.length;
			}
		}
		if(moveIndex == 0){
			return "Up";
		}
		else if(moveIndex == 1){
			return "Left";
		}
		else if(moveIndex == 2){
			return "Right";
		}
		else {
			return "Down";
		}
	}
}
