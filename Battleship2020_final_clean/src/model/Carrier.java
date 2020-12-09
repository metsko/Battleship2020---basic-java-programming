package model;

import java.awt.Color;

public class Carrier extends Ship {
	static final Color color = Color.green;
	public static final int size = 5;
	static final int pointsPerHit = 1;
	static final int pointsWhenSunk = 2 * pointsPerHit;

	public Carrier(Coordinate[] coo) {
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