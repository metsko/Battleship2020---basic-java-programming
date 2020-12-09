package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Locale;
import java.util.Scanner;

import view.ErrorMessage;

public class ReadDimFromFile {
	public static int[] read(File file) throws IllegalArgumentException, NumberFormatException, FileNotFoundException {
		String numberFormatException = "Invalid input, valid input for the board dimension on first line is of format: 'int' and strictly larger than 4. See Rules.";
		try (Scanner sc = new Scanner(new FileReader(file))) {
			sc.useLocale(Locale.ENGLISH);
			try {
				int dim = Integer.parseInt(sc.nextLine()) - 1; // return dim = number of rows/columns -1 => row/col
																// index: 0, 1, 2, 3, 4 if board dimension = 4
				if (dim >= 4) {
					return new int[] { dim, dim };
				} else {
					ErrorMessage.message(numberFormatException);
					throw new IllegalArgumentException();
				}
			} catch (NumberFormatException ex) {
				ErrorMessage.message(numberFormatException);
				throw new NumberFormatException();
				// caught in start screen => try again
			}
		} // end try
	}
}
