import java.util.ArrayList;

public class InputInfo {

    int numQueries = 0;
    int numSentences = 0;
    ArrayList<Sentence> knowledgeBase;
    ArrayList<Literal> queries;
    ArrayList<String> constants;


    public InputInfo() {

        this.queries = new ArrayList<>();
        this.knowledgeBase = new ArrayList<>();
        this.constants = new ArrayList<>();

    }


}
