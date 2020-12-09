package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.Controller;
import data.HighscoreTableRow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HighScoreScreen {
	// UI settings
	Dimension frameDimension = new Dimension(1200, 500);

	// Swing GUI
	private final JFrame frame;
	JScrollPane scrollPane;
	JButton backButton;
	JTable table;

	// Data
	ArrayList<HighscoreTableRow> highscoreTableRows = new ArrayList<>();
	DefaultTableModel tableModel;

	// Constructor
	HighScoreScreen(Controller controller) {

		// frame
		frame = new JFrame();
		frame.setTitle("Battleship2020 - Highscores");
		frame.setPreferredSize(frameDimension);
		frame.setLayout(new BorderLayout());

		// backbutton => show previous startScreen
		backButton = new JButton("Back");
		backButton.setBackground(Color.red);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == backButton) {
					controller.getStartScreen().setVisible(true);
					frame.dispose();
				}
			}
		});

		// Table with records
		// Column names
		String[] columns = { "Rank", "Name", "Score", "Time" };
		try {
			readData();
		} catch (IOException e) {
			ErrorMessage.message("Could not read 'highscores.txt'. ");
		}
		// add rank to records
		addRank();
		// create table model
		tableModel = new DefaultTableModel(columns, 0);
		// create the table from the model
		table = new JTable(tableModel);
		table.setRowHeight(table.getRowHeight() + 20);
		table.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 20));
		table.setFont(new Font("Calibri", Font.BOLD, 20));
		// add the data
		addData();
		scrollPane = new JScrollPane(table);

		// add components to frame
		frame.add(backButton, BorderLayout.SOUTH);
		frame.add(scrollPane, BorderLayout.CENTER);

		// finish and visualize
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	// Some extra methods
	// Read the data from highscores.txt
	private void readData() throws IOException {
		String localPath = "highscores.txt";
		File file = new File(localPath);
		String line;
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			while ((line = in.readLine()) != null) {
				String[] splittedLine = line.split(",");
				highscoreTableRows.add(new HighscoreTableRow(splittedLine[0], splittedLine[1], splittedLine[2]));
			} // end while
		} // end try
	}

	// add rank and sort descending
	private void addRank() {
		Collections.sort(highscoreTableRows, new Comparator<>() {
			@Override
			public int compare(HighscoreTableRow o1, HighscoreTableRow o2) {
				return Double.compare(Double.parseDouble(o2.getScore()), Double.parseDouble(o1.getScore())); // descending
																												// order
			}
		});
		int i = 1;
		for (HighscoreTableRow r : highscoreTableRows) {
			r.setRank(Integer.toString(i));
			i++;
		}
	}

	// data to the table
	private void addData() {
		for (HighscoreTableRow r : highscoreTableRows) {
			String[] stringToAdd = { r.getRank(), r.getName(), r.getScore(), r.getDateTime() };
			tableModel.addRow(stringToAdd);
		}
	}
}
