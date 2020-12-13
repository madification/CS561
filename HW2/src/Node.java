import java.util.ArrayList;


public class Node {

    Location boardPosition;
    int fruitType;
    int[][] board;
    Node parent;
    ArrayList<Node> children = new ArrayList<>(); //one child per placement of one additional lizard in a location that is safe and won't cause a failure


    public Node(Location boardPosition, int[][] board, Node parent) {
        this.boardPosition = boardPosition;
        this.board = board;
        this.fruitType = board[boardPosition.rowLocation][boardPosition.columnLocation];
        this.parent = parent;
    }


    public void determineChildren(ArrayList<Node> knownNodes) {

        // Check if we're on the edge (at the bottom) of the board. If we're at the bottom, don't search below
        if(this.boardPosition.rowLocation+1 < this.board.length) {
            //Look vertically down one space from node location for same fruit type
            if (this.board[this.boardPosition.rowLocation + 1][this.boardPosition.columnLocation] == this.fruitType) {
                //it's the same type of fruit so we want to add it nodes to be explored
                Location childLocation = new Location(this.boardPosition.rowLocation + 1, this.boardPosition.columnLocation);
                // Loop protection;
                if (this.parent == null || (childLocation.rowLocation != this.parent.boardPosition.rowLocation) || (childLocation.columnLocation != this.parent.boardPosition.columnLocation)) {
                        Node child = new Node(childLocation, this.board, this);
                        if(!child.isInList(knownNodes)) {
                            this.children.add(child);
                        }
                }
            }
        }
        // Check if we're on the edge (at the top) of the board. If yes, we don't search above.
        if(boardPosition.rowLocation-1 >= 0) {
            //Look vertically down one space from node location for same fruit type
            if (board[boardPosition.rowLocation - 1][boardPosition.columnLocation] == fruitType) {
                //it's the same type of fruit so we want to add it nodes to be explored
                Location childLocation = new Location(boardPosition.rowLocation - 1, boardPosition.columnLocation);
                // Loop protection
                if (parent == null || (childLocation.rowLocation != parent.boardPosition.rowLocation || childLocation.columnLocation != parent.boardPosition.columnLocation)) {
                    Node child = new Node(childLocation, this.board, this);
                    if(!child.isInList(knownNodes)) {
                        this.children.add(child);
                    }
                }
            }
        }

        // If we're in the farthest right column, don't search right
        if(boardPosition.columnLocation+1 < board.length) {
            //Look horizontally right one space from node location for same fruit type
            if (board[boardPosition.rowLocation][boardPosition.columnLocation + 1] == fruitType) {
                //it's the same type of fruit so we want to add it nodes to be explored
                Location childLocation = new Location(boardPosition.rowLocation, boardPosition.columnLocation + 1);
                // Loop protection
                if (parent == null || (childLocation.rowLocation != parent.boardPosition.rowLocation || childLocation.columnLocation != parent.boardPosition.columnLocation)) {
                    Node child = new Node(childLocation, this.board, this);
                    if(!child.isInList(knownNodes)) {
                        this.children.add(child);
                    }
                }
            }
        }
        // If we're in the left most column, don't search left
        if(boardPosition.columnLocation-1 >= 0) {
            //Look horizontally left one space from node location for same fruit type
            if (board[boardPosition.rowLocation][boardPosition.columnLocation - 1] == fruitType) {
                //it's the same type of fruit so we want to add it nodes to be explored
                Location childLocation = new Location(boardPosition.rowLocation, boardPosition.columnLocation - 1);
                // Loop protection
                if (parent == null || (childLocation.rowLocation != parent.boardPosition.rowLocation || childLocation.columnLocation != parent.boardPosition.columnLocation)) {
                    Node child = new Node(childLocation, this.board, this);
                    if(!child.isInList(knownNodes)) {
                        this.children.add(child);
                    }
                }
            }
        }
    }


    public Boolean isInList(ArrayList<Node> list){
        // Loop through list
        for (int index = 0; index < list.size(); index++){
            // Get the location associated with the entry
            Location entry = list.get(index).boardPosition;
            if (entry.rowLocation == this.boardPosition.rowLocation & entry.columnLocation == this.boardPosition.columnLocation){
                // The location of the node we're running on matches the location of the list entry
                return Boolean.TRUE;
            }
        }
        // We've looped through the entire list and none of the entry locations matched that of the node on which we're running
        return Boolean.FALSE;
    }




    public Node saveNode(Node toSave){
        int[][] dState = new int[toSave.board.length][toSave.board.length];
        Node destination = new Node(null, dState, null);

        // Save state to destination
        for (int i = 0; i < toSave.board.length; i++){
            for(int j = 0; j < toSave.board.length; j++){
                destination.board[i][j] = toSave.board[i][j];
            }
        }

        return destination;
    }



    public Node copy(){
        return new Node(this.boardPosition, this.board.clone(), this.parent);

    }
}
