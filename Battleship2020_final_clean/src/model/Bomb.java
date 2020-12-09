package model;

public class Bomb {
	final private Coordinate bombCoo;

	public Bomb(Coordinate bombCoo) {
		this.bombCoo = bombCoo;
	}

	public Coordinate getCoo() {
		return bombCoo;
	}

}