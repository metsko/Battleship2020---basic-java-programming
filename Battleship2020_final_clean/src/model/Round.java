package model;

public class Round {
	private Player player;
	private int number;
	private Bomb bomb;

	public Round(Bomb b, Player p, int n) {
		this.setBomb(b);
		this.setNumber(n);
		this.setPlayer(p);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setBomb(Bomb bomb) {
		this.bomb = bomb;
	}
}
