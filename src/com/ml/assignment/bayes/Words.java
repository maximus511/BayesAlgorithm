package com.ml.assignment.bayes;

/**
 * Class to store the properties of each word.
 * @author Rahul
 *
 */
public class Words {
	
	private Integer count;
	private String word;
	private Integer label;
	
	public Words(Integer count, String word, Integer label)
	{
		this.count = count;
		this.word = word;
		this.label = label;
	}
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getLabel() {
		return label;
	}

	public void setLabel(Integer label) {
		this.label = label;
	}
	

}
