public class Main {

    public static void main(String args[]) {

        //Designate input.txt as name of file we want to read
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        // Collect input information from input.txt
        InputInfo inputInfo = FileManipulation.readInput(inputFile);
        Resolution resolution = new Resolution(inputInfo);

        OutputInfo outputInfo = resolution.resolve();

        FileManipulation.createOutput(outputInfo, outputFile);

     }
}
