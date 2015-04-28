import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SimpleRandomClassifier extends Classifier {
	
	public String optionA;
	public String optionB;
	public ArrayList<Integer> randomTestList;

	public SimpleRandomClassifier(String namesFilepath) throws IOException {
		super(namesFilepath);
		
		optionA = "";
		optionB = "";
		
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
			lineCount++;
		}
		
		br.close();
	}

	public void train(String trainingDataFilepath) {
		//Do Nothing
	}

	public void makePredictions(String testDataFilepath) {
		System.out.println("Results...");
		
		int lineCount;
		try {
			lineCount = this.getNumLines(testDataFilepath);
			
			//Calculate scores
			int numCorrect = 0;
			int numWrong = 0;
			
			for (int i = 0; i < lineCount; i++) {
				double randNum = Math.random();
				if (randNum >= 0.5) {
					numCorrect++;
				}
				else {
					numWrong++;
				}
			}
			
			System.out.println("Correct: " + numCorrect);
			System.out.println("Wrong: " + numWrong);
			System.out.println("% Correct: " + (double)numCorrect / (double)lineCount);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/* HELPER METHODS */
	private int getNumLines(String testDataFilepath) throws IOException {
		FileReader fileReader;
		fileReader = new FileReader(new File(testDataFilepath));
		
		BufferedReader br = new BufferedReader(fileReader);
		
		String line = null;
		randomTestList = new ArrayList<Integer>();
		int lineCount = 1;
		
		while ((line = br.readLine()) != null) {
			lineCount++;
		}
			
		br.close();
		
		return lineCount;
	}
	
}