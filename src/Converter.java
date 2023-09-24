import java.util.ArrayList;
import java.util.Arrays;

public class Converter extends Method{
    /**
     * it convert a move from PGN notation to a Move object.
     * first it detect moves by their length and then by special notation that would separate their type of move
     * from others.
     * then it detect initial and final position of move and add the move to an Arraylist of Move objects.
     */
    public static ArrayList<Move> convertMoves(String[] movesArray){
        ArrayList<Move> moves = new ArrayList<>();
        resetSquares();
        for (int i = 0; i < movesArray.length; i++) {
            String s = movesArray[i];
            switch (s.length()) {
                case 2 -> {
                    int[] position = getPosition(s);
                    int[] initial = detectPawn(i, position, '0');
                    moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                }
                case 3 -> {
                    if (s.equals("O-O")) {
                        moves.add(new Move(i % 2 == 0, true, false, false, s));
                    } else {
                        String piece = String.valueOf(s.charAt(0));
                        int[] position = getPosition(s.charAt(1) + String.valueOf(s.charAt(2)));
                        int[] initial = detectPiece(i, piece, position, false);
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                    }
                }
                case 4 -> {
                    if (s.contains("=")) {
                        int[] position = getPosition(s.charAt(0) + String.valueOf(s.charAt(1)));
                        int[] initial = detectPawn(i, position, '0');
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(i, s.charAt(3)), getPiece(initial), Piece.NONE, false, s));
                    } else if (s.contains("x")) {
                        int[] position = getPosition(s.charAt(2) + String.valueOf(s.charAt(3)));
                        int[] initial;
                        if (Character.isUpperCase(s.charAt(0))) {
                            String piece = String.valueOf(s.charAt(0));
                            initial = detectPiece(i, piece, position, true);
                        } else {
                            initial = detectPawn(i, position, Character.toUpperCase(s.charAt(0)));
                        }
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), getPiece(position), Piece.NONE, false, s));
                    } else if (Character.isLowerCase(s.charAt(1))) {
                        String piece = String.valueOf(s.charAt(0));
                        String y = String.valueOf(s.charAt(1));
                        int[] position = getPosition(s.charAt(2) + String.valueOf(s.charAt(3)));
                        int[] initial = detectPieceGivenInformation(i, piece, null, y, position);
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                    } else {
                        String piece = String.valueOf(s.charAt(0));
                        String x = String.valueOf(s.charAt(1));
                        int[] position = getPosition(s.charAt(2) + String.valueOf(s.charAt(3)));
                        int[] initial = detectPieceGivenInformation(i, piece, x, null, position);
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                    }
                }
                case 5 -> {
                    if (s.equals("O-O-O")) {
                        moves.add(new Move(i % 2 == 0, false, true, false, s));
                    } else if (s.charAt(0) == 'Q' && Character.isLowerCase(s.charAt(1)) && Character.isLowerCase(s.charAt(3))
                            && Character.isDigit(s.charAt(2)) && Character.isDigit(s.charAt(4))){
                        int[] initial = getPosition(s.charAt(1) + String.valueOf(s.charAt(2)));
                        int[] position = getPosition(s.charAt(3) + String.valueOf(s.charAt(4)));
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                    }
                    else if (Character.isLowerCase(s.charAt(1))) {
                        String piece = String.valueOf(s.charAt(0));
                        String y = String.valueOf(s.charAt(1));
                        int[] position = getPosition(s.charAt(3) + String.valueOf(s.charAt(4)));
                        int[] initial = detectPieceGivenInformation(i, piece, null, y, position);
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), getPiece(position), Piece.NONE, false, s));
                    }
                    else {
                        String piece = String.valueOf(s.charAt(0));
                        String x = String.valueOf(s.charAt(1));
                        int[] position = getPosition(s.charAt(3) + String.valueOf(s.charAt(4)));
                        int[] initial = detectPieceGivenInformation(i, piece, x, null, position);
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), getPiece(position), Piece.NONE, false, s));
                    }
                }
                case 6 -> {
                    if(s.contains("=")) {
                        int[] position = getPosition(s.charAt(2) + String.valueOf(s.charAt(3)));
                        int[] initial = detectPawn(i, position, Character.toUpperCase(s.charAt(0)));
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(i, s.charAt(5)), getPiece(position), getPiece(initial), false, s));
                    }
                    else if(s.contains("x")){
                        int[] initial = getPosition(s.charAt(1) + String.valueOf(s.charAt(2)));
                        int[] position = getPosition(s.charAt(4) + String.valueOf(s.charAt(5)));
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), getPiece(position), Piece.NONE, false, s));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * it detect initial position of a Pawn movement.
     * @param i
     * index of move to determine white or black.
     * @param position
     * final position of move
     * @param s1
     * it's the first letter of move string when pawn's movement does include capturing.
     * "exd5" => s1 = 'e'
     * '0' => declare that movement does not include capturing. like "e5" => s1 = '0'
     */
    public static int[] detectPawn(int i, int[] position, char s1){
        if(i % 2 == 0) {
            if(s1 == '0') {
                if (getPiece(new int[]{position[0] - 1, position[1]}) == Piece.WHITE_PAWN)
                    return new int[]{position[0] - 1, position[1]};
                else
                    return new int[]{position[0] - 2, position[1]};
            } else
                return getPosition(s1+String.valueOf(position[0]));
        } else {
            if(s1 == '0') {
                if (getPiece(new int[]{position[0] + 1, position[1]}) == Piece.BLACK_PAWN)
                    return new int[]{position[0] + 1, position[1]};
                else
                    return new int[]{position[0] + 2, position[1]};
            } else
                return getPosition(s1+String.valueOf(position[0]+2));
        }
    }

    /**
     * it detect initial position of a piece movement. there is a different method for each type of piece.
     * for Rooks it looks for a suitable rook in squares that are vertical or horizontal to final position.
     * for Bishops it looks for a suitable bishop in squares that are diagonal to final position.
     * for Knights it looks for a suitable knight in 8 squares that a knight could be before moving to final position.
     * for Queen it looks for a suitable queen in squares that are vertical or horizontal or diagonal to final position.
     * for King it looks for a suitable king in 8 squares that a king could be before moving to final position.
     * @param i
     * index of move
     * @param piece
     * moving piece
     * @param position
     * final position of move
     * @param capture
     * indicate whether movement does include capture or not.
     */
    public static int[] detectPiece(int i, String piece, int[] position, boolean capture){
        switch (piece.toUpperCase()) {
            case "R":
                for (int j = 0; j < 8; j++) {
                    int[] initial1 = new int[]{j, position[1]};
                    int[] initial2 = new int[]{position[0], j};
                    if(i % 2 == 0) {
                        if (getPiece(initial1) == Piece.WHITE_ROOK) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.WHITE_ROOK) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    } else {
                        if (getPiece(initial1) == Piece.BLACK_ROOK) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.BLACK_ROOK) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    }
                }
                break;
            case "B":
                for (int j = -7; j < 8; j++) {
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] + j >= 0 && position[1] + j <= 7) {
                        int[] initial1 = new int[]{position[0] + j, position[1] + j};
                        if(i % 2 == 0) {
                            if (getPiece(initial1) == Piece.WHITE_BISHOP) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        } else {
                            if (getPiece(initial1) == Piece.BLACK_BISHOP) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        }
                    }
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] - j >= 0 && position[1] - j <= 7) {
                        int[] initial2 = new int[]{position[0] + j, position[1] - j};
                        if(i % 2 == 0) {
                            if (getPiece(initial2) == Piece.WHITE_BISHOP) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        } else {
                            if (getPiece(initial2) == Piece.BLACK_BISHOP) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        }
                    }
                }
                break;
            case "N":
                if (position[0] - 1 >= 0 && position[1] - 2 >= 0) {
                    int[] initial = new int[]{position[0] - 1, position[1] - 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] - 1 >= 0 && position[1] + 2 <= 7) {
                    int[] initial = new int[]{position[0] - 1, position[1] + 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] - 2 >= 0 && position[1] - 1 >= 0) {
                    int[] initial = new int[]{position[0] - 2, position[1] - 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] - 2 >= 0 && position[1] + 1 <= 7) {
                    int[] initial = new int[]{position[0] - 2, position[1] + 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 1 <= 7 && position[1] - 2 >= 0) {
                    int[] initial = new int[]{position[0] + 1, position[1] - 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 1 <= 7 && position[1] + 2 <= 7) {
                    int[] initial = new int[]{position[0] + 1, position[1] + 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 2 <= 7 && position[1] - 1 >= 0) {
                    int[] initial = new int[]{position[0] + 2, position[1] - 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 2 <= 7 && position[1] + 1 <= 7) {
                    int[] initial = new int[]{position[0] + 2, position[1] + 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                break;
            case "Q":
                for (int j = 0; j < 8; j++) {
                    int[] initial1 = new int[]{j, position[1]};
                    int[] initial2 = new int[]{position[0], j};
                    if(i % 2 == 0) {
                        if (getPiece(initial1) == Piece.WHITE_QUEEN) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.WHITE_QUEEN) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    } else {
                        if (getPiece(initial1) == Piece.BLACK_QUEEN) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.BLACK_QUEEN) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    }
                }
                for (int j = -7; j < 8; j++) {
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] + j >= 0 && position[1] + j <= 7) {
                        int[] initial1 = new int[]{position[0] + j, position[1] + j};
                        if(i % 2 == 0) {
                            if (getPiece(initial1) == Piece.WHITE_QUEEN) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        } else {
                            if (getPiece(initial1) == Piece.BLACK_QUEEN) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        }
                    }
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] - j >= 0 && position[1] - j <= 7) {
                        int[] initial2 = new int[]{position[0] + j, position[1] - j};
                        if(i % 2 == 0) {
                            if (getPiece(initial2) == Piece.WHITE_QUEEN) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        } else {
                            if (getPiece(initial2) == Piece.BLACK_QUEEN) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        }
                    }
                }
                break;
            default:
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        if(position[0] + j >= 0 && position[0] + j <= 7 &&
                                position[1] + k >= 0 && position[1] + k <= 7) {
                            int[] initial = new int[]{position[0] + j, position[1] + k};
                            if(i % 2 == 0) {
                                if (getPiece(initial) == Piece.WHITE_KING)
                                    return initial;
                            } else {
                                if (getPiece(initial) == Piece.BLACK_KING)
                                    return initial;
                            }
                        }
                    }
                }
                break;
        }
        return new int[]{};
    }

    /**
     * it detect initial position of a piece movement when some extra information provided in string move.
     * @param i
     * index of move
     * @param piece
     * moving piece
     * @param x
     * information about x axis.
     * @param y
     * information about y axis.
     * @param position
     * final position of move
     */
    public static int[] detectPieceGivenInformation(int i, String piece, String x, String y, int[] position){
        if(x != null) {
            int x1 = Integer.parseInt(x)-1;
            switch (piece.toUpperCase()) {
                case "R":
                    return new int[]{x1, position[1]};
                case "N":
                    if(position[1]-1 >= 0) {
                        int[] initial = new int[]{x1, position[1] - 1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[1]-2 >= 0) {
                        int[] initial = new int[]{x1, position[1] - 2};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[1]+1 <= 7) {
                        int[] initial = new int[]{x1, position[1] + 1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[1]+2 <= 7) {
                        int[] initial = new int[]{x1, position[1] + 2};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    break;
                case "B":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{x1, j};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_BISHOP)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_BISHOP)
                                return initial;
                        }
                    }
                    break;
                case "Q":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{x1, j};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_QUEEN)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_QUEEN)
                                return initial;
                        }
                    }
                    break;
            }
        } else {
            int y1;
            switch (y.toUpperCase()){
                case ("A") -> y1 = 0;
                case ("B") -> y1 = 1;
                case ("C") -> y1 = 2;
                case ("D") -> y1 = 3;
                case ("E") -> y1 = 4;
                case ("F") -> y1 = 5;
                case ("G") -> y1 = 6;
                default -> y1 = 7;
            }
            switch (piece.toUpperCase()) {
                case "R":
                    if(y1 != position[1])
                        return new int[]{position[0], y1};
                    else{
                        for(int k = 0; k <= 7; k++){
                            int[] initial = new int[]{k, y1};
                            if(i % 2 == 0) {
                                if(getPiece(initial) == Piece.WHITE_ROOK)
                                    return new int[]{k, y1};
                            } else {
                                if(getPiece(initial) == Piece.BLACK_ROOK)
                                    return new int[]{k, y1};
                            }
                        }
                    }
                    break;
                case "N":
                    if(position[0]-1 >= 0) {
                        int[] initial = new int[]{position[0] - 1, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[0]-2 >= 0) {
                        int[] initial = new int[]{position[0] - 2, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[0]+1 <= 7) {
                        int[] initial = new int[]{position[0] + 1, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[0]+2 <= 7) {
                        int[] initial = new int[]{position[0] + 2, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    break;
                case "B":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{j, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_BISHOP)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_BISHOP)
                                return initial;
                        }
                    }
                    break;
                case "Q":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{j, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_QUEEN)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_QUEEN)
                                return initial;
                        }
                    }
                    break;
            }
        }
        return new int[]{};
    }

    /**
     * if a piece stops a check, then we are not allow to moving it.
     * it detect king's position and check if the move happens is there any clear path to king vertically, horizontally
     * and diagonally or not. if there was then it would not allow it and return false.
     * @param i
     * index of move
     * @param from
     * initial square
     * @param to
     * final square
     * @param piece
     * moving piece
     */
    public static boolean isAllowedCheck(int i, Square from, Square to, Piece piece){
        boolean allowed = true;

        Square.piece.put(from, Piece.NONE);
        Piece pieceInTo = Square.piece.get(to);
        Square.piece.put(to, piece);

        for(Square s: Square.values()){
            Piece p = Square.piece.get(s);
            int[] positionOfKing = new int[2];
            if(i % 2 == 0) {
                for(Square s1: Square.values()){
                    if(Square.piece.get(s1) == Piece.WHITE_KING) {
                        positionOfKing = Square.position.get(s1);
                        break;
                    }
                }
                if (p == Piece.BLACK_ROOK){
                    int[] positionOfRook = Square.position.get(s);
                    if(positionOfRook[0] == positionOfKing[0] && positionOfRook[1] != positionOfKing[1]){
                        for(int k = Math.min(positionOfRook[1], positionOfKing[1])+1;
                            k < Math.max(positionOfRook[1], positionOfKing[1]); k++){
                            if(getPiece(new int[]{positionOfRook[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    else if(positionOfRook[0] != positionOfKing[0] && positionOfRook[1] == positionOfKing[1]){
                        for(int k = Math.min(positionOfRook[0], positionOfKing[0])+1;
                            k < Math.max(positionOfRook[0], positionOfKing[0]); k++){
                            if(getPiece(new int[]{k, positionOfRook[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if(!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                }
                else if (p == Piece.BLACK_BISHOP){
                    int[] positionOfBishop = Square.position.get(s);
                    if((positionOfBishop[0] + positionOfBishop[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfBishop[0]) == Math.abs(positionOfKing[1]-positionOfBishop[1])){
                        if(positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfBishop[0], positionOfKing[1]-positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] + j};
                                if(positionOfBishop[0] + j >=0 && positionOfBishop[0] + j <=7 &&
                                        positionOfBishop[1] + j >=0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfBishop[0], positionOfBishop[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] - j};
                                if(positionOfBishop[0] + j >=0 && positionOfBishop[0] + j <=7 &&
                                        positionOfBishop[1] - j >=0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfBishop[0]-positionOfKing[0], positionOfKing[1]-positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] + j};
                                if(positionOfBishop[0] - j >=0 && positionOfBishop[0] - j <=7 &&
                                        positionOfBishop[1] + j >=0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfBishop[0]-positionOfKing[0], positionOfBishop[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] - j};
                                if(positionOfBishop[0] - j >=0 && positionOfBishop[0] - j <=7 &&
                                        positionOfBishop[1] - j >=0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if(!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                }
                else if (p == Piece.BLACK_QUEEN) {
                    int[] positionOfQueen = Square.position.get(s);
                    if(positionOfQueen[0] == positionOfKing[0] && positionOfQueen[1] != positionOfKing[1]){
                        for(int k = Math.min(positionOfQueen[1], positionOfKing[1])+1;
                            k < Math.max(positionOfQueen[1], positionOfKing[1]); k++){
                            if(getPiece(new int[]{positionOfQueen[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    else if(positionOfQueen[0] != positionOfKing[0] && positionOfQueen[1] == positionOfKing[1]){
                        for(int k = Math.min(positionOfQueen[0], positionOfKing[0])+1;
                            k < Math.max(positionOfQueen[0], positionOfKing[0]); k++){
                            if(getPiece(new int[]{k, positionOfQueen[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if(!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                    if((positionOfQueen[0] + positionOfQueen[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfQueen[0]) == Math.abs(positionOfKing[1]-positionOfQueen[1])){
                        if(positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfQueen[0], positionOfKing[1]-positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] + j};
                                if(positionOfQueen[0] + j >=0 && positionOfQueen[0] + j <=7 &&
                                        positionOfQueen[1] + j >=0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfQueen[0], positionOfQueen[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] - j};
                                if(positionOfQueen[0] + j >=0 && positionOfQueen[0] + j <=7 &&
                                        positionOfQueen[1] - j >=0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfQueen[0]-positionOfKing[0], positionOfKing[1]-positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] + j};
                                if(positionOfQueen[0] - j >=0 && positionOfQueen[0] - j <=7 &&
                                        positionOfQueen[1] + j >=0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if (positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfQueen[0]-positionOfKing[0], positionOfQueen[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] - j};
                                if(positionOfQueen[0] - j >=0 && positionOfQueen[0] - j <=7 &&
                                        positionOfQueen[1] - j >=0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if(!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                }
            }
            else {
                for (Square s1 : Square.values()) {
                    if (Square.piece.get(s1) == Piece.BLACK_KING) {
                        positionOfKing = Square.position.get(s1);
                        break;
                    }
                }
                if (p == Piece.WHITE_ROOK) {
                    int[] positionOfRook = Square.position.get(s);
                    if (positionOfRook[0] == positionOfKing[0] && positionOfRook[1] != positionOfKing[1]) {
                        for (int k = Math.min(positionOfRook[1], positionOfKing[1]) + 1;
                             k < Math.max(positionOfRook[1], positionOfKing[1]); k++) {
                            if (getPiece(new int[]{positionOfRook[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    } else if (positionOfRook[0] != positionOfKing[0] && positionOfRook[1] == positionOfKing[1]) {
                        for (int k = Math.min(positionOfRook[0], positionOfKing[0]) + 1;
                             k < Math.max(positionOfRook[0], positionOfKing[0]); k++) {
                            if (getPiece(new int[]{k, positionOfRook[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if (!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                } else if (p == Piece.WHITE_BISHOP) {
                    int[] positionOfBishop = Square.position.get(s);
                    if ((positionOfBishop[0] + positionOfBishop[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfBishop[0]) == Math.abs(positionOfKing[1]-positionOfBishop[1])) {
                        if (positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfBishop[0], positionOfKing[1] - positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] + j};
                                if (positionOfBishop[0] + j >= 0 && positionOfBishop[0] + j <= 7 &&
                                        positionOfBishop[1] + j >= 0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfBishop[0], positionOfBishop[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] - j};
                                if (positionOfBishop[0] + j >= 0 && positionOfBishop[0] + j <= 7 &&
                                        positionOfBishop[1] - j >= 0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfBishop[0] - positionOfKing[0], positionOfKing[1] - positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] + j};
                                if (positionOfBishop[0] - j >= 0 && positionOfBishop[0] - j <= 7 &&
                                        positionOfBishop[1] + j >= 0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfBishop[0] - positionOfKing[0], positionOfBishop[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] - j};
                                if (positionOfBishop[0] - j >= 0 && positionOfBishop[0] - j <= 7 &&
                                        positionOfBishop[1] - j >= 0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if (!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                } else if (p == Piece.WHITE_QUEEN) {
                    int[] positionOfQueen = Square.position.get(s);
                    if (positionOfQueen[0] == positionOfKing[0] && positionOfQueen[1] != positionOfKing[1]) {
                        for (int k = Math.min(positionOfQueen[1], positionOfKing[1]) + 1;
                             k < Math.max(positionOfQueen[1], positionOfKing[1]); k++) {
                            if (getPiece(new int[]{positionOfQueen[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    } else if (positionOfQueen[0] != positionOfKing[0]&& positionOfQueen[1] == positionOfKing[1]) {
                        for (int k = Math.min(positionOfQueen[0], positionOfKing[0]) + 1;
                             k < Math.max(positionOfQueen[0], positionOfKing[0]); k++) {
                            if (getPiece(new int[]{k, positionOfQueen[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if (!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                    if ((positionOfQueen[0] + positionOfQueen[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfQueen[0]) == Math.abs(positionOfKing[1]-positionOfQueen[1])) {
                        if (positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfQueen[0], positionOfKing[1] - positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] + j};
                                if (positionOfQueen[0] + j >= 0 && positionOfQueen[0] + j <= 7 &&
                                        positionOfQueen[1] + j >= 0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfQueen[0], positionOfQueen[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] - j};
                                if (positionOfQueen[0] + j >= 0 && positionOfQueen[0] + j <= 7 &&
                                        positionOfQueen[1] - j >= 0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]) {
                            for (int j = 1; j < Math.max(positionOfQueen[0] - positionOfKing[0], positionOfKing[1] - positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] + j};
                                if (positionOfQueen[0] - j >= 0 && positionOfQueen[0] - j <= 7 &&
                                        positionOfQueen[1] + j >= 0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfQueen[0] - positionOfKing[0], positionOfQueen[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] - j};
                                if (positionOfQueen[0] - j >= 0 && positionOfQueen[0] - j <= 7 &&
                                        positionOfQueen[1] - j >= 0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if (!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                }
            }
        }

        Square.piece.put(from, piece);
        Square.piece.put(to, pieceInTo);
        return allowed;
    }

    /**
     * if a move cannot be done (because another piece is between initial and final position)
     * it would not allow it.
     * @param initial
     * initial position
     * @param position
     * final position
     * @param capture
     * indicate whether movement does include capture or not.
     * @param type
     * type of movement: "horizontally" - "vertically" - "diagonally"
     */
    public static boolean isAllowedMove(int[] initial, int[] position, boolean capture, String type){
        boolean allowed = true;
        if(type.equals("vertically")){
            for (int k = Math.min(initial[0], position[0]); k <= Math.max(initial[0], position[0]); k++) {
                if (k != initial[0]) {
                    if (getPiece(new int[]{k, position[1]}) != Piece.NONE) {
                        if(capture){
                            if(!Arrays.equals(new int[]{k, position[1]}, position)) {
                                allowed = false;
                                break;
                            }
                        } else {
                            allowed = false;
                            break;
                        }
                    }
                }
            }
        }
        else if(type.equals("horizontally")){
            for (int k = Math.min(initial[1], position[1]); k <= Math.max(initial[1], position[1]); k++) {
                if (k != initial[1]) {
                    if (getPiece(new int[]{position[0], k}) != Piece.NONE) {
                        if(capture){
                            if(!Arrays.equals(new int[]{position[0], k}, position)) {
                                allowed = false;
                                break;
                            }
                        } else {
                            allowed = false;
                            break;
                        }
                    }
                }
            }
        }
        else {
            if((initial[0] + initial[1]) % 2 == (position[0] + position[1]) % 2
                    && Math.abs(position[0]-initial[0]) == Math.abs(position[1]-initial[1])){
                if(position[0] > initial[0] && position[1] > initial[1]){
                    for (int j = 1; j < Math.max(position[0]-initial[0], position[1]-initial[1]); j++) {
                        int[] pos = new int[]{initial[0] + j, initial[1] + j};
                        if(initial[0] + j >=0 && initial[0] + j <=7 &&
                                initial[1] + j >=0 && initial[1] + j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else if(position[0] > initial[0] && position[1] < initial[1]){
                    for (int j = 1; j < Math.max(position[0]-initial[0], initial[1]-position[1]); j++) {
                        int[] pos = new int[]{initial[0] + j, initial[1] - j};
                        if(initial[0] + j >=0 && initial[0] + j <=7 &&
                                initial[1] - j >=0 && initial[1] - j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else if(position[0] < initial[0] && position[1] > initial[1]){
                    for (int j = 1; j < Math.max(initial[0]-position[0], position[1]-initial[1]); j++) {
                        int[] pos = new int[]{initial[0] - j, initial[1] + j};
                        if(initial[0] - j >=0 && initial[0] - j <=7 &&
                                initial[1] + j >=0 && initial[1] + j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else if(position[0] < initial[0] && position[1] < initial[1]){
                    for (int j = 1; j < Math.max(initial[0]-position[0], initial[1]-position[1]); j++) {
                        int[] pos = new int[]{initial[0] - j, initial[1] - j};
                        if(initial[0] - j >=0 && initial[0] - j <=7 &&
                                initial[1] - j >=0 && initial[1] - j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return allowed;
    }

}
