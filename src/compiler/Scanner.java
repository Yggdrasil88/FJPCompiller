package compiler;

import java.util.ArrayList;
import java.util.List;
/**
 * Scanner class for lexical analyzes
 */
public class Scanner {
	/**
	 * Array with keywords
	 */
	private static final String KEYWORDS[] = new String[]{"call", "return", "begin", "end", "if", "else", "then", "while", "do", 
		"switch", "case", "procedure", "const", "var", "(", ")", "!", ".", ",", ";", "=", "==", "<>", "<", ">", "<=", ">=", "and", "or", 
		"+", "-", "*", "/", "?", ":"};
	/**
	 * Array with keywords
	 */
	private static final String KEYWORDS_MIDDLE[] = new String[]{"(", ")", "!", ".", ",", ";", "==", "=", "<>", "<", ">", "<=", ">=", 
		"+", "-", "*", "/", "?", ":"};
	/**
	 * Current word to check
	 */
	private String word;
	/**
	 * Pivot, indicates current position in the array
	 */
	private int pivot;
	/**
	 * Analyzes given text, creates list of tokens
	 * @param text text to analyze
	 * @return list of tokens
	 * @throws Exception exception - Program not found
	 */
	public List<Token> analyse(String text) throws Exception {
		text = text.trim();
		if (text.equals("")) ErrorHandler.progNotFound();
		
		List<Token> tokens = new ArrayList<>();
		String[] words = text.split("\\s+");
		pivot = 0;
		word = null;

		while (pivot < words.length) {
			String nextStringToken = getNext(words);
			tokens.add(Token.createToken(nextStringToken, KEYWORDS));
		}

		return tokens;
	}
	/**
	 * Gets next word
	 * @param words array of words
	 * @return next word
	 */
	private String getNext(String[] words) {
		String input = null;
		if (word == null) {
			word = words[pivot];
		}

		if (equals(KEYWORDS, word)) {
			input = word;
			word = null;
			pivot++;
		} else {
			String splitter = contains(KEYWORDS_MIDDLE, word);
			if (splitter != null) {
				int index = word.indexOf(splitter);
				if (index == 0) {
					input = splitter;
					word = word.substring(splitter.length(), word.length());
				} else {
					input = word.substring(0, index);
					word = word.substring(index);
				}
			} else {
				input = word;
				word = null;
				pivot++;
			}	
		}
		return input;
	}
	/**
	 * Determines if a word is in the given array
	 * @param array array of keywords
	 * @param word word to check
	 * @return true if word is in array
	 */
	private boolean equals(String array[], String word) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(word)) return true;
		}
		return false;
	}
	/**
	 * Checks if a word is in an array (also checks substring)
	 * @param array array of keywords
	 * @param word word to check
	 * @return word that contains given word or substring
	 */
	private String contains(String array[], String word) {
		for (int i = 0; i < word.length(); i++) {
			for (int j = 0; j < array.length; j++) {
				if (i + array[j].length() <= word.length()) {
					if (word.substring(i, i + array[j].length()).equals(array[j])) return array[j];
				}
			}
		}
		return null;
	}
}
