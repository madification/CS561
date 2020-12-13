import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MiniMaxRunner {

    /**
     * Continuously run the game: read file, run minimax, choose next move, write file, switch players... repeat until
     * the game is over. Print out the score and stuff
     */
    public static void continuousPlay() {
        run(false);
    }

    /**
     * Run the game move by move: read file, run minimax, choose next move, write file, switch players, wait for
     * user to hit enter... repeat until the game is over. Print out the score and stuff
     */
    public static void moveByMovePlay() {
        run(true);
    }

    private static void run(boolean MoveByMove) {
        //Designate input.txt as name of file we want to read
        final String inputFile = "input.txt";
        final String calFile = "calibration.txt";
        final String outputFile = "output.txt";

        boolean myPlay = true;
        double myScore = 0;
        double theirScore = 0;
        double totalElapsedSeconds = 0;
        int roundNumber = 0;

        while (true) {

            System.out.print("\n\nRound number: " + roundNumber++ +"\n");
            // our start time
            long startTimeNanos = System.nanoTime();

            // collect up the input stuff
            InputInfo inputInfo = FileManipulation.readInput(inputFile);
            CalibrationInfo calInfo = FileManipulation.readCalibration(calFile, inputInfo);
            calInfo.startTime = startTimeNanos;

            // run minimax
            MiniMax miniMax = new MiniMax(inputInfo, calInfo);

            // figure out the elapsed time
            double roundTimeSeconds = nanosToSeconds(System.nanoTime() - startTimeNanos);
            totalElapsedSeconds += roundTimeSeconds;

            // collect up the output stuffs
            OutputInfo outputInfo = miniMax.determineMove();
            createOutput(outputInfo, inputFile, inputInfo.boardDimensions, inputInfo.numFruitTypes, inputInfo.remainingTime - roundTimeSeconds);
            FileManipulation.createOutput(outputInfo, outputFile);


            System.out.printf("Elapsed Time for round: %.5f seconds\n", roundTimeSeconds);
            System.out.println(myPlay ? "My Turn:" : "Their turn:");
            printBoard(outputInfo.outputArrangement);

            // save off scores and switch with the opponent
            if (myPlay) {
                myPlay = false;
                myScore += miniMax.getScore();
            }
            else {
                myPlay = true;
                theirScore += miniMax.getScore();
            }

            // print some stats when finished
            if (done(outputInfo.outputArrangement)) {
                System.out.printf("Total time: %.5f\n", totalElapsedSeconds);
                System.out.println("My score: " + myScore);
                System.out.println("Their score: " + theirScore);
                System.exit(1);
            }

            if(MoveByMove) {
                try {
                    System.out.println("Press enter to continue");
                    System.in.read();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }

            }

        }
    }

    private static double nanosToSeconds(double nanoTime) {
        return nanoTime * Math.pow(10, -9);
    }

    private static boolean done(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != -1) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void printBoard(int[][] board)
    {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(board[i][j] == -1 ? "*" : board[i][j]);
            }
            System.out.println();
        }
    }

    private static void createOutput(OutputInfo outputInfo, String fileName, int boardsize, int numFruit, double timeRemaining) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.write(Integer.toString(boardsize));
            writer.newLine();
            writer.write(Integer.toString(numFruit));
            writer.newLine();
            writer.write(Double.toString(timeRemaining));
            writer.newLine();
            for (int row = 0; row < outputInfo.boardDimensions; row++) {
                for (int column = 0; column < outputInfo.boardDimensions; column++) {
                    if (outputInfo.outputArrangement[row][column] == -1) {
                        // cell was empty, convert back to * for HW conventions
                        writer.write("*");
                    }
                    else {
                        // Place in file
                        writer.write(Integer.toString(outputInfo.outputArrangement[row][column]));
                    }
                }
                writer.newLine();
            }

            writer.close();


        } catch (IOException e) {
            System.out.println(e.toString() + "Error in creation of output file.");
        }
    }
}
