package controller;

import static controller.DataWriter.writeRecords;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import model.*;
import view.*;

public class Controller {

    private StartScreen startScreen;
    private BoardScreen boardScreen;
    private Game game;

    // constructor
    public Controller() {
    }

    public static void main(String[] args) {
        // create new controller object
        Controller controller = new Controller();
        controller.initialize();
    }

    public void initialize() {
        this.setGame(new Game()); // initialize game
        // show start screen
        this.setStartScreen(new StartScreen(this));
    }

    public void playGame() {
        this.setBoardScreen(new BoardScreen(this));
    }

    public void updateGame(Coordinate bombCoo) {
        double scoreSystemFlag;
        // if first player or (second and other scoring system)
        if (this.game.getScoringSystem() || (this.getMostRecentPlayer().equals(this.game.getPlayerArray()[0])
                && !(this.game.getScoringSystem()))) {
            scoreSystemFlag = 0; // equal points for both players
        } else {
            scoreSystemFlag = 1;
        }
        Bomb bomb = this.createBomb(bombCoo);// create bomb
        this.getThisGameRound().setBomb(bomb);
        this.updateHits(bombCoo, this.game); // update hits
        double addToScore = 0;
        Ship shipHit = this.game.getShipHit(bombCoo); // return ship that is bombed
        if (shipHit != null) {
            // add bomb coordinate to coordinates hit
            shipHit.addHitCoo(bombCoo);
            // update addToScore value;
            addToScore = addToScore + shipHit.getPointsPerHit() * Math.pow(Game.getScoringConstant(), scoreSystemFlag);
            // updateDowns (done in variable definition)
            if (shipHit.getIsDown()) {
                // updateScores;
                addToScore = addToScore * 2;
            }
        }
        // update score
        this.getMostRecentPlayer().setScore(new BigDecimal(addToScore).setScale(2, RoundingMode.HALF_EVEN)
                .add(new BigDecimal(this.getMostRecentPlayer().getScore()).setScale(2, RoundingMode.HALF_EVEN)));
        this.setNewRound(); // we set the round for the next player
    }

    public void endGame() {
        // set this game's winning Players
        this.game.setWinningPlayers(this.getWinningPlayers());
        new EndOfGameScreen(this);
        writeRecords(this.getGame().getPlayerArray());
    }

    public void setNewRound() { // after previous player clicked location

        // this round's player = not last rounds player
        List<Player> list = new ArrayList<>();
        for (Player p : this.game.getPlayerArray()) {
            if (p != this.getMostRecentPlayer()) {
                list.add(p);
            }
        }
        Player newPlayer = (Player) list.toArray()[0];
        // update round
        this.game.getRounds().add(new Round(null, newPlayer, this.getThisGameRound().getNumber() + 1));
    }

    // add hit to game's previous hits
    public void updateHits(Coordinate coo, Game game) {
        ArrayList<Coordinate> list = game.getPreviousHits();
        list.add(coo); // add coordinate of new hit
        game.setPreviousHits(list);
    }

    public Round getThisGameRound() {
        return this.game.getRounds().get(this.game.getRounds().size() - 1);
    }

    public Player getMostRecentPlayer() {
        return getThisGameRound().getPlayer();
    }

    public Bomb createBomb(Coordinate bombCoo) {
        return new Bomb(bombCoo);// create bomb
    }

    public void setData(int[] boardDim, String namePlayer1, String namePlayer2, boolean scoringSystem, File file)

            throws IllegalArgumentException, FileNotFoundException {

        // register some retrieved data
        // set board dimension
        this.game.getBoard().setDim(boardDim);
        // set player names
        this.setPlayerNames(namePlayer1, namePlayer2);
        // Set scoringsystem
        this.game.setScoringSystem(scoringSystem);
        // set Ships & board: if no file => set ships randomly and board by default
        if (file != null) {
            this.setShipsAndBoardFromConfigFile(file);
        } else {
            Randomizer randomizer = new Randomizer();
            this.getGame().setShips(randomizer.randomizer(this.getGame()));
        }
    }

    public void setPlayerNames(String namePlayer1, String namePlayer2) {
        if (!(namePlayer1.isEmpty())) { // if the field is not empty
            this.getGame().getPlayerArray()[0].setName(namePlayer1, 0);
        } // do nothing
        if (!(namePlayer2.isEmpty())) { // if the field is not empty
            this.getGame().getPlayerArray()[1].setName(namePlayer2, 1);
        } // do nothing
    }

    public void setShipsAndBoardFromConfigFile(File file) throws IllegalArgumentException, FileNotFoundException {
        // user inserted a fileName => set dimension & coordinates
        this.getGame().getBoard().setDim(ReadDimFromFile.read(file)); // check performed in reader
        for (Ship ship : this.getGame().getShips()) {
            // read & set coordinates of ship
            ship.setCoo(ReadShipFromFile.readWrite(file, ship));
        }
        // check if no overlapping ships and within board dimensions =>
        // message the user and start the game again
        ArrayList<Coordinate> overlappingCoordinates = checkShipCoordinatesNotOverlapping(this.getGame().getShips());
        if (overlappingCoordinates.size() > 0) {
            StringBuilder s = new StringBuilder();
            for (Coordinate coo : overlappingCoordinates) {
                s.append("(" + coo.getX() + "," + coo.getY() + ")");
            }
            ErrorMessage.message("You have entered overlapping ships on: " + s);
            throw new IllegalArgumentException();
        }
    }

    private ArrayList<Coordinate> checkShipCoordinatesNotOverlapping(Ship[] ships) {
        // ships cannot overlap
        ArrayList<Coordinate> overlappingCoordinates = new ArrayList<>();
        ArrayList<Ship> shipsToCheck = new ArrayList<>(Arrays.asList(ships));
        for (Ship ship : ships) { // loop ships
            shipsToCheck.remove(ship);
            checkShipInBoardBounds(ship);
            checkShipHorizontalOrVertical(ship);
            checkShipSize(ship);
            ArrayList<Coordinate> cooList = new ArrayList<>();
            for (Ship otherShip : shipsToCheck) {
                for (Coordinate coo : otherShip.getAllCoordinates()) {
                    cooList.add(coo);
                }
                // end coordinate loop
            }
            // end loop other ships
            // Add any overlapping coordinates
            for (Coordinate coo : cooList) {
                if (ship.getAllCoordinates().stream().anyMatch(c -> c.equals(coo))
                        && overlappingCoordinates.contains(coo)) {
                    overlappingCoordinates.add(coo);
                }
            }
        } // end first loop over all ships
          // return overlapping coordinates
        return overlappingCoordinates;
    }

    private void checkShipInBoardBounds(Ship ship) {
        // check x & y dimension
        boolean maxXBound = ship.getCoo()[0].getX() > this.game.getBoard().getDim()[0];
        boolean minXBound = ship.getCoo()[0].getX() < 0;
        boolean maxYBound = ship.getCoo()[0].getY() > this.game.getBoard().getDim()[1];
        boolean minYBound = ship.getCoo()[0].getX() < 0;
        if (maxXBound || minXBound || maxYBound || minYBound) {
            ErrorMessage.message("Your " + ship.getClass().getName() + " went out of the board bounds.");
            throw new IllegalArgumentException();
        }
    }

    private void checkShipHorizontalOrVertical(Ship ship) {
        // remark we don't have to check if coordinates 'folllow up' since ship is
        // defined by first and last coordinate=>
        // check whether they are horizontal or vertical, else => error
        int i = 0; // current index of ship.getAllCoordinates()
        for (Coordinate coo : ship.getAllCoordinates()) {
            if (i == 0) {
                // do nothing
            } else {
                // check if horizontal ship
                if (ship.checkHorizontal()) { // also checks whether the coordinates follow up
                    // y coordinates also right?
                    if (coo.getY() != ship.getCoo()[0].getY()) {
                        String error3 = "Your " + ship.getClass().getSimpleName()
                                + " is not entirely horizontal due to Coordinate " + ship.getCoo()[0].getX() + ","
                                + coo.getY() + ".";
                        ErrorMessage.message(error3);
                        throw new IllegalArgumentException();
                    }
                }
                // vertical ship
                else {
                    // x coordinates also right?
                    if (coo.getX() != ship.getCoo()[0].getX()) {
                        String error3 = "Your " + ship.getClass().getSimpleName()
                                + " is not entirely vertical due to Coordinate " + ship.getCoo()[0].getY() + ","
                                + coo.getX() + ".";
                        ErrorMessage.message(error3);
                        throw new IllegalArgumentException();
                    }
                }
            } // end first else
            i++;
        }
    }

    private static void checkShipSize(Ship ship) throws IllegalArgumentException {
        if (ship.checkHorizontal()) {
            int submittedSize = ship.getCoo()[1].getX() - ship.getCoo()[0].getX();
            if (submittedSize != ship.getSize() - 1) {
                String error = "Your " + ship.getClass().getSimpleName() + "has size: " + submittedSize + 1
                        + " while it should have size: " + ship.getSize();
                ErrorMessage.message(error);
                throw new IllegalArgumentException();
            }
        } else {
            int submittedSize = ship.getCoo()[1].getY() - ship.getCoo()[0].getY();
            if (submittedSize != ship.getSize() - 1) {
                String error = "Your " + ship.getClass().getSimpleName() + "has size: " + submittedSize;
                ErrorMessage.message(error);
                throw new IllegalArgumentException();
            }
        }
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public StartScreen getStartScreen() {
        return this.startScreen;
    }

    public void setStartScreen(StartScreen startScreen) {
        this.startScreen = startScreen;
    }

    protected void setBoardScreen(BoardScreen boardScreen) {
        this.boardScreen = boardScreen;
    }

    public Player[] getWinningPlayers() {
        ArrayList<Player> pList = new ArrayList<>();
        pList.add(this.getGame().getPlayerArray()[0]);
        if (this.getGame().getPlayerArray()[0].getScore() < this.getGame().getPlayerArray()[1].getScore()) {
            pList.remove(0);
            pList.add(this.getGame().getPlayerArray()[1]);
            return pList.toArray(new Player[0]);
        } else if (this.getGame().getPlayerArray()[0].getScore() == this.getGame().getPlayerArray()[1].getScore()) {
            pList.add(this.getGame().getPlayerArray()[1]);
            return pList.toArray(new Player[0]);
        } else {
            return pList.toArray(new Player[0]);
        }
    }
}