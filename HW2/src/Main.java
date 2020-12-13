

public class Main {
    public static void main(String args[]) {

//        MiniMaxRunner.continuousPlay(); // alternate turns until the game ends
      //  MiniMaxRunner.moveByMovePlay(); // wait for keyboard input between turns

        double startTime = System.nanoTime();

        //Designate input.txt as name of file we want to read
        String inputFile = "input.txt";
        String calFile = "calibration.txt";
        String outputFile = "output.txt";

        Boolean calibrating = Boolean.FALSE;

        // Collect input information from input.txt
        InputInfo inputInfo = FileManipulation.readInput(inputFile);

        double[] onePlyTimeArray = FileManipulation.readCalibration(calFile);

        CalibrationInfo calInfo = new CalibrationInfo(inputInfo.remainingTime, startTime, inputInfo.boardDimensions);
        calInfo.onePlyTimeArray = onePlyTimeArray;
        calInfo.timeForOnePly = onePlyTimeArray[inputInfo.boardDimensions-1];

        MiniMax miniMax = new MiniMax(inputInfo, calInfo);

        // Start it all
        OutputInfo outputInfo = miniMax.determineMove(calibrating);

        FileManipulation.createOutput(outputInfo, outputFile);
        double currTime = System.nanoTime() - startTime;
        System.out.println("Elapsed Time = " + currTime*Math.pow(10,-9) + "seconds");

    }

}