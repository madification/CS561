
import java.io.*;

public class FileManipulation {

    private FileManipulation() {

    }

    public static InputInfo readInput(String fileName) {
        InputInfo inputInfo = new InputInfo();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            inputInfo.searchType = reader.readLine();
            inputInfo.nurseryDimensions = Integer.parseInt(reader.readLine());
            inputInfo.initArrangement = new int[inputInfo.nurseryDimensions][inputInfo.nurseryDimensions];
            inputInfo.numLizards = Integer.parseInt(reader.readLine());

            for (int row = 0; row < inputInfo.nurseryDimensions; row++) {
                String[] lineRead = reader.readLine().split("");

                for (int column = 0; column < inputInfo.nurseryDimensions; column++) {
                    inputInfo.initArrangement[row][column] = Integer.parseInt(lineRead[column]);
                    // If decide to try to handle pre-placed lizards, this is where I can check and set a flag or something
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.toString() + "Could not find input.txt file.");
        }

        return inputInfo;

    }

    public static void createOutput(OutputInfo outputInfo, String fileName) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.write(outputInfo.result);

            if (outputInfo.result == "OK") {
                writer.newLine();
                for (int row = 0; row < outputInfo.nurseryDimensions; row++) {
                    for (int column = 0; column < outputInfo.nurseryDimensions; column++) {
                        // Remove dead zone markers
                        if (outputInfo.outputArrangement[row][column] == -1) {
                            outputInfo.outputArrangement[row][column] = 0;
                        }
                        // Place in file
                        writer.write(Integer.toString(outputInfo.outputArrangement[row][column]));
                    }
                    writer.newLine();
                }
            }

            writer.close();


        } catch (IOException e) {
            System.out.println(e.toString() + "Error in creation of output file.");
        }
    }


    public static void createTestOutput(OutputInfo outputInfo, String fileName) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.write(outputInfo.result);
            writer.newLine();
            for (int row = 0; row < outputInfo.nurseryDimensions; row++) {
                for (int column = 0; column < outputInfo.nurseryDimensions; column++) {
                    // Remove dead zone markers
                    if (outputInfo.outputArrangement[row][column] == -1) {
                        outputInfo.outputArrangement[row][column] = 0;
                    }
                    // Place in file
                    writer.write(Integer.toString(outputInfo.outputArrangement[row][column]));
                }
                writer.newLine();
            }

            writer.newLine();
            writer.write("SOLUTION PATH");
            writer.newLine();

            for (int i = 0; i < outputInfo.solutionPath.size(); i++) {
                Node currParent = outputInfo.solutionPath.get(i);
                writer.write("Parent " + Integer.toString(i));
                writer.newLine();
                for (int row = 0; row < outputInfo.nurseryDimensions; row++) {
                    for (int column = 0; column < outputInfo.nurseryDimensions; column++) {
                        writer.write(Integer.toString(currParent.state[row][column]));
                    }
                    writer.newLine();
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString() + "Error in creation of output file.");
        }
    }


}
