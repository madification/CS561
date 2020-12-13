

public class Main {
    public static void main(String args[]) {

        //Designate input.txt as name of file we want to read
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        // Collect input information from input.txt
        InputInfo inputInfo = FileManipulation.readInput(inputFile);

        // Create the root node from input information
        Node root = new Node(inputInfo.initArrangement, null);

        // Initialize output information for output.txt
        OutputInfo outputInfo = new OutputInfo("FAIL", root);

        switch (inputInfo.searchType) {
            case "DFS":
                outputInfo = Search.DepthFirstSearch(root, inputInfo.numLizards);
                break;
            case "BFS":
                outputInfo = Search.BreadthFirstSearch(root, inputInfo.numLizards);
                break;
            case "SA" :
                outputInfo = Search.simulatedAnnealing(root, inputInfo.numLizards);
                break;
                default:
                System.out.println("Search Type from input.txt not recognized.");
                break;

        }

        outputInfo.nurseryDimensions = inputInfo.nurseryDimensions;
        FileManipulation.createOutput(outputInfo, outputFile);

    }

}
