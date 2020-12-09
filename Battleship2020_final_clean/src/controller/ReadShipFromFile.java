package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Locale;
import java.util.Scanner;

import model.Coordinate;
import model.Game;
import model.Ship;
import view.ErrorMessage;

public class ReadShipFromFile {

	public static Coordinate[] readWrite(File file, Ship ship) throws FileNotFoundException {
		Coordinate[] coo;
		try (Scanner sc = new Scanner(new FileReader(file))) {
			// check if we have enough lines to specify dim and (Game.nrOfShips) number of
			// ships
			checkNumberOfLines(file);

			sc.useLocale(Locale.ENGLISH);
			// ReadDimFromFile class already reads the dimension
			sc.nextLine();

			while (sc.hasNext()) {
				String line = sc.nextLine();
				String[] splittedLine = line.split("\\;");
				// get shipname
				String className = splittedLine[0];
				// if we found the ship's coordinates line => try to read it => issues create
				// error and lead to new start screen for retry
				if (className.equals(ship.getClass().getSimpleName())) {
					coo = getStartAndEndCoordinate(splittedLine, ship);
					return coo;
				}
			}
			// no matching ship name
			{
				ErrorMessage.message(ship.getClass().getSimpleName()
						+ " has not been specified a valid Ship Name, should be one of {Carrier, Destroyer, Submarine, Battleship}. Please, try again.");
				throw new IllegalArgumentException();
			}
		}
	} // end try

	private static Coordinate[] getStartAndEndCoordinate(String[] splittedLine, Ship ship) {

		// check the length of this line, in fact not really necessary, but could be
		// informative
		if (splittedLine.length != ship.getSize() + 1) {
			ErrorMessage.message("You did not specify " + ship.getSize() + " coordinates for shiptype "
					+ ship.getClass().getName() + ", please try again.");
			throw new IllegalArgumentException();
		} else {
			// startcoordinate
			String[] splittedStartCoo = splittedLine[1].split("\\*");
			// check format
			checkFormatSplittedCoordinate(splittedStartCoo, ship, true);
			Coordinate startCoo = new Coordinate(Integer.parseInt(splittedStartCoo[0]),
					Integer.parseInt(splittedStartCoo[1]));
			// endcoordinate
			String[] splittedEndCoo = splittedLine[ship.getSize()].split("\\*");
			checkFormatSplittedCoordinate(splittedEndCoo, ship, false);
			Coordinate endCoo = new Coordinate(Integer.parseInt(splittedEndCoo[0]),
					Integer.parseInt(splittedEndCoo[1]));
			return new Coordinate[] { startCoo, endCoo };
		}
	}

	private static void checkFormatSplittedCoordinate(String[] string, Ship ship, boolean first)
			throws NumberFormatException {
		// is it the first or last coordinate,
		// just for error message
		String coordinate = "last";
		if (first) {
			coordinate = "first";
		}
		// check format size (2 ints, after seperating)
		if (string.length != 2) {
			ErrorMessage.message("Your " + coordinate + " coordinate for shiptype " + ship.getClass().getName()
					+ " does not respect the size of 3 characters, it has got " + string.length
					+ " characters. See Rules.");
			throw new IllegalArgumentException("Your " + coordinate + " coordinate for shiptype "
					+ ship.getClass().getName() + " does not respect the size of 3 characters, it has got "
					+ string.length + " characters. See Rules.");
		}
		String firstInt = string[0];
		String lastInt = string[1];

		// check firstint
		try {
			Integer.parseInt(firstInt);
		} catch (NumberFormatException e) {
			ErrorMessage.message("You specified " + string + ", which is not an integer for the " + coordinate
					+ " coordinate of shiptype " + ship.getClass().getSimpleName() + ". See Rules.");
			throw new NumberFormatException("You specified " + string + ", which is not an integer for the "
					+ coordinate + " coordinate of shiptype " + ship.getClass().getSimpleName() + ". See Rules.");
		}
		// check lastint
		try {
			Integer.parseInt(lastInt);
		} catch (NumberFormatException e) {
			ErrorMessage.message("You specified " + string + ", which is not an integer for the " + coordinate
					+ " coordinate of shiptype " + ship.getClass().getSimpleName() + ". See Rules.");
			throw new NumberFormatException("You specified " + string + ", which is not an integer for the "
					+ coordinate + " coordinate of shiptype " + ship.getClass().getSimpleName() + ".");
		}
	}

	private static void checkNumberOfLines(File file) {
		int counter = 0;
		Scanner sc2;
		try {
			sc2 = new Scanner(new FileReader(file));
			while (sc2.hasNext() && counter < Game.nrOfShips + 1) { // specifiy one dim and 4 ships => 5 lines
				sc2.nextLine();
				counter++;
			}
			if (counter > Game.nrOfShips + 1) {
				ErrorMessage.message("Too many lines in config file, it should be 5 lines");
				throw new IllegalArgumentException();
			}
		} catch (FileNotFoundException e) {
			ErrorMessage.message("Could nog find file: '" + file.getName() + "'. See Rules.");
		}
	}
}