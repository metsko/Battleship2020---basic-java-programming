package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import model.*;

public class DataWriter {

	public static void writeRecords(Player[] players) {
		String sep = ",";
		File file = new File("highscores.txt");
		// creates new if file.exists() == false,
		// otherwise appends
		try (Writer out = new BufferedWriter(new FileWriter(file, true)))

		{
			String line;
			for (Player player : players) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				line = now.format(dtf) + sep + player.getName() + sep + player.getScore() + "\r\n";
				out.write(line);
			}
		} catch (IOException e) {
			System.out.println("Could not read the 'highscores.txt' file");
		}
	}
}
