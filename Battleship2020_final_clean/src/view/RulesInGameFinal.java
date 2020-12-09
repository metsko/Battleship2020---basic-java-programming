package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

import controller.Controller;

public class RulesInGameFinal extends JFrame {

	private static final long serialVersionUID = 1L;
	// Swing GUI components
	TextArea textA = new TextArea();
	JPanel panel;

	// Input
	Controller controller;
	Dimension dim = new Dimension(1200, 1200);

	// Constructor
	public RulesInGameFinal(Controller controller) {
		this.controller = controller;

		setTitle("Battleship2020 - Rules");
		this.setPreferredSize(dim);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = (JPanel) this.getContentPane();

		// BackButton to previous startScreen
		JButton backButton = new JButton("Back");
		backButton.setBackground(Color.red);
		backButton.addActionListener(new ActionListener() {
			// show highScores screen
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == backButton) {
					controller.getStartScreen().setVisible(true);
					;
					dispose();
				}
			}
		});

		// Add components
		panel.add(textA, BorderLayout.CENTER);
		panel.add(backButton, BorderLayout.AFTER_LAST_LINE);

		// Show
		pack();
		setVisible(true);
	}

	// method to read rules from rules.txt
	public static String readRules() throws IOException {
		BufferedReader br;
		String line;
		String everything = null;
		final String newline = "\n";
		try {
			br = new BufferedReader(new FileReader("rules.txt"));
			StringBuilder sb = new StringBuilder();

			line = br.readLine();
			while (line != null) {
				sb.append(line + newline);
				line = br.readLine();
			}
			everything = sb.toString();
		} catch (FileNotFoundException e) {
			System.out.print("'rules.txt' not found.");
		}
		return everything;
	}

	// Inner class for JPanel holding textArea
	static class TextArea extends JPanel {
		private static final long serialVersionUID = 1L;
		JTextArea textA = new JTextArea(500, 500);
		JScrollPane areaScrollPane = new JScrollPane(textA);

		public TextArea() {
			try {
				textA.append(readRules());
				textA.setFont(textA.getFont().deriveFont(20f)); // change size to 12pt
			} catch (IOException e) {
				ErrorMessage.message("Issue while reading rules.txt.");
			}
			textA.setEditable(false);

			setLayout(new BorderLayout());
			add(areaScrollPane, BorderLayout.CENTER);
		}
	}
}
