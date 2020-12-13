import java.util.ArrayList;

public class Calibrate {

    public static void main(String[] args) {
        double startTime = System.nanoTime();
        //Designate calibrate.txt as name of file we want to write
        String calFile = "calibration.txt";
        Boolean calibrating = Boolean.TRUE;


            double[] onePlyTimes = new double[26];
            ArrayList<Node> nodeList = new ArrayList<>();


            for(int b = 1; b <= 26; b++){
                int[][] arrangement = new int[b][b];
                InputInfo inputInfo = new InputInfo();
                inputInfo.initArrangement = arrangement;
                inputInfo.boardDimensions = b;
                inputInfo.nodeList = nodeList;
                CalibrationInfo calInfo = new CalibrationInfo(300.0, startTime, b);
                calInfo.maxPlys = 1;
                MiniMax miniMax = new MiniMax(inputInfo, calInfo);


                for(int row = 0; row < b; row++){
                    for (int column = 0; column < b; column++){
                        int fruit = (int) (Math.random() * (10));
                        arrangement[row][column] = fruit;
                        Location nodeLocation = new Location(row, column);
                        Node node = new Node(nodeLocation, arrangement, null);
                        nodeList.add(node);

                    }
                }

                double beginTime = System.nanoTime();

                miniMax.determineMove(calibrating);
                //In nanoseconds
                double timeForOnePly = System.nanoTime() - beginTime;
                calInfo.timeForOnePly = timeForOnePly;

                onePlyTimes[b-1] = timeForOnePly;

                nodeList.clear();

            }


            FileManipulation.createCalibration(onePlyTimes, calFile);


    }
}
