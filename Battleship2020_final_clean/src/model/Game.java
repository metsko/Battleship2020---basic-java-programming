package model;

import java.util.ArrayList;
import java.util.Arrays;

import controller.Randomizer;

public class Game {
	public final static String title = "Battleship2020";
	private Player[] winningPlayers;
	//flco: onzeker over het final zijn hiervan? is het een optie van dit te initializeren, negeer maar als dit een bewuste keuze is
	private final ArrayList<Round> rounds = new ArrayList<>();
	private Player[] playerArray = new Player[2];
	private final Board board;
	public static int nrOfShips = 4;
	private Ship[] ships = new Ship[nrOfShips];
	private ArrayList<Coordinate> previousHits = new ArrayList<>();
	public static final String[] scoringSystemsStrings = { "same points-per-hit", "no-first mover advantage" };
	private boolean scoringSystem = true; // if true same points-per-hit" else "no-first mover advantage"
	private final static double scoringConstant = 1.1;

	public Game() {
		// create board with standard dimensions
		this.board = new Board();
		// create2 players
		this.setPlayerArray(new Player[] { new Player(this, null, 0), new Player(this, null, 1) });
		// create ship list, initialized with a random coordinate, overlapping but does
		// not matter at this point, just to avoid loops randomizer
		this.setShips(initializeShips());
		this.rounds.add(new Round(null, this.getPlayerArray()[0], 1)); // set first round
		// initialize coordinates postpone until sure no file => avoid computation
	}

	private static Ship[] initializeShips() {
		Coordinate[] coo;
		coo = new Coordinate[2];
		int x0 = Randomizer.uniform(Board.minDim[0]); // => randomizer.uniform(5) => 6 options
		int y0 = Randomizer.uniform(Board.minDim[1]);
		//flco: enkele redundante lijnen code
		Coordinate cooStart = new Coordinate(x0, y0);
		Coordinate cooEnd = new Coordinate(x0, y0);
		coo[0] = cooStart;
		coo[1] = cooEnd;
		return new Ship[] { new Carrier(coo), new Destroyer(coo), new Battleship(coo), new Submarine(coo) };
	}

	public Board getBoard() {
		return this.board;
	}

	public Player[] getPlayerArray() {
		return playerArray;
	}

	public void setPlayerArray(Player[] playerArray) {
		this.playerArray = playerArray;
	}

	public Ship[] getShips() {
		return this.ships;
	}

	public Ship[] getFloatingShips() {
		return Arrays.stream(this.getShips()).filter(x -> x.getIsDown() == false).toArray(Ship[]::new);
	}

	public Ship getShipHit(Coordinate bombCoo) {
		/*
		 * We have to check only floating ships as bombCoo can only be a
		 * 'not-yet-hit'coordinate since buttons are disabled after one click.
		 */
		for (Ship ship : this.getFloatingShips()) {
			if (ship.getAllCoordinates().stream().anyMatch(coo -> coo.equals(bombCoo))) {
				return ship;
			} // end if
		} // end for
		return null;
	}

	public ArrayList<Coordinate> getPreviousHits() {
		return this.previousHits;
	}

	public void setPreviousHits(ArrayList<Coordinate> previousHits) {
		this.previousHits = previousHits;
	}

	public boolean getScoringSystem() {
		return scoringSystem;
	}

	public void setWinningPlayers(Player[] winningPlayers) {
		this.winningPlayers = winningPlayers;
	}

	public ArrayList<Round> getRounds() {
		return rounds;
	}

	public void setShips(Ship[] ships) {
		this.ships = ships;
	}

	public void setScoringSystem(boolean scoringSystem) {
		this.scoringSystem = scoringSystem;
	}

	public static double getScoringConstant() {
		return scoringConstant;
	}

}
