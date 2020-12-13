import java.io.*;
import java.util.ArrayList;

public class FileManipulation {

    private FileManipulation() {

    }

    public static InputInfo readInput(String fileName) {
        InputInfo inputInfo = new InputInfo();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            // Process the queries
            inputInfo.numQueries = Integer.parseInt(reader.readLine());
            for (int i = 0; i < inputInfo.numQueries; i++){
                String lineRead = reader.readLine();
                Sentence querySentence = new Sentence(false, lineRead);
//                ArrayList<Literal> currQuery = querySentence.processSentence();
                if(querySentence.djncOfLiterals.size() > 1) System.out.println("ISSUE: Found more than one literal in query.");
                inputInfo.queries.addAll(querySentence.djncOfLiterals);
            }

            // Process the knowledge base
            inputInfo.numSentences = Integer.parseInt(reader.readLine());
            for(int i = 0; i < inputInfo.numSentences; i++){
                String lineRead = reader.readLine();
                Boolean or = lineRead.contains("|");

                Sentence currSentence = new Sentence(or, lineRead);
                inputInfo.knowledgeBase.add(currSentence);

                currSentence.processKB(i);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println(e.toString() + "Could not find input.txt file.");
        }

        return inputInfo;
    }
    // end readInput




    public static void createOutput(OutputInfo outputInfo, String fileName) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            for(int i = 0; i < outputInfo.resultsSize; i++) {
                writer.write(outputInfo.results[i]);
                writer.newLine();
            }

            writer.close();


        } catch (IOException e) {
            System.out.println(e.toString() + "Error in creation of output file. Potential mismatch between number of results collected and number of queries.");
        }
    }

}
