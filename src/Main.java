import java.io.IOException;
import java.util.*;

public class Main {
	
	public static void main(String[] args) throws IOException {
		//Create the output variables, and a map of the census names.
//		ClassifyThis newClassifier = new ClassifyThis("census.names");
		SimpleRandomClassifier newClassifier = new SimpleRandomClassifier("census.names");
		
		//Learns the input data, and creates Logistic function to calculate weights.
		newClassifier.train("census.train");
		
		//Uses weights from training step to calculate output.
		newClassifier.makePredictions("census.test");
	}

}
