package model;

import java.awt.Color;

public class Battleship extends Ship {
	static final Color color = Color.magenta;
	public static final int size = 4;
	static final int pointsPerHit = 2;
	static final int pointsWhenSunk = 2 * pointsPerHit;

	Battleship(Coordinate[] coo) {
		super(coo);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getPointsPerHit() {
		return pointsPerHit;
	}

	@Override
	public Color getColor() {
		return color;
	}

}
