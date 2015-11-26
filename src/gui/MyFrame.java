package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import compiler.CodeGenerator;
import compiler.Parser;
import compiler.Scanner;
import compiler.Token;
import compiler.TokenNode;


public final class MyFrame extends JFrame {
	private static final long serialVersionUID = -5382604565989249423L;

	private final static String NAME = "PL/0 Gen.";
	
	private JTextArea input;
	private JTextArea output;
	private JButton button;
	
	public MyFrame() {
		super(NAME);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		createPanels();
		
		this.setVisible(true);
	}

	private void createPanels() {
		this.setLayout(new BorderLayout());	
		JPanel top = new JPanel(new GridLayout(1, 2));
		
		top.setBackground(Color.WHITE);
		JPanel pom = new JPanel(new BorderLayout());
		pom.setBackground(Color.WHITE);
		JTextField helper = new JTextField("  Vlozte program  ");
		helper.setBackground(Color.WHITE);
		helper.setFocusable(false);
		helper.setEditable(false);
		pom.add(helper, BorderLayout.NORTH);
		input = new JTextArea();
		input.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		JScrollPane scroller = new JScrollPane(input);
		pom.add(scroller, BorderLayout.CENTER);
		top.add(pom);
		
		pom = new JPanel(new BorderLayout());
		pom.setBackground(Color.WHITE);
		helper = new JTextField("  Instrukce:  ");
		helper.setBackground(Color.WHITE);
		helper.setFocusable(false);
		helper.setEditable(false);
		pom.add(helper, BorderLayout.NORTH);
		output = new JTextArea();
		output.setEditable(false);
		output.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		scroller = new JScrollPane(output);
		pom.add(scroller, BorderLayout.CENTER);
		top.add(pom);
		this.add(top, BorderLayout.CENTER);
		
		
		JPanel bottom = new JPanel(new FlowLayout());
		bottom.setBackground(Color.WHITE);
		button = new JButton("Compile");
		bottom.add(button);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				compile();
			}
		});
		this.add(bottom, BorderLayout.SOUTH);
	}
	
	private void compile() {
		String programText = input.getText();
		try {
			List<Token> tokens = new Scanner().analyse(programText);
			TokenNode node = new Parser().parse(tokens);
			List<String> instructions = new CodeGenerator().generate(node);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < instructions.size(); i++) {
				builder.append(i + " " + instructions.get(i) + "\n");
			}
			output.setForeground(Color.BLUE);
			output.setText(builder.toString());
		} catch (Exception e) {
			output.setForeground(Color.RED);
			output.setText(e.getMessage());
		}
	}
}
