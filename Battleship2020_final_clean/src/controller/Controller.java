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

    //flco: is er een reden om dit niet in je constructor te doen?
    //dan kan je dit ook doen zonder je setters te gebruiken  
    public void initialize() {
        //flco: voor locale methodes hoef je geen this.setter() te gebruiken ;) 
        //ook protip, je gebruikt setStartScreen maar 1 keer voor je initialisatie
        //EN deze staat 200 lijnen verder benenden, kwestie van leesbaarheid en aantal lijnen code is het altijd fijner om locaal te kunnen werken
        //algemeen gezien is het good practice om je getters en setters vlak onder je constructor te zetten
        this.setGame(new Game()); // initialize game
        // show start screen
        this.setStartScreen(new StartScreen(this));
    }

    //flco: methode die als "signaal" gecalled wordt, eventueel comment
    public void playGame() {
        this.setBoardScreen(new BoardScreen(this));
    }

    //flco: functie voelt lang aan, zou het mogelijk zijn om de inhoud van deze functie in 2 te splitsen?
    public void updateGame(Coordinate bombCoo) {
        //flco: voor simpele setters als deze can je dit op 1 lijn doen via
        //double scoreSystemFlag = {{voorwaarden}} ? {{uitkomst als waar}} : {{uitkomst anders}}
        //zoek op: ternary equations
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
        //flco: stijltip, als de inhoud van u loops of ifkes uit 1 lijn code bestaan kan je de haakjes droppen
        //ook, ik snap niet super goed wat je hier aan het doen bent, 
        //je neemt de spelers, pakt de oude, en zwiert er nog een extra ronde bij met deze nieuwe/oude speler?
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
        //flco: nuttig naar verdere werking, bij aanmaken variabele is het nuttig om hier List ipv ArrayList te zetten
        //voor de simpele reden dat stel je game.getPreviousHits zou aanpassen om een LinkedList ipv ArrayList te gebruiken,
        //dan zou dit crashen en dit debuggen zou een absolute nachtmerrie zijn
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

    //flco: redundante comment
    public Bomb createBomb(Coordinate bombCoo) {
        return new Bomb(bombCoo);// create bomb 
    }

    //flco: redundante comments, ik vind deze code leesbaarder zonder deze
    //setScoring system commenten vlak boven een functie die setScoringSystem heet is kinda funny
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

    //flco: redundante comments
    //wat gebeurt er bij lege strings? eerder gewoon een vraagje, 
    //gebruik van getter is redundant
    //overbodige haakjes bij if
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
            //flco: comment zou je dit op 2 lijnen kunnen doen
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
        //flco: List ipv ArrayList
        ArrayList<Coordinate> overlappingCoordinates = new ArrayList<>();
        ArrayList<Ship> shipsToCheck = new ArrayList<>(Arrays.asList(ships));
        //flco: moeilijke for loop
        //mijn individuele issues staan in de loop zelf uitgeschreven
        //algemeen gezien, je probeert hier te veel te doen, zijnde 4 onafhankelijke checks
        //voor de coordinaten die overlappen is mijn advies om het niet per schip, maar in zijn geheel te bekijken
        //hoe ik het zou doen: verzamel alle coordinaten van alle schepen en zet deze in 1 lijst
        //loop door deze lijst, en verzamel je elementen in een Set (een Hashset bv)
        //hashsets kunnen niet tegen duplicates en zien of een uniek element hier in de lijst staat kan in constante tijd
        //indien duplicaat, sla deze op in een aparte lijst of set en geef deze terug
        //dit is volgens mij een O(N) oplossing en zou een stuk sneller moeten werken
        //CS uitleg: O(waarde in functie van N) is algoritmische complexiteit en gaat om het gedrag als je N naar oneindig duwt
        //O(1) is constant, maakt niet uit hoe groot je verzameling is, O(N) is decent, O(N²) of O(N³) gaat rap traag worden bij grotere sets
        for (Ship ship : ships) { // loop ships, flco: redundante comments, tis vrij duidelijk dat je loops begint en eindigt :p 
            shipsToCheck.remove(ship);
            //flco: bad practice, je functie zegt dat je naar overlapping in coordinaten kijkt, 
            //maar je doet hier ook een andere (ook belangrijke) controles, die vrij onverwacht en later moeilijk terug te vinden valt
            checkShipInBoardBounds(ship);
            checkShipHorizontalOrVertical(ship);
            checkShipSize(ship);
            ArrayList<Coordinate> cooList = new ArrayList<>();
            //flco: nachtmerrie van loop nesting, je zei dat je problemen had met schaalbaarheid, hier zit je probleem
            //je gaat ALLE locaties apart binnenhalen en deze nog eens zelf onafhankelijk opnieuw bezien:
            //in algoritmische notatie is dit O(M*N²) en dus toch voor verbetering vatbaar ;) 
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

    //flco: is minder zware if nesting mogelijk?
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

    //flco: je if en else zien er uit als duplicate code
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
            //flco: 3 keer de zelfde return, je kan dit gewoon op het einde zetten zonder en zo 3 lijnen code besparen ;)
            //laatste else is dan ook redundant
            return pList.toArray(new Player[0]);
        } else if (this.getGame().getPlayerArray()[0].getScore() == this.getGame().getPlayerArray()[1].getScore()) {
            pList.add(this.getGame().getPlayerArray()[1]);
            return pList.toArray(new Player[0]);
        } else {
            return pList.toArray(new Player[0]);
        }
    }
}