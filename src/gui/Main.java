package gui;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Main class for the app, creates UI.
 */
public class Main {
	/**
	 * Main method
	 * @param args arguments
	 */
	public static void main(String[] args) {
		
		if(args.length == 1){
			try{
				String filename = (String)args[0];
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				MyFrame frame = new MyFrame();
				String program = "";
				String line;
				while((line = reader.readLine()) != null){
					program += line + "\n";
				}
				reader.close();
				frame.setInputText(program);
			} catch(Exception e){
				System.err.println("Nepodarilo se otevrit a precist soubor");
				return;
			} 
			
		} else {
			new MyFrame();
		}
		
	}

}
