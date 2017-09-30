import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class PgnReader {
    public static String tagValue(String tagName, String game) {
        String[] parts = game.split("]");
        String tag = "[" + tagName;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith(tag, parts[i].indexOf('['))) {
                return parts[i].substring(parts[i].indexOf('"') + 1,
                    parts[i].lastIndexOf('"'));
            }
        }
        return "NOT GIVEN";
    }
    public static boolean checkPin(char[][] board, int row, int col, int turn,
            String move) {
        char king = 'K';
        if (turn == 1) {
            king = 'k';
        }
        int rowK = 0;
        int colK = 0;
        int rowChange = 0;
        int colChange = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == king) {
                    rowK = r;
                    colK = c;
                }
            }
        }
        if (row < rowK) {
            rowChange = -1;
        } else if (row > rowK) {
            rowChange = 1;
        }
        if (col < colK) {
            colChange = -1;
        } else if (col > colK) {
            colChange = 1;
        }
        int rowCheck = rowK + rowChange;
        int colCheck = colK + colChange;
        if (row - rowK == 0 || col - colK == 0) {
            while (rowCheck >= 0 && rowCheck < 8 && colCheck >= 0
                    && colCheck < 8 && (board[rowCheck][colCheck] == 'e'
                    || (rowCheck == row && colCheck == col))) {
                rowCheck += rowChange;
                colCheck += colChange;
            }
            if (rowCheck < 0 || rowCheck > 7 || colCheck < 0 || colCheck > 7) {
                return true;
            } else if (turn == 0 && (board[rowCheck][colCheck] == 'r'
                    || board[rowCheck][colCheck] == 'q')) {
                return false;
            } else if (turn == 1 && (board[rowCheck][colCheck] == 'R'
                    || board[rowCheck][colCheck] == 'Q')) {
                return false;
            }
            return true;
        } else if (row - rowK == col - colK || row - rowK == colK - col) {
            while (rowCheck >= 0 && rowCheck < 8 && colCheck >= 0
                    && colCheck < 8 && (board[rowCheck][colCheck] == 'e'
                    || (rowCheck == row && colCheck == col))) {
                rowCheck += rowChange;
                colCheck += colChange;
            }
            if (rowCheck < 0 || rowCheck > 7 || colCheck < 0 || colCheck > 7) {
                return true;
            } else if (turn == 0 && (board[rowCheck][colCheck] == 'b'
                    || board[rowCheck][colCheck] == 'q')) {
                return false;
            } else if (turn == 1 && (board[rowCheck][colCheck] == 'B'
                    || board[rowCheck][colCheck] == 'Q')) {
                return false;
            }
            return true;
        } else {
            return true;
        }
    }
    public static void findSquare(char[][] board, String move, int turn,
            char piece, int[] rowAdd, int[] colAdd, boolean range) {
        String finalPos = move.substring(move.length() - 2);
        String disam = move.substring(1, move.length() - 2);
        boolean needDisam = !disam.isEmpty();
        int row = 8 - Character.getNumericValue(finalPos.charAt(1));
        int col = Character.getNumericValue(finalPos.charAt(0)) - 10;
        board[row][col] = piece;
        if (disam.length() == 2) {
            board[8 - Character.getNumericValue(disam.charAt(1))]
            [Character.getNumericValue(disam.charAt(0)) - 10] = 'e';
        } else {
            int rowD = -1;
            int colD = -1;
            if (needDisam) {
                char disamC = disam.charAt(0);
                if (Character.isDigit(disamC)) {
                    rowD = 8 - Character.getNumericValue(disamC);
                } else {
                    colD = Character.getNumericValue(disamC) - 10;
                }
            }
            int rowCheck = row;
            int colCheck = col;
            int index = 0;
            boolean found = false;
            while (!found) {
                rowCheck += rowAdd[index];
                colCheck += colAdd[index];
                while (range && rowCheck >= 0 && rowCheck < 8 && colCheck >= 0
                        && colCheck < 8 && board[rowCheck][colCheck] == 'e') {
                    rowCheck += rowAdd[index];
                    colCheck += colAdd[index];
                }
                if (rowCheck >= 0 && rowCheck < 8 && colCheck >= 0
                        && colCheck < 8 && board[rowCheck][colCheck] == piece
                        && checkPin(board, rowCheck, colCheck, turn, move)
                        && (rowD == -1 || rowCheck == rowD)
                        && (colD == -1 || colCheck == colD)) {
                    found = true;
                    board[rowCheck][colCheck] = 'e';
                }
                rowCheck = row;
                colCheck = col;
                index++;
            }
        }
    }
    public static void moveRook(char[][] board, String move, int turn) {
        char piece = 'R';
        if (turn == 1) {
            piece = 'r';
        }
        int[] rowAdd = {0, 1, 0, -1};
        int[] colAdd = {1, 0, -1, 0};
        findSquare(board, move, turn, piece, rowAdd, colAdd, true);
    }
    public static void moveBishop(char[][] board, String move, int turn) {
        char piece = 'B';
        if (turn == 1) {
            piece = 'b';
        }
        int[] rowAdd = {1, -1, 1, -1};
        int[] colAdd = {-1, 1, 1, -1};
        findSquare(board, move, turn, piece, rowAdd, colAdd, true);
    }
    public static void moveKnight(char[][] board, String move, int turn) {
        char piece = 'N';
        if (turn == 1) {
            piece = 'n';
        }
        int[] rowAdd = {2, 2, -2, -2, 1, 1, -1, -1};
        int[] colAdd = {1, -1, 1, -1, 2, -2, 2, -2};
        findSquare(board, move, turn, piece, rowAdd, colAdd, false);
    }
    public static void moveQueen(char[][] board, String move, int turn) {
        char piece = 'Q';
        if (turn == 1) {
            piece = 'q';
        }
        int[] rowAdd = {0, 1, 0, -1, 1, -1, 1, -1};
        int[] colAdd = {1, 0, -1, 0, -1, 1, 1, -1};
        findSquare(board, move, turn, piece, rowAdd, colAdd, true);
    }
    public static void moveKing(char[][] board, String move, int turn) {
        String finalPos = move.substring(move.length() - 2);
        int row = 8 - Character.getNumericValue(finalPos.charAt(1));
        int col = Character.getNumericValue(finalPos.charAt(0)) - 10;
        char piece = 'K';
        if (turn == 1) {
            piece = 'k';
        }
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                if (row + r >= 0 && row + r < 8 && col + c >= 0 && col + c < 8
                        && board[row + r][col + c] == piece) {
                    board[row + r][col + c] = 'e';
                    board[row][col] = piece;
                }
            }
        }
    }
    public static void moveCastle(char[][] board, String move, int turn) {
        if (turn == 0 && move.equals("O-O")) {
            board[7][4] = 'e';
            board[7][7] = 'e';
            board[7][6] = 'K';
            board[7][5] = 'R';
        } else if (turn == 1 && move.equals("O-O")) {
            board[0][4] = 'e';
            board[0][7] = 'e';
            board[0][6] = 'k';
            board[0][5] = 'r';
        } else if (turn == 0 && move.equals("O-O-O")) {
            board[7][4] = 'e';
            board[7][0] = 'e';
            board[7][2] = 'K';
            board[7][3] = 'R';
        } else if (turn == 1 && move.equals("O-O-O")) {
            board[0][4] = 'e';
            board[0][0] = 'e';
            board[0][2] = 'k';
            board[0][3] = 'r';
        }
    }
    public static void movePawn(char[][] board, String move, int turn) {
        String finalPos;
        char promote = 'P';
        if (!Character.isDigit(move.charAt(move.length() - 1))) {
            promote = move.charAt(move.length() - 1);
            finalPos = move.substring(move.length() - 3, move.length() - 1);
            move = move.substring(0, move.length() - 1);
        } else {
            finalPos = move.substring(move.length() - 2);
        }
        int row = 8 - Character.getNumericValue(finalPos.charAt(1));
        int col = Character.getNumericValue(finalPos.charAt(0)) - 10;
        int dir = 1;
        char pawn = 'P';
        if (turn == 1) {
            promote = Character.toLowerCase(promote);
            dir = -1;
            pawn = 'p';
        }
        if (move.length() == 2 && board[row + dir][col] == pawn) {
            board[row + dir][col] = 'e';
        } else if ((move.length() == 2 && board[row + 2 * dir][col] == pawn)) {
            board[row + 2 * dir][col] = 'e';
        } else if (move.length() == 3) {
            int startCol = Character.getNumericValue(move.charAt(0)) - 10;
            if (board[row][col] == 'e') {
                board[row + dir][col] = 'e';
            }
            board[row + dir][startCol] = 'e';
        }
        board[row][col] = promote;
    }
    public static String printfen(char[][] board) {
        String fen = "";
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 'e') {
                    count++;
                    if (j == 7) {
                        fen += count;
                        count = 0;
                    }
                } else if (count != 0) {
                    fen += count;
                    fen += board[i][j];
                    count = 0;
                } else {
                    fen += board[i][j];
                }
            }
            if (i != 7) {
                fen += "/";
            }
        }
        return fen;
    }
    public static void moveBoard(char[][] board, String move, int turn) {
        char piece = move.charAt(0);
        if (piece == 'R') {
            moveRook(board, move, turn);
        } else if (piece == 'B') {
            moveBishop(board, move, turn);
        } else if (piece == 'N') {
            moveKnight(board, move, turn);
        } else if (piece == 'Q') {
            moveQueen(board, move, turn);
        } else if (piece == 'K') {
            moveKing(board, move, turn);
        } else if (piece == 'O') {
            moveCastle(board, move, turn);
        } else if (piece == 'a' || piece == 'b' || piece == 'c'
            || piece == 'd' || piece == 'e' || piece == 'f'
            || piece == 'g' || piece == 'h') {
            movePawn(board, move, turn);
        }
    }
    public static String finalPosition(String game) {
        String[] parts = game.split("]");
        String premovelist = parts[parts.length - 1].replaceAll("\n", " ");
        premovelist = premovelist.replaceAll("\\!", "");
        premovelist = premovelist.replaceAll("\\?", "");
        premovelist = premovelist.replaceAll("\\+", "");
        premovelist = premovelist.replaceAll("\\#", "");
        premovelist = premovelist.replaceAll("\\=", "");
        premovelist = premovelist.replaceAll("  ", " ");
        premovelist = premovelist.replaceAll("x", "");
        String[] movelist = premovelist.split("\\.");
        for (int i = 0; i < movelist.length; i++) {
            movelist[i] = movelist[i].trim();
        }
        movelist[movelist.length - 1] += "   ";
        String[][] moves = new String[movelist.length - 1][2];
        int space1 = 0;
        int space2 = 0;
        for (int i = 1; i < movelist.length; i++) {
            space1 = movelist[i].indexOf(" ");
            moves[i - 1][0] = movelist[i].substring(0, space1);
            space2 = movelist[i].indexOf(" ", space1 + 1);
            moves[i - 1][1] = movelist[i].substring(space1 + 1, space2);
        }
        char[][] board =
        {
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
            {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
            {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
            {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        int movecount = 0;
        for (int i = 0; i < moves.length; i++) {
            if (!moves[i][0].isEmpty()) {
                moveBoard(board, moves[i][0], 0);
            }
            if (!moves[i][1].isEmpty()) {
                moveBoard(board, moves[i][1], 1);
            }
        }
        return printfen(board);
    }
    public static String fileContent(String path) {
        Path file = Paths.get(path);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            System.exit(1);
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        String game = fileContent(args[0]);
        System.out.format("Event: %s%n", tagValue("Event", game));
        System.out.format("Site: %s%n", tagValue("Site", game));
        System.out.format("Date: %s%n", tagValue("Date", game));
        System.out.format("Round: %s%n", tagValue("Round", game));
        System.out.format("White: %s%n", tagValue("White", game));
        System.out.format("Black: %s%n", tagValue("Black", game));
        System.out.format("Result: %s%n", tagValue("Result", game));
        System.out.println("Final Position:");
        System.out.println(finalPosition(game));
    }
}