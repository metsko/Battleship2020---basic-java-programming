package model;

import java.math.BigDecimal;

public class Player {
	final Game game;

	private String name;
	public final static String[] defaultNames = { "Player1", "Player2" };
	private double score;

	// constructor
	public Player(Game game, String name, int number) {
		this.game = game;
		this.setName(name, number);
	}

	public void setName(String name, int number) {
		if (name == null || name.equals("") || name.equals("Insert name Player1...")
				|| name.equals("Insert name Player2...")) {
			this.name = defaultNames[number];
		} else {
			this.name = name;
		}
	}

	public void setScore(BigDecimal bigDecimal) {
		this.score = bigDecimal.doubleValue();
	}

	public double getScore() {
		return this.score;
	}

	public String getName() {
		return name;
	}
}
