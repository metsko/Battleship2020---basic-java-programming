package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.*;

public class Randomizer {
	int dimX;
	int dimY;
	int loopCounter = 0;
	List<Integer> availableRows;
	List<Integer> availableColumns;
	int[][] takenCoo;
	Board board;
	ArrayList<Ship> ships = new ArrayList<>();
	ArrayList<Ship> shipsNotPositioned;
	ArrayList<Coordinate> possibleRowPositions;
	ArrayList<Coordinate> possibleColumnPositions;
	Coordinate cooStart;
	Coordinate cooEnd;

	Ship smallestShip;

	public Randomizer() {
	}

	@SuppressWarnings("unchecked")

	public Ship[] randomizer(Game game) {
		// get board dimensions
		this.dimX = game.getBoard().getDim()[0]; // if dimX = 4 => 4 columns: 0,1,2,3,4
		this.dimY = game.getBoard().getDim()[1];
		// get ships
		this.ships = new ArrayList<>(Arrays.asList(game.getShips()));
		// to traverse y dimension (vertically), range(1,y) returns 1...y-1
		this.availableRows = IntStream.range(0, dimY + 1).boxed().collect(Collectors.toList());
		// to traverse x dimension (horizontally)
		this.availableColumns = IntStream.range(0, dimX + 1).boxed().collect(Collectors.toList());
		// zeros => with x-dim: 0...dimX, y-dim: 0...dimY (other direction than normal
		// y-axis!!!!!)
		this.takenCoo = new int[dimY + 1][dimX + 1]; // you spefic number of rows (=> Y-dimension) and number of columns
														// (=> X-dimension) and +1

		// Create one list to track the ships that have been positioned and those that
		// were not
		this.shipsNotPositioned = ((ArrayList<Ship>) ships.clone());

		// random order, shuffle ships list
		Collections.shuffle(this.ships); // random ships

		// assign to ships
		for (Ship ship : this.ships) {
			/*
			 * update PossibleRowPositions (horizontal) and possibleColumnPositions
			 * (vertical) using set of updated (after each 'ship'-iteration) availableRows
			 * and -Columns.
			 */

			this.possibleRowPositions = this.getPossibleHorizontalStartPositions(ship);
			this.possibleColumnPositions = this.getPossibleVerticalStartPositions(ship);

			// choose smallest to-be-positioned ship
			this.smallestShip = Collections.min(shipsNotPositioned, Comparator.comparing(s -> s.getSize()));

			// case1
			if (this.possibleRowPositions.isEmpty()
					&& this.possibleColumnPositions.isEmpty() /* && shipsNotPositioned.size()>0 */) { //
				// try again
				System.out.println("new loop!");
				this.loopCounter++;
				Randomizer rand = new Randomizer();
				ships = new ArrayList<>(Arrays.asList(rand.randomizer(game)));
				break;
			}

			// Case 2, position vertically (only option)
			else if (this.possibleRowPositions.size() == 0) {
				this.setVerticalShipCoo(ship);
			}

			// Case 3, position horizontally (only option)
			else if (this.possibleColumnPositions.size() == 0) {
				this.setHorizontalShipCoo(ship);
			}
			// case4 both are a possibility
			else {
				// create coin
				int coin = Randomizer.bernoulli();
				if (coin == 1) { // 50% chance of positioning this ship horizontally.
					this.setHorizontalShipCoo(ship);
				} else {
					this.setVerticalShipCoo(ship);
				}
			} // end case4
				// Update the second shiplist so that it only contains the ships that are NOT
				// yet positioned
			this.shipsNotPositioned.remove(ship);
		} // end for-loop
			// return ship array
		return ships.toArray(Ship[]::new);
	}

	public ArrayList<Coordinate> getPossibleHorizontalStartPositions(Ship ship) {
		// loop over all availble rows (y-dimension) and then loop over all possible
		// start positions
		// in that given row (x-dimension)
		// Create ArrayList to store possible start-coordinates such that this ship fits
		// in the row
		ArrayList<Coordinate> cooRowList = new ArrayList<>();
		for (int r : this.availableRows) {
			int[] takenRow = this.takenCoo[r];

			// traverse row and check for start coordinates such that ship fits
			// if we encounter a 'taken coordinate', start counting over
			int count = 0;
			// need counter for more than size times unoccupied position
			// int added =0;
			for (int i = 0; i <= this.dimX; i++) { // i: 0,1,2,3,...,dimX
				// for (int i = dimY-1;i>=0 ;i--)
				if (takenRow[i] == 100) {
					count = 0;
				} else {
					count++;
					if (count >= ship.getSize()) { // keep on adding as long as no taken Coo's are encountered
						int s = i - count + 1;
						// added=0;
						cooRowList.add(new Coordinate(s, r));
					} // end if
				} // end else
			} // end for loop
		} // end first for loop
			// return possible, horizontal start positions
		return cooRowList;
	}

	public ArrayList<Coordinate> getPossibleVerticalStartPositions(Ship ship) {
		/*
		 * Get horizontal board dimension => loop over columns by traversing board
		 * horizontally (x-dimension) and loop over each column (y-dimension) Create
		 * ArrayList to store possible start-coordinates such that this ship fits in the
		 * column
		 */
		ArrayList<Coordinate> cooColumnList = new ArrayList<>();
		for (int c : availableColumns) {
			int[] takenColumns = Randomizer.getColumn(takenCoo, c);
			// if we encounter a 'taken coordinate', start counting over
			int count = 0;
			// need counter for more than size times unoccupied position
			// int added =0;
			// traverse column and check for start coordinates such that ship fits
			for (int i = 0; i <= dimY; i++) { // i: 0,1,2,3,...,dimY-1
				if (takenColumns[i] == 100) {
					count = 0;
				} else {
					count++;
					if (count >= ship.getSize()) { // keep on adding as long as no taken Coo's are encountered
						// added=0;
						int s = i - count + 1;
						cooColumnList.add(new Coordinate(c, s));
					}
				} // end else
			} // end 2nd for
		} // end first for
		return cooColumnList;
	}

	private void setVerticalShipCoo(Ship ship) {
		// randomize start- and endCoordinate given set of start coordinates
		// (=PossibleRowPositions)
		this.cooStart = possibleColumnPositions.get(Randomizer.uniform(possibleColumnPositions.size() - 1));
		this.cooEnd = new Coordinate(this.cooStart.getX(), this.cooStart.getY() + ship.getSize() - 1);
		ship.setCoo(new Coordinate[] { this.cooStart, cooEnd });
		// fill in taken coo's with 100 in matrix => taken:100, not taken:0
		for (int i = 0; i < ship.getSize(); i++) {
			this.takenCoo[this.cooStart.getY() + i][this.cooStart.getX()] = 100; // first row index = y, then column =x
		}
		// update availableColumns => delete col if no ship fits (if smallest
		// to-be-positioned ship does not fit.)
		// Coordinate innerCooStart = cooStart;
		if (this.dimY - this.cooEnd.getY() < this.smallestShip.getSize()
				&& this.cooStart.getY() - this.smallestShip.getSize() <= 0) {
			this.availableColumns.remove(Integer.valueOf(this.cooStart.getX()));
		}
	}

	private void setHorizontalShipCoo(Ship ship) {
		// randomize start- and endCoordinate given set of start coordinates
		// (=PossibleRowPositions)
		this.cooStart = this.possibleRowPositions.get(Randomizer.uniform(this.possibleRowPositions.size() - 1));
		this.cooEnd = new Coordinate(this.cooStart.getX() + ship.getSize() - 1, this.cooStart.getY());
		ship.setCoo(new Coordinate[] { this.cooStart, this.cooEnd });
		// fill in taken coo's with 100 in matrix => taken:100, not taken:0
		for (int i = 0; i < ship.getSize(); i++) {
			this.takenCoo[this.cooStart.getY()][this.cooStart.getX() + i] = 100; // first row index = y, then column =x
		}
		// heuristic
		// update availableRows => delete row if no ship fits (if smallest
		// to-be-positioned ship does not fit.)
		if (this.dimX - this.cooEnd.getX() < this.smallestShip.getSize()
				&& this.cooStart.getX() - this.smallestShip.getSize() <= 0) {
			this.availableRows.remove(Integer.valueOf(this.cooStart.getY()));
		}
	}

	// following method from stackoverflow
	public static int[] getColumn(int[][] array, int index) {
		int colSize = array.length; // number of rows = col size
		int[] column = new int[colSize];
		for (int i = 0; i < colSize; i++) {
			column[i] = array[i][index];
		}
		return column;
	}

	public static ArrayList<Ship> getHorizontalShips(ArrayList<Ship> Ship) {
		return (ArrayList<Ship>) Ship.stream().filter(s -> s.getCoo()[0].getY() == s.getCoo()[1].getY())
				.collect(Collectors.toList());
	}

	public static ArrayList<Ship> getVerticalShips(ArrayList<Ship> Ship) {
		return (ArrayList<Ship>) Ship.stream().filter(s -> s.getCoo()[0].getX() == s.getCoo()[1].getX())
				.collect(Collectors.toList());
	}

	public static int bernoulli() {
		double rand = Math.random();
		if (rand < 0.5) {
			return 0;
		} else {
			return 1;
		}
	}

	public static int uniform(int x) {
		double rand = Math.random(); // This method returns a pseudorandom double greater than or equal to 0.0 and
										// less than 1.0.
		return (int) Math.round(rand * x); // The java.lang.Math.round(float a) returns the closest int to the argument.
	}
}