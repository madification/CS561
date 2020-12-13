import java.util.ArrayList;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Node {

    int[][] state;
    Node parent;
    ArrayList<Node> children = new ArrayList<>(); //one child per placement of one additional lizard in a location that is safe and won't cause a failure

    public Node(int[][] state, Node parent) {
        this.state = state;
        this.parent = parent;
    }


    public Integer determineChildren() {
        Integer lizardCount = 0;
        Location lizardLoc = new Location(0, 0);
        Boolean lizardPlaced = FALSE;
        Boolean expandedRowSearch = FALSE;

        // Locate the last placed lizard. Assuming fillDeadZone has been completed for found lizard/ no pre-existing lizards
        for (int row = 0; row < state.length; row++) {
            for (int column = 0; column < state.length; column++) {
                if (state[row][column] == 1) {
                    lizardLoc.rowLocation = row;
                    lizardLoc.columnLocation = column;
                    lizardCount++;
                }
            }
        }

        int row = lizardLoc.rowLocation;

        // While we have not placed a lizard, loop through the columns (0 to length) and increment the rows starting
        // with the row of our last lizard. The idea is that there may still be availability in this row if there were
        // tree also in the row. If there's no availability, it'll increment the row until the end of the board. If no
        // lizard has been placed by this time, we start back at the beginning of the board to check for availability
        // above the last placed lizard (above the row at which we started). We increment the row until we hit the row
        // from which we started. If we've still not been able to place a lizard then we can now say we've searched the
        // entire board and there is NO available space left for lizard placement, so we break and return lizardPlaced
        // as FALSE.
        while (!lizardPlaced) {

            for (int column = 0; column < state.length; column++) {

                if (state[row][column] == 0) {

                    int[][] newState = fillDeadZone(state, row, column);

                    // Check that fillDeadZone worked and place lizard
                    if (newState[row][column] != 0) {
                        System.out.println("Something went wrong in fillDeadZone");
                    } else {
                        newState[row][column] = 1;
                        lizardCount++;
                        lizardPlaced = TRUE;

                        // Create child with new lizard containing state and add to list of children
                        Node child = new Node(newState, this);
                        children.add(child);
                    }
                }
            }

            if (row == lizardLoc.rowLocation && expandedRowSearch) {
                // If we've made it here, we searched the entire board and found no availability
                // break out of while statement and return that lizardPlace = False
                break;
            }
            if (row < state.length-1) {
                // Check from last placed lizard to end for availability
                row++;
                expandedRowSearch = TRUE;
            } else if (row == state.length-1) {
                // Start back at the beginning for last ditch search for availability
                row = 0;
            }

        }

        return lizardCount;

    }


    private int[][] fillDeadZone(int[][] currState, int row, int column) {

        int[][] newState = new int[currState.length][currState.length];

        // Get a state to work with that won't change our original node state (this.state)
        for (int i = 0; i < currState.length; i++) {
            for (int j = 0; j < currState.length; j++) {
                newState[i][j] = currState[i][j];
            }
        }

        // Fill each column before lizard in the selected row w/markers
        for (int i = column-1; i >= 0 ; i--) {
            if (newState[row][i] == 2) {
                break;
            } else {
                newState[row][i] = -1;
            }
        }
        // Fill each column after lizard
        for (int i = column + 1; i < state.length; i++) {
            if (newState[row][i] == 2) {
                break;
            } else {
                newState[row][i] = -1;
            }
        }

        // Fill rows above in same column
        for (int i = row-1; i >= 0; i--) {
            if (newState[i][column] == 2) {
                break;
            } else {
                newState[i][column] = -1;
            }
        }
        // Fill rows below in same column
        for (int i = row + 1; i < state.length; i++) {
            if (newState[i][column] == 2) {
                break;
            } else {
                newState[i][column] = -1;
            }
        }

        // Fill left to top diagonal
        int j = column - 1;
        for (int i = row - 1; i >= 0; i--) {
            if (j >= 0) {
                if (newState[i][j] == 2) {
                    break;
                } else {
                    newState[i][j] = -1;
                    j--;
                }
            } else break;
        }
        // Fill right to top diagonal
        j = column + 1;
        for (int i = row - 1; i >= 0; i--) {
            if (j < state.length) {
                if (newState[i][j] == 2) {
                    break;
                } else {
                    newState[i][j] = -1;
                    j++;
                }
            } else break;
        }

        // Fill left to bottom diagonal
        j = column - 1;
        for (int i = row + 1; i < state.length; i++) {
            if (j >= 0) {
                if (newState[i][j] == 2) {
                    break;
                } else {
                    newState[i][j] = -1;
                    j--;
                }
            } else break;
        }

        // Fill right to bottom diagonal
        j = column + 1;
        for (int i = row + 1; i < state.length; i++) {
            if (j < state.length) {
                if (newState[i][j] == 2) {
                    break;
                } else {
                    newState[i][j] = -1;
                    j++;
                }
            } else break;
        }

        return newState;
    }

    public Location[] moveLizard (Location [] lizardLocs, Location[] boardLocs) {

        // New array to represent updated lizard locations after successful move
        Location[] newLizardLocs = new Location[lizardLocs.length];
        for (int i = 0; i < lizardLocs.length; i++) {
            newLizardLocs[i] = lizardLocs[i];
        }

        // Select a random lizard to move
        double indexL = Math.random()*(lizardLocs.length);
        Location lizard = lizardLocs[(int)indexL];

        // Select a random location to attempt to place the lizard
        double indexB = Math.random()*(boardLocs.length);
        Location newLocation = boardLocs[(int)indexB];


        //TODO prob need to be more strategic about selecting a 'random operation'
        // This tries n^2 times to find an open spot on the board where n is the number of locations on the board
        // making the assumption that after randomizing n times, every possible index in boardLocs will have been returned by rand
        int attempts = 0;
        while(attempts < boardLocs.length*boardLocs.length){

            if (state[newLocation.rowLocation][newLocation.columnLocation] == 0) {
                // We've found an open location to place our lizard.
                // Update state of the current node by moving lizard from old location to new
                state[lizard.rowLocation][lizard.columnLocation] = 0;
                state[newLocation.rowLocation][newLocation.columnLocation] = 1;


                // Now update of array of lizard locations
                newLizardLocs[(int)indexL] = newLocation;

                return newLizardLocs;
            }

            // Track the attempts to place a lizard
            attempts++;

            // Select a new random location to attempt to place the lizard; the previous was not open.
            indexB = Math.random()*(boardLocs.length);
            newLocation = boardLocs[(int)indexB];
        }



        //TODO possibly strategy answer to previous ToDo: could make a switch case with all knight moves and randomly choose between them

//        int x = lizard.rowLocation;
//        int y = lizard.columnLocation;
//        // Move the lizard one row down. If at end of board, loop back to first row.
//        if (x+1 < state.length) {
//            newLocation.rowLocation = x+1;
//        }
//        else newLocation.rowLocation = 0;
//
//        // Move lizard one row right. If at edge of board, loop back to first column.
//        if (y+2 < state.length){
//            newLocation.columnLocation = y+2;
//        }
//        else newLocation.columnLocation = 0;

        // If we're here, then we never found an available location on the board and a lizard was not moved
        // newLizardLocs was not changed so it still == lizardLocs


        return newLizardLocs;
    }



    public Node saveNode(Node toSave){
        int[][] dState = new int[toSave.state.length][toSave.state.length];
        Node destination = new Node(dState, null);

        // Save state to destination
        for (int i = 0; i < toSave.state.length; i++){
            for(int j = 0; j < toSave.state.length; j++){
                destination.state[i][j] = toSave.state[i][j];
            }
        }

        return destination;
    }



    public Node copy(){
        return new Node(this.state.clone(), this.parent);

    }
}
