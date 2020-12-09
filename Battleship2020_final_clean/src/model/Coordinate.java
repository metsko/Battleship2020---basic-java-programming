package model;

public class Coordinate {
    private int x;
    private int y;

    public Coordinate(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean equals(Coordinate c) {
        return c.getX() == this.getX() && c.getY() == this.getY();
    }

}
