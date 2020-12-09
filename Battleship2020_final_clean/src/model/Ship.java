package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class Ship {
	public static int size;
	public static Color color;
	private Coordinate[] coo;
	private final ArrayList<Coordinate> hitCoo = new ArrayList<>(); // Coordinates that are hit

	Ship(Coordinate[] coo) {
		this.setCoo(coo);
	}

	public void setCoo(Coordinate[] coo) {
		this.coo = coo;
	}

	public Coordinate[] getCoo() {
		return coo;
	}

	public boolean checkHorizontal() {
		// true => horizontal ship, else vertical
		return (this.getCoo()[1].getX() - this.getCoo()[0].getX() > 0);
	}

	public ArrayList<Coordinate> getAllCoordinates() {
		// Neglect intermediate coordinates =>
		ArrayList<Coordinate> cooList = new ArrayList<>();
		if (this.checkHorizontal()) {
			for (int i = 0; i < this.getSize(); i++) {
				cooList.add(new Coordinate(this.getCoo()[0].getX() + i, this.getCoo()[0].getY()));
				// end loop to add coordinates
			}
		} // end if
		else //flco: inconsistente hakenplaatsing
			for (int i = 0; i < this.getSize(); i++) {
				cooList.add(new Coordinate(this.getCoo()[0].getX(), this.getCoo()[0].getY() + i));
			} // end loop to add coordinates
		return cooList;
	}

	public boolean getIsDown() {
		// ships goes down if it's hit once on all coordinates
		return this.getSize() == this.getHitCoo().size();
	}

	public abstract int getSize();

	public abstract int getPointsPerHit();

	public abstract Color getColor();

	public List<Coordinate> getHitCoo() {
		return hitCoo;
	}

	public void addHitCoo(Coordinate hitCoo) {
		this.hitCoo.add(hitCoo);
	}

}
