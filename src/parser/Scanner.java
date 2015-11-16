package parser;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
	private static final String KEY_WORDS_STANDALONE[] = new String[]{"call", "return", "begin", "end", "if", "else", "then", "while", "do", 
		"switch", "case", "procedure", "const", "var", "(", ")", "!", ".", ",", ";", "=", "==", "<>", "<", ">", "<=", ">=", "AND", "OR", 
		"+", "-", "*", "/", "?", ":"};
	private static final String KEY_WORDS_MIDDLE[] = new String[]{"(", ")", "!", ".", ",", ";", "==", "=", "<>", "<", ">", "<=", ">=", 
		"+", "-", "*", "/", "?", ":"};

	private String word;
	private int pivot;

	public List<Token> analyse(String text) {
		text = text.trim();
		List<Token> tokens = new ArrayList<>();
		if (text.equals("")) return tokens;
		
		String[] words = text.split("\\s+");
		pivot = 0;
		word = null;
		
		while (pivot < words.length) {
			String nextStringToken = getNext(words);
			tokens.add(Token.createToken(nextStringToken));
		}

		return tokens;
	}

	private String getNext(String[] words) {
		String input;
		if (word == null) {
			word = words[pivot];
		}

		if (equals(KEY_WORDS_STANDALONE, word)) {
			input = word;
			word = null;
			pivot++;
		} else {
			String splitter = contains(KEY_WORDS_MIDDLE, word);
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

	private boolean equals(String array[], String word) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(word)) return true;
		}
		return false;
	}

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
