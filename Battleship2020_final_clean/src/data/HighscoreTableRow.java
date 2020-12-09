package data;

public class HighscoreTableRow {
	private String rank;
	private final String dateTime;
	private final String name;
	private final String score;

	public HighscoreTableRow(String dateTime, String name, String score) {
		this.dateTime = dateTime;
		this.name = name;
		this.score = score;
	}

	public String getDateTime() {
		return dateTime;
	}

	public String getScore() {
		return score;
	}

	public String getName() {
		return name;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

}
