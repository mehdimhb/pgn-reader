import java.util.*;
import java.util.stream.Collectors;

public class GameStat {
    private PGN pgn;

    GameStat(PGN pgn){
        this.setPgn(pgn);
    }

    GameStat(){

    }

    public PGN getPgn() {
        return pgn;
    }

    public void setPgn(PGN pgn) {
        this.pgn = pgn;
    }

    /**
     * it takes the name of a player and returns the games of that player.
     */
    ArrayList<Game> getGames(String playerName){
        ArrayList<Game> result = new ArrayList<>();
        for(Game g: getPgn().getGames()){
            if(g.getWhitePlayer().equals(playerName) || g.getBlackPlayer().equals(playerName))
                result.add(g);
        }
        return result;
    }

    /**
     * it takes the name of a player and returns the ratio of the number of games
     * that the player won and the ones that lost.
     */
    public double getWinLoseRatio(String playerName){
        ArrayList<Game> games = getGames(playerName);
        double win = 0, lose = 0;
        for(Game g: games){
            if(g.getWhitePlayer().equals(playerName)){
                if(g.getResult().length() == 3) {
                    if (g.getResult().charAt(0) == '1')
                        win++;
                    else
                        lose++;
                }
            }
            else if(g.getBlackPlayer().equals(playerName)){
                if(g.getResult().length() == 3) {
                    if (g.getResult().charAt(2) == '1')
                        win++;
                    else
                        lose++;
                }
            }
        }
        if(lose != 0)
            return win/lose;
        else
            return win;
    }

    /**
     * returns n moves of the most used moves in all games of the file. return an arraylist of Move object.
     * if general is true, it return most used moves excluding capturing act. Bf5 and Bxf5 would be the same.
     * if general is false. it return most used moves including capturing act. Bf5 and Bxf5 are different now.
     */
    public ArrayList<Move> mostUsedMoves(int n, boolean general){
        ArrayList<Move> allMoves = new ArrayList<>();
        for(Game g: getPgn().getGames()){
            allMoves.addAll(g.board.getMoves());
        }
        if(general)
            return customSortV2(allMoves, n);
        return customSort(allMoves, n);
    }

    /**
     * returns n moves of the most used moves in all games of the file.
     * return an arraylist of String representation of move.
     * because this method does not require conversion of string move to Move object, it runs much faster.
     * so this method will be used in toString.
     */
    public ArrayList<String> mostUsedMovesString(int n){
        ArrayList<String> allMoves = new ArrayList<>();
        for(Game g: getPgn().getGames()){
            allMoves.addAll(Arrays.asList(g.getStringMovesArray()));
        }
        return customSortV3(allMoves, n);
    }

    /**
     * return number of the game that begins with that opening.
     */
    public int numberOfGamesWithOpening(String opening){
        int count = 0;
        for(Game g: getPgn().getGames()){
            if(g.getOpening().equals(opening))
                count++;
        }
        return count;
    }

    /**
     * return the percent of winning games with certain opening.
     */
    public double openingWinRate(String opening){
        double win = 0, count = 0;
        for(Game g: getPgn().getGames()){
            if(g.getOpening().equals(opening)) {
                count++;
                if(g.getResult().length() == 3 && g.getResult().charAt(0) == '1')
                    win++;
            }
        }
        return (win/count)*100;
    }

    /**
     * returns mean number of the moves that take place before first capture in all games.
     */
    public double expectedMovesBeforeFirstCapture(){
        double sum = 0, count = 0;
        for(Game g: getPgn().getGames()){
            for(int i = 0; i < g.getStringMovesArray().length; i++){
                if(g.getStringMovesArray()[i].contains("x")) {
                    sum += i;
                    count++;
                    break;
                }
            }
        }
        return sum/count;
    }

    /**
     * returns the percent of winning games that begin with certain move (first white move or first black move).
     * it takes an Move object.
     */
    public double getWinRate(Move m){
        double win = 0, count = 0;
        for(Game g: getPgn().getGames()) {
            if(g.board.getMoves().size() > 2) {
                if (m.equals(g.board.getMoves().get(0))) {
                    count++;
                    if (g.getResult().length() == 3 && g.getResult().charAt(0) == '1')
                        win++;
                } else if (m.equals(g.board.getMoves().get(1))) {
                    count++;
                    if (g.getResult().length() == 3 && g.getResult().charAt(2) == '1')
                        win++;
                }
            }
        }
        if(count != 0)
            return (win/count)*100;
        else
            return 0;
    }

    /**
     * returns the percent of winning games that begin with certain move (first white move or first black move).
     * it takes an String representation of move.
     */
    public double getWinRateString(String m){
        double win = 0, count = 0;
        for(Game g: getPgn().getGames()) {
            if(g.getStringMovesArray().length > 2) {
                if (m.equals(g.getStringMovesArray()[0])) {
                    count++;
                    if (g.getResult().length() == 3 && g.getResult().charAt(0) == '1')
                        win++;
                } else if (m.equals(g.getStringMovesArray()[1])) {
                    count++;
                    if (g.getResult().length() == 3 && g.getResult().charAt(2) == '1')
                        win++;
                }
            }
        }
        if(count != 0)
            return (win/count)*100;
        else
            return 0;
    }

    /**
     * given some moves and predict n most probable moves for next move.
     * it takes an arraylist of Move object
     */
    public ArrayList<Move> nextMoves(ArrayList<Move> moves, int n){
        ArrayList<Move> expected = new ArrayList<>();
        Collections.reverse(moves);
        for(Game g: getPgn().getGames()) {
            ArrayList<Move> gMove = g.board.getMoves();
            for (int i = 0; i < gMove.size() - 1; i++) {
                if (gMove.get(i).equals(moves.get(0))) {
                    expected.add(gMove.get(i + 1));
                    if (i > 0 && i < moves.size()) {
                        for (int j = 1; j < Math.min(n, i + 1); j++) {
                            if (gMove.get(i - j).equals(moves.get(j))) {
                                expected.add(gMove.get(i + 1));
                            }
                            else
                                break;
                        }
                    }
                }
            }
        }
        return customSort(expected, n);
    }

    /**
     * given some moves and predict n most probable moves for next move.
     * it takes an arraylist of String representation of move.
     */
    public ArrayList<String> nextMovesString(ArrayList<String> moves, int n){
        Collections.reverse(moves);
        ArrayList<String> expected = new ArrayList<>();
        for(Game g: getPgn().getGames()) {
            String[] gMove = g.getStringMovesArray();
            for (int i = 0; i < gMove.length - 1; i++) {
                if (gMove[i].equals(moves.get(0))) {
                    expected.add(gMove[i + 1]);
                    if (i > 0 && i < moves.size()) {
                        for (int j = 1; j < Math.min(n, i + 1); j++) {
                            if (gMove[i - j].equals(moves.get(j))) {
                                expected.add(gMove[i + 1]);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return customSortV3(expected, n);
    }

    /**
     * returns next probable move in a game given all the moves of that game to current move.
     * it require require conversion of string move to Move object.
     */
    public ArrayList<Move> nextMovesInGame(int indexOfGame, int indexOfMove, int n) {
        if(indexOfMove == -1){
            ArrayList<Move> allFirstMoves = new ArrayList<>();
            for(Game g: getPgn().getGames()){
                if(g.board.getMoves().size() > 0)
                    allFirstMoves.add(g.board.getMoves().get(0));
            }
            return customSort(allFirstMoves, n);
        } else {
            Game game = pgn.getGames().get(indexOfGame);
            ArrayList<Move> moves = new ArrayList<>();
            for (int i = 0; i <= indexOfMove; i++)
                moves.add(game.board.getMoves().get(i));
            return nextMoves(moves, n);
        }
    }

    /**
     * returns next probable move in a game given all the moves of that game to current move.
     * it does not require require conversion of string move to Move object. so it runs much faster.
     * so this would be use in toString.
     */
    public ArrayList<String> nextMovesInGameString(int indexOfGame, int indexOfMove, int n) {
        if(indexOfMove == -1){
            ArrayList<String> allFirstMoves = new ArrayList<>();
            for(Game g: getPgn().getGames()){
                if(g.getStringMovesArray().length > 0)
                    allFirstMoves.add(g.getStringMovesArray()[0]);
            }
            return customSortV3(allFirstMoves, n);
        } else {
            Game game = pgn.getGames().get(indexOfGame);
            ArrayList<String> moves = new ArrayList<>();
            for (int i = 0; i <= indexOfMove; i++)
                moves.add(game.getStringMovesArray()[i]);
            return nextMovesString(moves, n);
        }
    }

    /**
     * it sort an arraylist of Move Object based on frequency and return first n move. comparing moves does "include" capturing act.
     */
    private ArrayList<Move> customSort(ArrayList<Move> moves, int n) {
        Map<String, Data> hm = new HashMap<>();
        for (int i = 0; i < moves.size(); i++) {
            hm.putIfAbsent(moves.get(i).toString(), new Data(moves.get(i), 0, i));
            hm.get(moves.get(i).toString()).count++;
        }
        List<Data> values = hm.values().stream()
                .sorted()
                .collect(Collectors.toList());

        ArrayList<Move> result = new ArrayList<>();
        for (Data data: values) {
            if(n > 0)
                result.add(data.move);
            else
                break;
            n--;
        }
        return result;
    }

    /**
     * it sort an arraylist of Move Object based on frequency and return first n move. comparing moves does "exclude" capturing act.
     */
    private ArrayList<Move> customSortV2(ArrayList<Move> moves, int n) {
        Map<String, Data> hm = new HashMap<>();
        for (int i = 0; i < moves.size(); i++) {
            hm.putIfAbsent(moves.get(i).toStringV2(), new Data(moves.get(i), 0, i));
            hm.get(moves.get(i).toStringV2()).count++;
        }
        List<Data> values = hm.values().stream()
                .sorted()
                .collect(Collectors.toList());

        ArrayList<Move> result = new ArrayList<>();
        for (Data data: values) {
            if(n > 0)
                result.add(data.move);
            else
                break;
            n--;
        }
        return result;
    }

    /**
     * it sort an arraylist of move String based on frequency and return first n move. comparing moves does "exclude" capturing act.
     */
    private ArrayList<String> customSortV3(ArrayList<String> moves, int n) {
        Map<String, DataV2> hm = new HashMap<>();
        for (int i = 0; i < moves.size(); i++) {
            hm.putIfAbsent(moves.get(i), new DataV2(moves.get(i), 0, i));
            hm.get(moves.get(i)).count++;
        }
        List<DataV2> values = hm.values().stream()
                .sorted()
                .collect(Collectors.toList());
        ArrayList<String> result = new ArrayList<>();
        for (DataV2 dataV2: values) {
            if(n > 0)
                result.add(dataV2.move);
            else
                break;
            n--;
        }
        return result;
    }

    public String toString(int indexOfGame, int indexOfMove, boolean moveStat){
        StringBuilder result = new StringBuilder();
        Game game = pgn.getGames().get(indexOfGame);
        String whitePlayer = game.getWhitePlayer();
        String blackPlayer = game.getBlackPlayer();
        String opening = game.getOpening();
        ArrayList<String> moves;

        if(whitePlayer != null)
            result.append(String.format("No. of %s's Games:  %d\n", whitePlayer, getGames(whitePlayer).size()));
        if(blackPlayer != null)
            result.append(String.format("No. of %s's Games:  %d\n", blackPlayer, getGames(blackPlayer).size()));
        if(whitePlayer != null)
            result.append(String.format("Win to Lose Ratio of %s:  %.2f\n", whitePlayer, getWinLoseRatio(whitePlayer)));
        if(blackPlayer != null)
            result.append(String.format("Win to Lose Ratio of %s:  %.2f\n", blackPlayer, getWinLoseRatio(blackPlayer)));
        if(opening != null) {
            result.append(String.format("No. of Games with %s Opening:  %d\n", opening, numberOfGamesWithOpening(opening)));
            result.append(String.format("Rate of Winning with %s:  %.2f%%\n", opening, openingWinRate(opening)));
        }
        if(game.getResult() != null) {
            result.append(String.format("Rate of White Winning with this First Move:  %.2f%%\n", getWinRateString(game.getStringMovesArray()[0])));
            result.append(String.format("Rate of Black Winning with this First Move:  %.2f%%\n", getWinRateString(game.getStringMovesArray()[1])));
        }
        result.append(String.format("Expected moves before first capture:  %.2f\n", expectedMovesBeforeFirstCapture()));

        if(moveStat) {
            result.append("Top 5 Most Used Moves in All Games:\n");
            moves = mostUsedMovesString(5);
            for (String m : moves)
                result.append(m).append("\n");

            if(indexOfMove < game.getStringMovesArray().length-1) {
                result.append("Top 5 Most Probable Next Move:\n");
                moves = nextMovesInGameString(indexOfGame, indexOfMove, 5);
                for (String m : moves)
                    result.append(m).append("\n");
            }
        }

        if(result.toString().contains("\n")) {
            int lastNextLine = result.lastIndexOf("\n");
            return result.delete(lastNextLine, lastNextLine + 2).toString();
        } else {
            return result.toString();
        }
    }

    private static class Data implements Comparable<Data> {
        Move move;
        int count, index;

        public Data(Move move, int count, int index) {
            this.move = move;
            this.count = count;
            this.index = index;
        }

        @Override
        public int compareTo(Data obj) {
            if (this.count != obj.count) {
                return obj.count - this.count;
            }
            return this.index - obj.index;
        }
    }

    private static class DataV2 implements Comparable<DataV2> {
        String move;
        int count, index;

        public DataV2(String move, int count, int index) {
            this.move = move;
            this.count = count;
            this.index = index;
        }

        @Override
        public int compareTo(DataV2 obj) {
            if (this.count != obj.count) {
                return obj.count - this.count;
            }
            return this.index - obj.index;
        }
    }

}
