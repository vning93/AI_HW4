import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class ClassifyThis extends Classifier {
	
	public String optionA;
	public String optionB;
	public Map<String, ArrayList<String>> namesMap;
	public Map<Integer, String> featureOrderMap;
	public int featureOrderCount;
	public int totalWeights;
	public double[] weights;
	public ArrayList<Entry> entryList;
	public double rate;
	public int iterations;
	public ArrayList<Entry> entryTestList;

	public ClassifyThis(String namesFilepath) throws IOException {
		super(namesFilepath);
		
		/* TWEEK THESE VARS */
		rate = 0.01;
		iterations = 5000;
		/* END TWEEKED VARS */
		
		optionA = "";
		optionB = "";
		namesMap = new LinkedHashMap<String, ArrayList<String>>();
		totalWeights = 0;
		featureOrderMap = new LinkedHashMap<Integer, String>();
		entryList = new ArrayList<Entry>();
		entryTestList = new ArrayList<Entry>();
		
		FileReader fileReader = new FileReader(new File(namesFilepath));
		BufferedReader br = new BufferedReader(fileReader);
		
		String line = null;
		int lineCount = 1;
		
		while ((line = br.readLine()) != null) {
			line = line.replaceAll("\\s+", " ");
			String[] elements = line.split(" ");
			if (lineCount == 1) {
				optionA = elements[0];
				optionB = elements[1];
			}
			else if (lineCount > 2) {
				for (int i = 1; i < elements.length; i++) {
					if (namesMap.containsKey(elements[0])) {
						namesMap.get(elements[0]).add(elements[i]);						
					}
					else {
						ArrayList<String> newList = new ArrayList<String>();
						newList.add(elements[i]);
						namesMap.put(elements[0], newList);
						featureOrderMap.put(featureOrderCount, elements[0]);
						featureOrderCount++;
					}
					totalWeights++;
				}
			}
			lineCount++;
		}
		
		br.close();
	}

	public void train(String trainingDataFilepath) {
		weights = new double[totalWeights];
		this.createTrainingList(trainingDataFilepath);
		
		//then, do the rest of the math here
		for (int n = 0; n < iterations; n++) {
			for (int i = 0; i < entryList.size(); i++) {
				int[] x = entryList.get(i).getX();
				double predicted = this.classify(x);
				int y = entryList.get(i).getY();
				
				for (int j = 0; j < weights.length; j++) {
					weights[j] = weights[j] + rate*(y - predicted)*x[j];
				}
			}
			System.out.println("Iteration: " + n + " - Weights: " + Arrays.toString(weights));
		}
	}

	public void makePredictions(String testDataFilepath) {
		System.out.println();
		System.out.println("Results...");
		
		//Convert to entryTestList numerically, and then apply weights to produce output answer.
		this.createTestList(testDataFilepath);
		
		int iterCounter = 1;
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		for (int i = 0; i < entryTestList.size(); i++) {
			double outputProb = this.classify(entryTestList.get(i).getX());
			if (outputProb >= 0.5) {
				resultList.add(1);
//				System.out.println("Entry " + iterCounter + ": " + optionA);
			}
			else {
				resultList.add(0);
//				System.out.println("Entry " + iterCounter + ": " + optionB);
			}
			iterCounter++;
		}
		
		//Calculate scores
		int numCorrect = 0;
		int numWrong = 0;
		for (int i = 0; i < entryList.size(); i++) {
			if (entryList.get(i).getY() == resultList.get(i)) {
				numCorrect++;
			}
			else {
				numWrong++;
			}
		}
		System.out.println("Correct: " + numCorrect);
		System.out.println("Wrong: " + numWrong);
		System.out.println("% Correct: " + (double)numCorrect / (double)iterCounter);
	}
	
	/* TRAINING HELPER METHODS */
	
	private void createTrainingList(String trainingDataFilepath) {
		FileReader fileReader;
		try {
			fileReader = new FileReader(new File(trainingDataFilepath));
			BufferedReader br = new BufferedReader(fileReader);
			
			String line = null;
			
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\s+", " ");
				String[] elements = line.split(" ");
				
//				if numeric, then it's a number and nothing needs to be done
//				if qualitative, then create 00001000 or whatever thetas/weights for the next .length-many weights in data[]
//				then, entrySet.add(entry)
				int[] data = new int[totalWeights];
				int weightCounter = 0;
				
				for (int i = 0; i < elements.length-1; i++) {
					String feature = featureOrderMap.get(i);
					if (namesMap.get(feature).get(0).equals("numeric")) {
						data[weightCounter] = Integer.parseInt(elements[i]);
						weightCounter++;
					}
					else {
						ArrayList<String> tmpList = namesMap.get(feature);
						for (String str : tmpList) {
							if (elements[i].equals(str)) {
								data[weightCounter] = 1;
							}
							else {
								data[weightCounter] = 0;
							}
							weightCounter++;
						}
					}
				}
				
				String output = elements[elements.length-1];
				int outputInt = 0;
				if (output.equals(optionA)) {
					outputInt = 1;
				}
				else {
					outputInt = 0;
				}
				
//				System.out.println(data.length);
//				System.out.println(outputInt);
				Entry newEntry = new Entry(outputInt, data);
				entryList.add(newEntry);	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private double classify(int[] x) {
		double logit = .0;
        for (int i = 0; i < weights.length; i++)  {
            logit += weights[i] * x[i];
        }
        return sigmoid(logit);
	}
	
	private double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }
	
	/* END TRAINING HELPER METHODS */
	
	/* TESTING HELPER METHODS */
	
	private void createTestList(String testDataFilepath) {
		FileReader fileReader;
		try {
			fileReader = new FileReader(new File(testDataFilepath));
			BufferedReader br = new BufferedReader(fileReader);
			
			String line = null;
			
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\s+", " ");
				String[] elements = line.split(" ");
				
//				if numeric, then it's a number and nothing needs to be done
//				if qualitative, then create 00001000 or whatever thetas/weights for the next .length-many weights in data[]
//				then, entrySet.add(entry)
				int[] data = new int[totalWeights];
				int weightCounter = 0;
				
				for (int i = 0; i < elements.length-1; i++) {
					String feature = featureOrderMap.get(i);
					if (namesMap.get(feature).get(0).equals("numeric")) {
						data[weightCounter] = Integer.parseInt(elements[i]);
						weightCounter++;
					}
					else {
						ArrayList<String> tmpList = namesMap.get(feature);
						for (String str : tmpList) {
							if (elements[i].equals(str)) {
								data[weightCounter] = 1;
							}
							else {
								data[weightCounter] = 0;
							}
							weightCounter++;
						}
					}
				}
				
				String output = elements[elements.length-1];
				int outputInt = 0;
				if (output.equals(optionA)) {
					outputInt = 1;
				}
				else {
					outputInt = 0;
				}
				
//				System.out.println(data.length);
//				System.out.println(outputInt);
				Entry newEntry = new Entry(outputInt, data);
				entryTestList.add(newEntry);	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* END TESTING HELPER METHODS */
	
}
