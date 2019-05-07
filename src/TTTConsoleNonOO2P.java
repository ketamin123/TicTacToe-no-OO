import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Tic-Tac-Toe: Two-player console, non-graphics, non-OO version. All
 * variables/methods are declared as static (belong to the class) in the non-OO
 * version.
 */
public class TTTConsoleNonOO2P {
	// Name-constants to represent the seeds and cell contents
	public static final int EMPTY = 0;
	public static final int CROSS = 1;
	public static final int NOUGHT = 2;

	// Name-constants to represent the various states of the game
	public static final int PLAYING = 0;
	public static final int DRAW = 1;
	public static final int CROSS_WON = 2;
	public static final int NOUGHT_WON = 3;

	// The game board and the game status
	public static final int ROWS = 3, COLS = 3; // number of rows and columns
	public static int[][] board = new int[ROWS][COLS]; // game board in 2D array
														// containing (EMPTY, CROSS, NOUGHT)
	public static int currentState; // the current state of the game
									// (PLAYING, DRAW, CROSS_WON, NOUGHT_WON)
	public static int currentPlayer; // the current player (CROSS or NOUGHT)
	public static int currntRow, currentCol; // current seed's row and column

	public static Scanner in = new Scanner(System.in); // the input Scanner

	/** The entry main method (the program starts here) */
	public static void main(String[] args) {
		// Initialize the game-board and current status
		initGame();
		// Play the game once
		do {
			playerMove(currentPlayer); // update currentRow and currentCol
			updateGame(currentPlayer, currntRow, currentCol); // update currentState
			printBoard();
			// Print message if game-over
			if (currentState == CROSS_WON) {
				System.out.println("'X' won! Bye!");
			} else if (currentState == NOUGHT_WON) {
				System.out.println("'O' won! Bye!");
			} else if (currentState == DRAW) {
				System.out.println("It's a Draw! Bye!");
			}
			// Switch player
			currentPlayer = (currentPlayer == CROSS) ? NOUGHT : CROSS;
		} while (currentState == PLAYING); // repeat if not game-over
	}

	/** Initialize the game-board contents and the current states */
	public static void initGame() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				board[row][col] = EMPTY; // all cells empty
			}
		}
		currentState = PLAYING; // ready to play
		currentPlayer = CROSS; // cross plays first
	}

	/**
	 * Player with the "theSeed" makes one move, with input validation. Update
	 * global variables "currentRow" and "currentCol".
	 */
	public static void playerMove(int theSeed) {
		boolean validInput = false; // for input validation
		do {
			if (theSeed == CROSS) {
				System.out.print("Player 'X', enter your move (row[1-3] column[1-3]): ");
			} else {
				System.out.print("Player 'O', enter your move (row[1-3] column[1-3]): ");
			}
			int row = in.nextInt() - 1; // array index starts at 0 instead of 1
			int col = in.nextInt() - 1;
			if (row >= 0 && row < ROWS && col >= 0 && col < COLS && board[row][col] == EMPTY) {
				currntRow = row;
				currentCol = col;
				board[currntRow][currentCol] = theSeed; // update game-board content
				validInput = true; // input okay, exit loop
			} else {
				System.out.println("This move at (" + (row + 1) + "," + (col + 1) + ") is not valid. Try again...");
			}
		} while (!validInput); // repeat until input is valid
	}

	/**
	 * Update the "currentState" after the player with "theSeed" has placed on
	 * (currentRow, currentCol).
	 */
	public static void updateGame(int theSeed, int currentRow, int currentCol) {
		if (hasWon(theSeed, currentRow, currentCol)) { // check if winning move
			currentState = (theSeed == CROSS) ? CROSS_WON : NOUGHT_WON;
		} else if (isDraw()) { // check for draw
			currentState = DRAW;
		}
		// Otherwise, no change to currentState (still PLAYING).
	}

	/** Return true if it is a draw (no more possible move) */
	public static boolean isDraw() {
		List<Integer[]> emptyCells = new ArrayList<>();
		Integer[] emptyCell = new Integer[2];

		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				if (board[row][col] == EMPTY) {
					if (emptyCells.size() <= 1) { // avoid unnecessary processing for possible win
						emptyCell[0] = row;
						emptyCell[1] = col;
						emptyCells.add(emptyCell); // add empty cell into list
						emptyCell = new Integer[2];
					} else {
						return false; // more than 2 cells are empty, not possible for draw, return false
					}
				}
			}
		}

		return isWinImpossible(emptyCells); // proceed to possible win test
	}

	/** Return true if it is impossible to win the game */
	public static boolean isWinImpossible(List<Integer[]> cells) {
		int tempPlayer = currentPlayer;
		int[][] tempBoard = new int[ROWS][COLS];
		int[] seedsSequence = new int[cells.size()];

		// copy board for possible win scenario testing
		for (int i = 0; i < ROWS; i++) {
			tempBoard[i] = Arrays.copyOf(board[i], board[i].length);
		}

		// calculating seeds' moves, technically versatile and proportional to board's size
		for (int i = 0; i < seedsSequence.length; i++) {
			tempPlayer = (tempPlayer == CROSS) ? NOUGHT : CROSS;
			seedsSequence[i] = tempPlayer;
		}

		// compare specific possible combinations of seeds based on the next moves on
		// temporary board
		for (int i = 0; i < seedsSequence.length; i++) {
			for (int j = 0; j < cells.size(); j++) {
				tempBoard[cells.get(j)[0]][cells.get(j)[1]] = seedsSequence[j];
			}
			
			for (int k = 0; k < cells.size(); k++) {
				if (hasWon(seedsSequence[k], cells.get(k)[0], cells.get(k)[1], tempBoard)) {
					return false; // possible win found, return false
				}
			}
			
			seedsSequence = shiftArrayElem(seedsSequence); // shift seeds combination
		}

		return true; // no possible win found, return true
	}

	/** Is to be used to shift first index array element to the next index */
	public static int[] shiftArrayElem(int[] arraySequence) {
		final int firstIndex = arraySequence[0];
		int[] arr = new int[arraySequence.length];
		System.arraycopy(arraySequence, 1, arr, 0, arraySequence.length - 1);
		arr[arr.length - 1] = firstIndex;

		return arr;
	}

	/**
	 * Return true if the player with "theSeed" has won after placing at
	 * (currentRow, currentCol)
	 */
	public static boolean hasWon(int theSeed, int currentRow, int currentCol) {
		return hasWon(theSeed, currentRow, currentCol, board);
	}

	/** overload of hasWon to include board parameter */
	public static boolean hasWon(int theSeed, int currentRow, int currentCol, int[][] board) {
		return (board[currentRow][0] == theSeed // 3-in-the-row
				&& board[currentRow][1] == theSeed && board[currentRow][2] == theSeed
				|| board[0][currentCol] == theSeed // 3-in-the-column
						&& board[1][currentCol] == theSeed && board[2][currentCol] == theSeed
				|| currentRow == currentCol // 3-in-the-diagonal
						&& board[0][0] == theSeed && board[1][1] == theSeed && board[2][2] == theSeed
				|| currentRow + currentCol == 2 // 3-in-the-opposite-diagonal
						&& board[0][2] == theSeed && board[1][1] == theSeed && board[2][0] == theSeed);
	}

	/** Print the game board */
	public static void printBoard() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				printCell(board[row][col]); // print each of the cells
				if (col != COLS - 1) {
					System.out.print("|"); // print vertical partition
				}
			}
			System.out.println();
			if (row != ROWS - 1) {
				System.out.println("-----------"); // print horizontal partition
			}
		}
		System.out.println();
	}

	/** Print a cell with the specified "content" */
	public static void printCell(int content) {
		switch (content) {
		case EMPTY:
			System.out.print("   ");
			break;
		case NOUGHT:
			System.out.print(" O ");
			break;
		case CROSS:
			System.out.print(" X ");
			break;
		}
	}

}