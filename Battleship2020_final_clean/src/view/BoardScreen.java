package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.Controller;
import model.Coordinate;
import model.Ship;
import model.Game;

public class BoardScreen {
	Controller controller;

	// GUI
	JFrame frame;
	BoardPanel boardPanel;
	ScorePanel scorePanel;
	JPanel turnPanel;

	// constructor
	public BoardScreen(Controller controller) {
		this.controller = controller;
		this.createAndShowGUI();
	}

	// BoardPanel class
	private class BoardPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		// new action listener class
		class TileActionListener implements ActionListener {
			// BoardPanel boardPanel;
			JButton but;
			Coordinate bombCoo;

			public TileActionListener(JButton button, int[] bombCoo) {
				this.bombCoo = new Coordinate(bombCoo[0], bombCoo[1]);
				this.but = button;
			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == this.but) {
					// everytime a 'bomb is thrown', we update the game state,
					// and refresh the panels.
					controller.updateGame(bombCoo);
					refreshTurnPanel();
					refreshBoardPanel();
					refreshScorePanel();
					frame.revalidate();
					frame.repaint();

					// if no ships are left, close the screen and end the game
					if (controller.getGame().getFloatingShips().length == 0) {
						frame.dispose();
						controller.endGame();
					} // end 2nd if
				} // end first if
			} // end actionPerformed
		} // end class TileActionlistener

		// BoardPanel constructor
		BoardPanel() {
			super();
			this.setPreferredSize(new Dimension(1200, 800));
			this.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;

			//Get board dimensions
			int dimBoardx = controller.getGame().getBoard().getDim()[0];
			int dimBoardy = controller.getGame().getBoard().getDim()[1];

			// The board has dimensions: x: 0 -> dimX, y: 0 -> dimY
			for (int y = 0; y <= dimBoardy; y++) {
				for (int x = 0; x <= dimBoardx; x++) {
					JButton button = new JButton();
					c.weighty = 1;
					c.weightx = 1;
					c.gridx = x;
					c.gridy = y;

					//If this coordinate is hit => this color, and disable button! (too avoid double hits on same coordinate)
					Coordinate bombCoo = new Coordinate(x, y);
					button.setBackground(getColor(bombCoo, controller.getGame()));
					if (controller.getGame().getPreviousHits().stream().anyMatch(coo -> coo.equals(bombCoo))) { 
						button.setEnabled(false);
					}

					// add Action listener (defined before)
					button.addActionListener(new TileActionListener(button, new int[] { c.gridx, c.gridy }));
					this.add(button, c);
				} // end x loop
			} // end y loop
		}// end of constructor
	}// end of Boardpanel inner class

	// ScorePanel inner class to show player scores
	private class ScorePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		// constructor
		ScorePanel() {
			super();
			this.setPreferredSize(new Dimension(400, 100));
			this.setLayout(new GridBagLayout());

			// add label for score player 1
			JLabel labelScore1 = new JLabel(
					controller.getGame().getPlayerArray()[0].getName() + " score: "
							+ controller.getGame().getPlayerArray()[0].getScore() + "  points.         ",
					SwingConstants.LEFT);
			labelScore1.setFont(new Font("Calibri", Font.BOLD, 20));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.5; // set all equal such that they enlarge when screen is enlarged
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.insets.right = 10;
			gbc.anchor = GridBagConstraints.LINE_START; // center-left
			this.add(labelScore1);

			// add label for score player 2
			JLabel labelScore2 = new JLabel("         " + controller.getGame().getPlayerArray()[1].getName()
					+ "'s score: " + controller.getGame().getPlayerArray()[1].getScore() + " points.",
					SwingConstants.RIGHT);
			labelScore2.setFont(new Font("Calibri", Font.BOLD, 20));
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.5;
			gbc.gridy = 0;
			gbc.gridx = 1;
			gbc.insets.left = 10;
			gbc.anchor = GridBagConstraints.LINE_END; // center-right
			this.add(labelScore2);

			// set black border
			this.setBorder(BorderFactory.createLineBorder(Color.black));
		} // end constructor scorePanel
	} // end inner class ScorePanel

	private class TurnPanel extends JPanel { // shows round and who's turn it is
		private static final long serialVersionUID = 1L;

		// constructor
		TurnPanel() {
			super();
			this.setPreferredSize(new Dimension(400, 100));
			// Set border
			this.setBorder(BorderFactory.createLineBorder(Color.black));
			// Set the round
			JLabel turn = new JLabel("Round " + controller.getThisGameRound().getNumber() + ", "
					+ controller.getThisGameRound().getPlayer().getName() + "'s turn.", SwingConstants.CENTER);
			turn.setFont(new Font("Calibri", Font.BOLD, 20));
			this.add(turn);
		}
	}

	public void refreshTurnPanel() {
		// update TurnPanel
		frame.remove(turnPanel);
		turnPanel = new TurnPanel();
		frame.add(turnPanel, BorderLayout.NORTH);
	}

	public void refreshScorePanel() {
		// update scorePanel
		frame.remove(scorePanel);
		scorePanel = new ScorePanel();
		frame.add(scorePanel, BorderLayout.SOUTH);

	}

	public void refreshBoardPanel() {
		// update boardPanel
		frame.remove(boardPanel);
		boardPanel = new BoardPanel();
		frame.add(boardPanel, BorderLayout.CENTER);
	}

	private Color getColor(Coordinate coo, Game game) {
		// Color of a button should match the ship/water's color that is on that location
		// if the the location has been hit before
		// else grey
		if (game.getPreviousHits().stream().anyMatch(x -> x.equals(coo))) {
			// get if a ship is hit, also null possible as return
			Ship shipHit = null;
			for (Ship ship : game.getShips()) {
				if (ship.getHitCoo().stream().anyMatch(c -> c.equals(coo))) {
					shipHit = ship;
				}
			}
			// no ship hit
			if (shipHit == null) {
				// Water
				return Color.blue;
			}
			// return ship's color
			else {
				return shipHit.getColor();
			}
		} // end second if
			// not hit
		else {
			return Color.LIGHT_GRAY;
		} // end second else
	}

	private void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("Battleship2020");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the turnPanel pane.
		turnPanel = new TurnPanel();
		frame.add(turnPanel, BorderLayout.NORTH);

		// Set up boardPanel
		boardPanel = new BoardPanel();
		frame.add(boardPanel, BorderLayout.CENTER);

		// set up scorePanel
		scorePanel = new ScorePanel();
		frame.add(scorePanel, BorderLayout.SOUTH);

		// Display the window.
		// frame.getContentPane().setSize(2000,2000);
		frame.pack();
		frame.setVisible(true);
	}
}