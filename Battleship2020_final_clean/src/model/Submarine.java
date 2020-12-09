package model;

import java.awt.Color;

public class Submarine extends Ship {
	static final Color color = Color.yellow;
	public static final int size = 3;
	static final int pointsPerHit = 3;

	public Submarine(Coordinate[] coo) {
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