import java.util.ArrayList;
import java.util.Comparator;

public class Group {

    double points = 0.0;
    ArrayList<Node> connectedCells = new ArrayList<>(); //each node is a cell that is part of the group
    int fruitType;
    Node sampleNode;
    int[][] postGravityArrangement;


    public Group(ArrayList<Node> foundNodes) {
        this.connectedCells = foundNodes;
        this.sampleNode = foundNodes.get(0); //TODO if running out of space, can get rid of this
        this.fruitType = this.sampleNode.fruitType;
        this.postGravityArrangement = new int[sampleNode.board.length][sampleNode.board.length];


        // The number of nodes in the group is equivalent to the number of points. The score is the square of the points.
        this.points = Math.pow(connectedCells.size(), 2);
    }


    public ArrayList<Group> sortGroups(ArrayList<Group> availableGroups) {

        availableGroups.add(this);
        availableGroups.sort(Comparator.comparingDouble(g -> g.points));

        return availableGroups;
    }

    public ArrayList<Node> updateUnexploredList(ArrayList<Node> unexploredList) {

        for (int i = 0; i < this.connectedCells.size(); i++) {
            Node found = this.connectedCells.get(i);
            for (int j = 0; j < unexploredList.size(); j++) {
                Node unex = unexploredList.get(j);
                if (unex.boardPosition.columnLocation == found.boardPosition.columnLocation) {
                    if (unex.boardPosition.rowLocation == found.boardPosition.rowLocation) {
                        unexploredList.remove(j);
                        break;
                    }
                }
            }
        }
        return unexploredList;
    }


    public static ArrayList<Group> getAvailableMoves (ArrayList<Node> currNodes) {
        Search search = new Search();
        ArrayList<Group> groupList = new ArrayList<>();

        ArrayList<Node> unexploredNodes = new ArrayList<>();
        // Create a local list of all nodes to track those we have not yet visited
        unexploredNodes.addAll(currNodes);

        // Find all possible moves
        int index = 0;
        while (!unexploredNodes.isEmpty()) {
            // Select the next unexplored node
            Node node = unexploredNodes.get(index);
            // Look at connected nodes for same fruit type
            ArrayList<Node> foundNodes = search.BFS(node);
            // Place all nodes in the discovered group in a list sorted by the score potential for that group
            Group group = new Group(foundNodes);
            groupList = group.sortGroups(groupList);
            unexploredNodes = group.updateUnexploredList(unexploredNodes);
        }

        // Now we have a list of all possible moves/ all possibly ways to score for this board
        return groupList;
    }

    public ArrayList<Node> makeMove(ArrayList<Node> current) {
        // Create a local list of all the nodes in the current board
        ArrayList<Node> updated = new ArrayList<>();

        // Get a local state of the board
        int[][] arrangement = new int[this.sampleNode.board.length][this.sampleNode.board.length];
        for (int row = 0; row < this.sampleNode.board.length; row++) {
            for (int column = 0; column < this.sampleNode.board.length; column++) {
                arrangement[row][column] = this.sampleNode.board[row][column];
            }
        }

        updated.addAll(current);
        // Remove all the nodes involved in the move/group
        for (int i = 0; i < this.connectedCells.size(); i++) {
            for (int j = 0; j < updated.size(); j++) {
                if (this.connectedCells.get(i).boardPosition.rowLocation == updated.get(j).boardPosition.rowLocation & this.connectedCells.get(i).boardPosition.columnLocation == updated.get(j).boardPosition.columnLocation) {
                    updated.remove(j);
                    arrangement[this.connectedCells.get(i).boardPosition.rowLocation][this.connectedCells.get(i).boardPosition.columnLocation] = -1;
                    break;
                }
            }
        }

        // rearrange board to 'apply gravity' and move fruit down into the empty spots (move empty cells up)
        // this returns a list of all non-empty nodes in the board now
        // Return our updated list of nodes
        return this.applyGravity(updated, arrangement);
    }

    private ArrayList<Node> applyGravity(ArrayList<Node> postMove, int[][] arrangement) {
        ArrayList<Node> postGravity = new ArrayList<>();

        // Make if the board is empty ie has no fruit left, skip all this and just return the empty list
        if (!postMove.isEmpty()) {

            // Now we have an arrangement with empty cells in the middle somewhere, let the gravity magic happen
            // Loop through board from bottom up; move through columns left to right, move through rows bottom to top
            for (int column = 0; column < arrangement.length; column++) {
                for (int row = arrangement.length - 1; row >= 0; row--) {
                    if (arrangement[row][column] == -1) {
                        // If we're at the top of the board, there's no more moving fruit down, just break
                        if (row == 0) break;

                        // Check rows above for fruit, move it down if found and leave behind an empty cell
                        int i = 0;
                        for (int n = 1; n <= row; n++) {
                            if (arrangement[row - n][column] != -1) {
                                // The position directly above has fruit, move it down
                                arrangement[row - i][column] = arrangement[row - n][column];
                                i++;
                                arrangement[row - n][column] = -1;
                            }
                        }
                        // We've now found the lowest most empty cell for this row and moved all fruit above it down,
                        // proceed to next column
                        break;
                    }
                }
            }

            // Now that arrangement is completely updated, loop through it (again unfortunately), create a node for each position and place all the nodes in postGravity
            for (int row = 0; row < arrangement.length; row++) {
                for (int column = 0; column < arrangement.length; column++) {
                    if (arrangement[row][column] != -1) {
                        Location nodeLocation = new Location(row, column);
                        Node node = new Node(nodeLocation, arrangement, null);
                        postGravity.add(node);
                    }
                }
            }
            //update move's postGravityArrangement to save the board post move
            this.postGravityArrangement = arrangement;

            return postGravity;
        }

        // Board is empty, fill with -1 to show that
        for (int row = 0; row < arrangement.length; row++) {
            for (int column = 0; column < arrangement.length; column++) {
                this.postGravityArrangement[row][column] = -1;
            }
        }

        // The list postMove was empty, just pass it back up
        return postMove;
    }
}
