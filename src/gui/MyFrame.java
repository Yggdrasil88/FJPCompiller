package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import compiler.PL0_Compiler;

/**
 *	UI for the app
 */
public final class MyFrame extends JFrame {
	private static final long serialVersionUID = -5382604565989249423L;
	/**
	 * Name of the window
	 */
	private final static String NAME = "PL/0 Compiler";
	/**
	 * Area for input
	 */
	private JTextArea input;
	/**
	 * Area for output
	 */
	private JTextArea output;
	/**
	 * Confirm button
	 */
	private JButton button;
	/**
	 * Constructor
	 */
	public MyFrame() {
		super(NAME);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		createPanels();
		
		this.setVisible(true);
	}
	/**
	 * Method for creating UI layout
	 */
	private void createPanels() {
		this.setLayout(new BorderLayout());	
		JPanel top = new JPanel(new GridLayout(1, 2));
		
		top.setBackground(Color.WHITE);
		JPanel hlp = new JPanel(new BorderLayout());
		hlp.setBackground(Color.WHITE);
		JTextField helper = new JTextField("  Insert program:  ");
		helper.setBackground(Color.WHITE);
		helper.setFocusable(false);
		helper.setEditable(false);
		hlp.add(helper, BorderLayout.NORTH);
		input = new JTextArea();
		input.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		JScrollPane scroller = new JScrollPane(input);
		hlp.add(scroller, BorderLayout.CENTER);
		top.add(hlp);
		
		hlp = new JPanel(new BorderLayout());
		hlp.setBackground(Color.WHITE);
		helper = new JTextField("  Instructions:  ");
		helper.setBackground(Color.WHITE);
		helper.setFocusable(false);
		helper.setEditable(false);
		hlp.add(helper, BorderLayout.NORTH);
		output = new JTextArea();
		output.setEditable(false);
		output.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		scroller = new JScrollPane(output);
		hlp.add(scroller, BorderLayout.CENTER);
		top.add(hlp);
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
	/**
	 * Method for getting PL/0 code
	 */
	private void compile() {
		String programText = input.getText();
		try {
			output.setForeground(Color.BLUE);
			output.setText(PL0_Compiler.compile(programText));
		}
		catch (Exception e) {
			output.setForeground(Color.RED);
			output.setText(e.getMessage());
		}
	}
}
