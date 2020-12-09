package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Controller;
import model.Game;

public class StartScreen extends JFrame {

	private static final long serialVersionUID = 1L;

	// input
	final Controller controller;
	public static Dimension dimension = new Dimension(350, 650);

	// output
	private final int[] boardDim = new int[2];
	private File file;
	private boolean scoringSystem;
	private String namePlayer1;
	private String namePlayer2;

	// GUI components
	StartScreen frame = this;
	private final JComboBox scoringSystemsList;
	private final JTextField tName1, tName2;
	private final JButton playGameButton, openButton;

	// Constructor
	public StartScreen(controller.Controller controller2) {
		super();
		this.controller = controller2;
		this.setLayout(new GridLayout(7, 1));

		// Button to 'open config file'
		openButton = new JButton("Select config file...", new ImageIcon("images/Open16.gif"));
		OpenFileActionListener openFileActionListener = new OpenFileActionListener();
		openButton.addActionListener(openFileActionListener);

		// 2 fields to insert names
		tName1 = new JTextField("Insert name Player1...");
		tName1.setFont(new Font("Calibri", Font.ITALIC, 12));
		tName1.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				JTextField source = (JTextField) e.getComponent();
				source.setText("");
				source.removeFocusListener(this);
			}
		});
		tName2 = new JTextField("Insert name Player2...");
		tName2.setFont(new Font("Calibri", Font.ITALIC, 12));
		tName2.addFocusListener(new FocusAdapter() { // make text disappear on click
			public void focusGained(FocusEvent e) {
				JTextField source = (JTextField) e.getComponent();
				source.setText("");
				source.removeFocusListener(this);
			}
		});
		tName1.setFont(new Font("Arial", Font.ITALIC, 12));
		tName2.setFont(new Font("Arial", Font.ITALIC, 12));
		tName1.setAlignmentX(SwingConstants.CENTER);
		tName2.setAlignmentX(SwingConstants.CENTER);
		tName1.setSize(190, 20);
		tName2.setSize(190, 20);

		// 2 'spinners' to select the dimensions
		SpinnerNumberModel spinnerNumberModel1 = new SpinnerNumberModel(10, 5, 1000, 1);
		SpinnerNumberModel spinnerNumberModel2 = new SpinnerNumberModel(10, 5, 1000, 1);

		JLabel dimX = new JLabel("Number of rows");
		dimX.setFont(new Font("Calibri", Font.ITALIC, 12));
		JLabel dimY = new JLabel("Number of columns");
		dimY.setFont(new Font("Calibri", Font.ITALIC, 12));

		JSpinner dim1 = new JSpinner(spinnerNumberModel1);
		dim1.setEditor(new JSpinner.DefaultEditor(dim1)); // not editable
		dim1.addChangeListener(new ChangeListener() { // get value from first spinner
			@Override
			public void stateChanged(ChangeEvent e) {
				boardDim[0] = (Integer) dim1.getValue() - 1;
			}
		});
		dim1.setPreferredSize(new Dimension(80, 30));

		JSpinner dim2 = new JSpinner(spinnerNumberModel2);
		dim2.setEditor(new JSpinner.DefaultEditor(dim2)); // get value from second spinner
		dim2.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				boardDim[1] = (Integer) dim2.getValue() - 1;
			}
		});

		dim2.setPreferredSize(new Dimension(80, 30));

		// label for title of scoring system chooser
		JLabel chooseScoringSystem = new JLabel("Choose the scoringsystem");
		chooseScoringSystem.setFont(new Font("Calibri", Font.ITALIC, 12));

		// retrieve from Static game value Game.scoringSystemStrings (String array)
		// Create the combo box
		// Create buttons and add this listener
		scoringSystemsList = new JComboBox(Game.scoringSystemsStrings);
		scoringSystemsList.setSelectedIndex(0);
		ComboBoxActionListener comboBoxActionListener = new ComboBoxActionListener();
		scoringSystemsList.addActionListener(comboBoxActionListener);

		// Button to go to highscores list
		JButton highscoresButton = new JButton("Highscores");
		highscoresButton.addActionListener(new ActionListener() {
			// show highScores screen
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == highscoresButton) {
					new HighScoreScreen(controller2);
					dispose();
				}
			}
		});

		// Button to go to rules sheet
		JButton rulesButton = new JButton("Rules");
		rulesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == rulesButton) {
					new RulesInGameFinal(controller2);
					dispose();
				}
			}
		});

		// Button 'Play Game'
		playGameButton = new JButton("Play Game");
		GameButtonActionListener gameButtonActionListener = new GameButtonActionListener();
		playGameButton.addActionListener(gameButtonActionListener);

		// create JPanels
		JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout(0, 0));

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout(0, 0));

		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(0, 2));
		p3.setBorder(BorderFactory.createLineBorder(Color.black));

		JPanel p4 = new JPanel();
		p4.setLayout(new GridLayout(0, 2));

		JPanel p5 = new JPanel();
		p5.setLayout(new BorderLayout(2, 0));

		JPanel p6 = new JPanel();
		p6.setLayout(new BorderLayout(0, 0));

		JPanel p7 = new JPanel();
		p7.setLayout(new BorderLayout(0, 0));

		// add components to panels
		p1.add(highscoresButton);

		p2.add(openButton);

		p3.add(dimX);
		p3.add(dimY);
		p3.add(dim1);
		p3.add(dim2);

		p4.add(tName1);
		p4.add(tName2);

		p5.add(chooseScoringSystem, BorderLayout.CENTER);
		p5.add(scoringSystemsList, BorderLayout.SOUTH);
		p5.setBorder(BorderFactory.createLineBorder(Color.black));

		p6.add(rulesButton);
		p7.add(playGameButton);

		// add panels to frame (boardScreen)
		this.add(p2);
		this.add(p4);
		this.add(p3);
		this.add(p5);
		this.add(p1);
		this.add(p6);
		this.add(p7);
		this.getContentPane().setPreferredSize(dimension);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();

		// show
		this.setTitle("Battleship2020 - Start");
		this.setVisible(true);
	}

	// Listens to the combo box
	class ComboBoxActionListener implements ActionListener {
		public ComboBoxActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == scoringSystemsList) {
				String sS = (String) scoringSystemsList.getSelectedItem();
				if (sS.equals(Game.scoringSystemsStrings[0])) {
					// Set variable that holds the selected scoringsystem value
					scoringSystem = true;
				}
			} // end if
		} // end actionPerformed
	}

	// Create inner class for the listener for the open-config-file button
	class OpenFileActionListener implements ActionListener {
		public OpenFileActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == openButton) {
				JFileChooser fc = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(filter);
				int returnVal = fc.showOpenDialog(frame);
				fc.setAcceptAllFileFilterUsed(false);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					openButton.invalidate();
					openButton.setText(file.getName());
					openButton.revalidate();
					openButton.repaint();
				}
			}
		}
	}

	// listener for the game button
	class GameButtonActionListener implements ActionListener {
		public GameButtonActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			// If the game button is clicked, save locally:
			if (e.getSource() == playGameButton) {
				// the names of both players
				namePlayer1 = tName1.getText();
				namePlayer2 = tName2.getText();
				try {
					// save the locally saved data in the appropriate objects
					controller.setData(boardDim, namePlayer1, namePlayer2, scoringSystem, file);
					// show boardScreen
					controller.playGame();
					controller.getStartScreen().dispose();
				} catch (IllegalArgumentException IAExc) { // also handles number format exception
					// if error from dimReader or from shipReader => retry
					controller.getStartScreen().dispose();
					controller.setStartScreen(new StartScreen(controller));
				} catch (FileNotFoundException FNFExc) {
					// unlikely to happen, as one can just choose one from system file explorer
					ErrorMessage.message("The file is not in this directory. Please try again:");
					controller.getStartScreen().dispose();
					controller.setStartScreen(new StartScreen(controller));
				}
			} // end if
		} // end actionPerformed
	}
}
