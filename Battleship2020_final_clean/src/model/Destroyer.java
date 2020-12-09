package model;

import java.awt.Color;

public class Destroyer extends Ship {
	static final Color color = Color.black;
	public static final int size = 2;
	static final int pointsPerHit = 4;
	static final int pointsWhenSunk = 2 * pointsPerHit;

	public Destroyer(Coordinate[] coo) {
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
