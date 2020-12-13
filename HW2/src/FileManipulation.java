import java.io.*;

public class FileManipulation {

    private FileManipulation() {

    }

    public static InputInfo readInput(String fileName) {
        InputInfo inputInfo = new InputInfo();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            inputInfo.boardDimensions = Integer.parseInt(reader.readLine());
            inputInfo.numFruitTypes = Integer.parseInt(reader.readLine());
            inputInfo.remainingTime = Float.parseFloat(reader.readLine());

            // Initialize initial board arrangement to size provided/read
            inputInfo.initArrangement = new int[inputInfo.boardDimensions][inputInfo.boardDimensions];

            for (int row = 0; row < inputInfo.boardDimensions; row++) {
                String[] lineRead = reader.readLine().split("");

                for (int column = 0; column < inputInfo.boardDimensions; column++) {
                    if (lineRead[column].equals("*")) {
                        // Cell was empty; increment count and convert to usable -1
                        inputInfo.emptyCells++;
                        inputInfo.initArrangement[row][column] = -1;
                    }
                    else {
                        inputInfo.initArrangement[row][column] = Integer.parseInt(lineRead[column]);
                    }
                }
            }

            // Create node list from our new arrangement
            for (int row = 0; row < inputInfo.boardDimensions; row++) {
                for (int column = 0; column < inputInfo.boardDimensions; column++) {
                    if (inputInfo.initArrangement[row][column] != -1) {
                        Location nodeLocation = new Location(row, column);
                        Node node = new Node(nodeLocation, inputInfo.initArrangement, null);
                        inputInfo.nodeList.add(node);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println(e.toString() + "Could not find input.txt file.");
        }

        return inputInfo;
    }

    public static double[] readCalibration(String fileName) {
        double[] timesArray = new double[26];

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            for(int i = 0; i < 26; i++){
                timesArray[i] = Double.parseDouble(reader.readLine());
            }

            reader.close();

        } catch (IOException e) {
            System.out.println(e.toString() + "Could not find calibration.txt file.");
        }

        return timesArray;
    }

    public static void createCalibration(double[] timesArray, String fileName) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            for(int i = 0; i < timesArray.length; i++) {
                double onePlyTime = timesArray[i];

                writer.write(Double.toString(onePlyTime));
                writer.newLine();

            }

            writer.close();


        } catch (IOException e) {
            System.out.println(e.toString() + "Error in creation of calibration file.");
        }
    }

    public static void createOutput(OutputInfo outputInfo, String fileName) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.write(convertColumn(outputInfo.selectedLocation.columnLocation+1));
            writer.write(Integer.toString(outputInfo.selectedLocation.rowLocation+1));
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

    private static String convertColumn(Integer column) {
        String columnLocation = "uninitialized";
        switch (column) {
            case 1:
                columnLocation = "A";
                break;
            case 2:
                columnLocation = "B";
                break;
            case 3:
                columnLocation = "C";
                break;
            case 4:
                columnLocation = "D";
                break;
            case 5:
                columnLocation = "E";
                break;
            case 6:
                columnLocation = "F";
                break;
            case 7:
                columnLocation = "G";
                break;
            case 8:
                columnLocation = "H";
                break;
            case 9:
                columnLocation = "I";
                break;
            case 10:
                columnLocation = "J";
                break;
            case 11:
                columnLocation = "K";
                break;
            case 12:
                columnLocation = "L";
                break;
            case 13:
                columnLocation = "M";
                break;
            case 14:
                columnLocation = "N";
                break;
            case 15:
                columnLocation = "O";
                break;
            case 16:
                columnLocation = "P";
                break;
            case 17:
                columnLocation = "Q";
                break;
            case 18:
                columnLocation = "R";
                break;
            case 19:
                columnLocation = "S";
                break;
            case 20:
                columnLocation = "T";
                break;
            case 21:
                columnLocation = "U";
                break;
            case 22:
                columnLocation = "V";
                break;
            case 23:
                columnLocation = "W";
                break;
            case 24:
                columnLocation = "X";
                break;
            case 25:
                columnLocation = "Y";
                break;
            case 26:
                columnLocation = "Z";
                break;
            default:
                System.out.println("column index out of bounds made it to convertColumn");
        }
        return columnLocation;
    }
}
