import java.util.ArrayList;

public class OutputInfo {
    public String result = "FAIL";
    public int [][] outputArrangement;
    public Integer nurseryDimensions = 0;
    public ArrayList<Node> solutionPath = null;    //TODO get rid of this for actual lizard hw

    public OutputInfo(String Result, Node node) {
        result = Result;

        if (Result == "OK"){
            outputArrangement = node.state;

            int i = 0;
            while (node.parent != null) {
                solutionPath.set(i, node.parent);
                i++;
            }
        }
        else if (Result == "FAIL") {
            outputArrangement = null;
        }
        else System.out.println("Invalid Result passed for creating OutputInfo.");
    }
}
