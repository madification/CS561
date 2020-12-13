import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Search {
    static Queue<Node> bfsQueue = new LinkedList<>();

    public Search() {
    }

    //TODO check that this will take a negligible amount of time for larger searches.
    public ArrayList<Node> BFS(Node root) {
        Node currNode;
        ArrayList<Node> nodeList = new ArrayList<>();

        bfsQueue.add(root);
        nodeList.add(root);

        while (!bfsQueue.isEmpty()) {

            currNode = bfsQueue.remove();

            currNode.determineChildren(nodeList);

            bfsQueue.addAll(currNode.children);
            nodeList.addAll(currNode.children);
        }

        //Return the list of all the nodes we identified as part of the group
        return nodeList;
    }
}
