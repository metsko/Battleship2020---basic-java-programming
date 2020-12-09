package view;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import controller.Controller;
import model.Player;

public class EndOfGameScreen {
	// UI settings
	Dimension frameDimension = new Dimension(1100, 650);

	// Swing GUI
	private final JFrame frame;
	private JLabel winningPlayerLabel;
	private final JLabel imageLabel;
	private final JButton backButton;

	// Input
	Controller controller;

	public EndOfGameScreen(controller.Controller controller2) {
		this.controller = controller2;

		// frame
		frame = new JFrame();
		frame.setTitle("Battleship2020 - End of Game");
		frame.setPreferredSize(frameDimension);
		frame.setLayout(new BorderLayout());

		// backbutton => close screen and initialize a new game (including showing a new
		// startScreen)
		backButton = new JButton("To Start");
		backButton.setBackground(Color.red);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == backButton) {
					controller.initialize();
					frame.dispose();
				}
			}
		});

		// JLabel with information on who wins the game
		this.setWinningPlayerLabel();
		winningPlayerLabel.setFont(new Font("Calibri", Font.BOLD, 30));

		// Picture, for fun
		File file = new File("images/EndOfGame.jpg");
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(file);
		} catch (IOException e1) {
			ErrorMessage.message("Could not find file 'images/EndOfGame.jpg'.");
		}
		Image scaled = bimg.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(scaled);
		imageLabel = new JLabel(icon, SwingConstants.CENTER);

		// add components to frame
		frame.add(winningPlayerLabel, BorderLayout.NORTH);
		frame.add(imageLabel, BorderLayout.CENTER);
		frame.add(backButton, BorderLayout.SOUTH);

		// finish and visualize
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void setWinningPlayerLabel() {
		Player[] winningPlayers = controller.getWinningPlayers();
		if (winningPlayers.length > 1) {
			winningPlayerLabel = new JLabel("Congratulations " + winningPlayers[0].getName() + " and "
					+ winningPlayers[1].getName() + " have both won!", SwingConstants.CENTER);
		} else {
			winningPlayerLabel = new JLabel("Congratulations " + winningPlayers[0].getName() + ", you won!",
					SwingConstants.CENTER);
		}
	}

}
