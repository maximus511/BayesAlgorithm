package com.ml.assignment.bayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * This class implements the Naive Bayes algorithm for Text classification.
 * It also acts as the main class for the assignment.
 * 
 * @author Rahul
 *
 */
public class NaiveBayesClassification {


	TreeMap<Integer, ArrayList<Words>> trainDataMap = new TreeMap<Integer, ArrayList<Words>>();
	TreeMap<Integer, ArrayList<Words>> testDataMap = new TreeMap<Integer, ArrayList<Words>>();
	ArrayList<String> wordIndicesList = new ArrayList<String>();
	ArrayList<Integer> labelList = new ArrayList<Integer>();
	ArrayList<Double> condProbsForZeroList = new ArrayList<Double>();
	ArrayList<Double> condProbsForOneList = new ArrayList<Double>();
	ArrayList<Integer> resultLabelsForTest = new ArrayList<Integer>();
	Double[] probabilityValuesForLabel = new Double[2];
	Integer totalWordCountForZeroLabel =0;
	Integer totalWordCountForOneLabel =0;
	
	/**
	 * Function to read the Training data csv file and store it in a treemap.
	 * @param csvFile
	 */
	public void readTrainingDataset(String csvFile)
	{
		BufferedReader bReader = null;
		try {
			File file = new File(csvFile);
			bReader = new BufferedReader(new FileReader(file));
			String currentLine = null;
			int i=0;
			ArrayList<Words> wordList;
			//Read through each lone of the csv
			while((currentLine=bReader.readLine())!=null)
			{
				String[] tokens = currentLine.split(",");
				int j = 0;
				wordList = new ArrayList<Words>();
				//Get the count for the words from their respective columns
				while ( j < tokens.length )
				{
					Integer wordCount = Integer.valueOf(tokens[j]);
					wordList.add(new Words(wordCount, wordIndicesList.get(j), labelList.get(i)));
					//Check which class this record corresponds to and  add the word count to the respective variable
					if(labelList.get(i)==0)
					{
						totalWordCountForZeroLabel +=wordCount;
					}
					else if(labelList.get(i)==1)
					{
						totalWordCountForOneLabel += wordCount;
					}
					j++;
				}
				trainDataMap.put(i,wordList);
				i++;
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(bReader != null)
				{
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Function to read the Word indices file
	 * @param wordIndicesFile
	 */
	public void readWordIndices(String wordIndicesFile)
	{
		BufferedReader bReader = null;
		try {
			File file = new File(wordIndicesFile);
			bReader = new BufferedReader(new FileReader(file));
			String currentLine = null;
			while((currentLine=bReader.readLine())!=null)
			{
				wordIndicesList.add(currentLine.trim());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(bReader != null)
				{
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Function to read the class label for the training data set
	 * @param labelFile
	 */
	public void readLabelFile(String labelFile)
	{
		BufferedReader bReader = null;
		try {
			File file = new File(labelFile);
			bReader = new BufferedReader(new FileReader(file));
			String currentLine = null;
			int countForZero =0;
			int countForOne =0;
			while((currentLine=bReader.readLine())!=null)
			{
				int currentValue = Integer.valueOf(currentLine.trim());
				labelList.add(currentValue);
				if(currentValue == 0)
				{
					countForZero++;
				}
				else {
					countForOne++;
				}
			}
			//Probability of each class (Taking log for calculation with Test data)
			probabilityValuesForLabel[0] = Math.log10(((double)countForZero)/((double)labelList.size()));
			probabilityValuesForLabel[1] = Math.log10(((double)countForOne)/((double)labelList.size()));

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(bReader != null)
				{
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Calculate the conditional probability of each word
	 * It stores the log (base 10) value, as the same is required for calculation in Test data
	 */
	public void calculateConditionalProbability()
	{
		int zeroCount =0;
		int oneCount = 0;
		for(int i=0;i<wordIndicesList.size();i++)
		{
			zeroCount =0;
			oneCount = 0;
			//Retrieve the word counts for zero and one class labels
			for(ArrayList<Words> wordList: trainDataMap.values())
			{
				Words word = wordList.get(i);
				if(word.getLabel()==0)
				{
					zeroCount += word.getCount();
				}
				else {
					oneCount += word.getCount();
				}
			}
			//The index i corresponds to the respective word in the list in that order
			Double condProbZero = Math.log10(((double)(zeroCount+1))/((double)(totalWordCountForZeroLabel+wordIndicesList.size())));
			condProbsForZeroList.add(i,condProbZero);

			Double condProbOne = Math.log10(((double)(oneCount+1))/((double)(totalWordCountForOneLabel+wordIndicesList.size())));
			condProbsForOneList.add(i,condProbOne);
		}
		
		System.out.println(totalWordCountForZeroLabel +"   "+totalWordCountForOneLabel);
	}


	/**
	 * Function to read the Test data csv file and predict the result class label
	 * for each document.
	 * @param csvFile
	 */
	public void readTestDataset(String csvFile)
	{
		BufferedReader reader = null;
		try {
			File file = new File(csvFile);
			reader = new BufferedReader(new FileReader(file));
			String currentLine = null;
			int i=0;
			while((currentLine=reader.readLine())!=null)
			{
				String[] tokens = currentLine.split(",");
				int j = 0;
				Double probForLabelZero = probabilityValuesForLabel[0];
				Double probForLabelOne = probabilityValuesForLabel[1];
				//For each word in the document calculate the probability of being in
				//class 0 or class 1
				while ( j < tokens.length )
				{
					Integer wordCount = Integer.valueOf(tokens[j]);
					probForLabelOne +=(double)wordCount*condProbsForOneList.get(j);
					probForLabelZero +=(double)wordCount*condProbsForZeroList.get(j);
					j++;
				}
				if(probForLabelZero > probForLabelOne)
				{
					resultLabelsForTest.add(i,0);
				}
				else 
				{
					resultLabelsForTest.add(i,1);
				}
				i++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(reader != null)
				{
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Function to calculate the accuracy of the predicted class labels
	 * for the test data
	 * @param testLabelFile
	 * @return
	 */
	public Double calculateAccuracy(String testLabelFile)
	{
		BufferedReader bReader = null;
		Double accuracy =0.0;
		try {
			File file = new File(testLabelFile);
			bReader = new BufferedReader(new FileReader(file));
			String currentLine = null;
			int matching =0;
			int index=0;
			while((currentLine=bReader.readLine())!=null)
			{
				int currentValue = Integer.valueOf(currentLine.trim());
				if(currentValue == resultLabelsForTest.get(index))
				{
					matching++;
				}
				index++;
			}

			accuracy = (double)matching/(double)resultLabelsForTest.size();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(bReader != null)
				{
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return accuracy*100;
	}

	//Getter and Setter methods
	public TreeMap<Integer, ArrayList<Words>> getTrainDataMap() {
		return trainDataMap;
	}

	public void setTrainDataMap(TreeMap<Integer, ArrayList<Words>> trainDataMap) {
		this.trainDataMap = trainDataMap;
	}

	public ArrayList<String> getWordIndicesList() {
		return wordIndicesList;
	}

	public void setWordIndicesList(ArrayList<String> wordIndicesList) {
		this.wordIndicesList = wordIndicesList;
	}

	public ArrayList<Integer> getLabelList() {
		return labelList;
	}

	public void setLabelList(ArrayList<Integer> labelList) {
		this.labelList = labelList;
	}
	
	/**
	 * Main function
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length!=5)
		{
			System.out.println("Invalid number of arguments!!");
			return;
		}
		NaiveBayesClassification nbClassifier = new NaiveBayesClassification();

		nbClassifier.readLabelFile(args[0]);
		nbClassifier.readWordIndices(args[1]);
		nbClassifier.readTrainingDataset(args[2]);
		nbClassifier.calculateConditionalProbability();
		
		//Clearing memory as these data are no longer required
		nbClassifier.setTrainDataMap(null);
		nbClassifier.setWordIndicesList(null);
		nbClassifier.setLabelList(null);
		nbClassifier.readTestDataset(args[3]);
		System.out.println("Accuracy: "+nbClassifier.calculateAccuracy(args[4])+"%");
	}
}
