import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Search {

    static Queue<Node> bfsQueue = new LinkedList<>();
    static Stack<Node> dfsStack = new Stack<>();

    public Search() {
    }

    private static Boolean goalReached(Node node, Integer numLizards, Integer placedLizards) {
        Integer lizardCount = 0;

//    if (placedLizards == numLizards) return Boolean.TRUE;
//    else return Boolean.FALSE;

        for (int row = 0; row < node.state.length; row++) {
            for (int column = 0; column < node.state.length; column++) {
                if (node.state[row][column] == 1) lizardCount++;
            }
        }

        if (lizardCount == numLizards) return Boolean.TRUE;
        else return Boolean.FALSE;

    }


    public static OutputInfo BreadthFirstSearch(Node root, Integer numLizards) {

        // Initialize outputInfo
        OutputInfo outputInfo = new OutputInfo("FAIL", root);

        Integer placedLizards = new Integer(0);


        bfsQueue.add(root);
        long startTime = System.nanoTime();

        while (!bfsQueue.isEmpty()) {

            // If no time left, break
            if (!stillTime(startTime)) break;

            Node currNode = bfsQueue.remove();

            if (goalReached(currNode, numLizards, placedLizards)) {
                outputInfo.result = "OK";
                outputInfo.outputArrangement = currNode.state;
                break;
            }

            placedLizards = currNode.determineChildren();

            bfsQueue.addAll(currNode.children);
        }

        return outputInfo;
    }


    // Depth First Search
    public static OutputInfo DepthFirstSearch(Node root, Integer numLizards) {

        // Initialize outputInfo
        OutputInfo outputInfo = new OutputInfo("FAIL", root);
        Integer placedLizards = new Integer(0);


        dfsStack.push(root);

        long startTime = System.nanoTime();
        while (!dfsStack.isEmpty()) {

            // If no time left, break
            if (!stillTime(startTime)) {
                System.out.println("time elapsed = " + (System.nanoTime() - startTime));
                break;
            }

            Node currNode = dfsStack.pop();

            if (goalReached(currNode, numLizards, placedLizards)) {
                outputInfo.result = "OK";
                outputInfo.outputArrangement = currNode.state;
                break;
            }

            placedLizards = currNode.determineChildren();

            dfsStack.addAll(currNode.children);

        }

        return outputInfo;
    }


    public static OutputInfo simulatedAnnealing(Node root, Integer numLizards) {
        // Start with some huge number of conflicts so we'll always select the first move.
        double prevE = 100;

        // Initialize outputInfo
        OutputInfo outputInfo = new OutputInfo("FAIL", root);
        Location[] boardLocations = new Location[root.state.length * root.state.length];
        Location[] lizardLocations = new Location[numLizards];

        int[][] currState = new int[root.state.length][root.state.length];
        int[][] nextState = new int[root.state.length][root.state.length];

        Node currNode = new Node(currState, null);
        Node nextNode = new Node(nextState, null);

        int availableSpace = 0;
        for (int i = 0; i < root.state.length; i++) {
            for (int j = 0; j < root.state.length; j++) {
                currNode.state[i][j] = root.state[i][j];
                nextNode.state[i][j] = root.state[i][j];
                if (root.state[i][j] == 0) availableSpace++;
            }
        }


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //initBoard(boardLength, numLizards, boardLocations, lizardLocations); //TODO figure out the best way to format/use this function

        // Populate boardLocations array so random locations can be selected from it
        int index = 0;
        for (int i = 0; i < root.state.length; i++) {
            for (int j = 0; j < root.state.length; j++) {
                //if (root.state[i][j] != 2) {
                    Location location = new Location(i, j);
                    boardLocations[index] = location;
                    index++;
                //}
            }
        }


        // Create a random assortment of lizard locations
        int lizardsPlaced = 0;
        index = 0;
        if (availableSpace >= numLizards) {
            while (numLizards > lizardsPlaced) {
                int row = (int) (Math.random() * (root.state.length));
                int column = (int) (Math.random() * (root.state.length));
                if (currNode.state[row][column] == 0) {
                    currNode.state[row][column] = 1;
                    nextNode.state[row][column] = 1;
                    Location location = new Location(row, column);
                    lizardLocations[index] = location;
                    lizardsPlaced++;
                    index++;
                }
            }
            prevE = determineConflicts(nextNode.state, lizardLocations);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Loop until we run out of allotted time (or until we get a result and break)
        double count = 0;
        double prevT = 9 * Math.pow(10, 6);
        long startTime = System.nanoTime();

        while (stillTime(startTime)) {

            if (availableSpace < numLizards) {
                // There aren't enough open spots (0 locations) for the number of lizards we need to place.
                outputInfo.result = "FAIL";
                System.out.println("available space issue");
                break;
            }

            count++;
            double T = updateTemperature(count, prevT);

            if (T <= 0.0001 & prevE > 0) {
                outputInfo.result = "FAIL";
                System.out.println("T = " + T);
                System.out.println("prevE = " + prevE);
                break;
            } else if (T <= 0.0001 || prevE == 0) {
                outputInfo.result = "OK";
                outputInfo.outputArrangement = currNode.state;
                break;
            } else {
                Location[] newLocations = nextNode.moveLizard(lizardLocations, boardLocations);

                double nextE = determineConflicts(nextNode.state, newLocations);

                double deltaE = nextE - prevE;

                if (deltaE <= 0) {
                    // Our proposed move produced a better state than our current state. Update currNode to accept move.
                    currNode = currNode.saveNode(nextNode);
                    for (int i = 0; i < lizardLocations.length; i++) {
                        lizardLocations[i] = newLocations[i];
                    }
                    prevE = nextE;
                } else {
                    if (selectBad(deltaE, T)) {
                        // We decided to select a bad move. Update currNode to accept move.
                        currNode = currNode.saveNode(nextNode);
                        for (int i = 0; i < lizardLocations.length; i++) {
                            lizardLocations[i] = newLocations[i];
                        }
                        prevE = nextE;
                    } else {
                        // We did not come up with a better board and did not accept a bad move
                        // Reset nextNode to reject proposed changes.
                        nextNode = nextNode.saveNode(currNode);
                    }
                }

            }
            prevT = T;
        }
        System.out.println("time elapsed = " + (System.nanoTime() - startTime));

        System.out.println("prevE = " + prevE);
        return outputInfo;
    }


    private static Boolean stillTime(long startTime) {
        long currentTime = System.nanoTime();

        // 4 minutes, 45 seconds
        double limit = (3 * Math.pow(10, 11)) - (85 * Math.pow(10, 9));

        // If elapsed time is less than the time limit, there is still time
        if (currentTime - startTime < limit) {
            return Boolean.TRUE;
        }
        // We've reached the time limit, there is not still time
        return Boolean.FALSE;
    }


    private static double updateTemperature(double step, double prevT) {
        // Decrease temperature by 1/log(step)
        double temperature = 1 / Math.log(1 + step);
        return prevT - temperature*10;
    }


    private static Boolean selectBad(double deltaE, double temp) {
        double rand = 2 * Math.random(); // want to produce options from 0 through 1 [0, 1] double inclusive
        double p = Math.exp(-deltaE / (0.0000001*temp));

        if (rand < p) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private static double determineConflicts(int[][] board, Location[] proposedLocs) {
        double currE = 0;
        ArrayList<Location> foundLizards = new ArrayList<>();

        for (int index = 0; index < proposedLocs.length; index++) {
            Boolean lizardFound = Boolean.FALSE;

            // For each lizard in the proposed array of lizard locations, pull out the row and column for operation
            Location lizard = proposedLocs[index];
            int row = lizard.rowLocation;
            int column = lizard.columnLocation;

            // Determine if this lizard has already been found to be in conflict with a previously checked lizard
            for (int i = 0; i < foundLizards.size(); i++) {
                Location found = foundLizards.get(i);
                if (found.rowLocation == lizard.rowLocation) {
                    if (found.columnLocation == lizard.columnLocation) {
                        lizardFound = Boolean.TRUE;
                        break;
                    }
                }
            }

            // If the currently selected lizard has not previously been found in conflict with a previously checked lizard, continue
            if (!lizardFound) {

                //Check each column before lizard in the selected row w/markers
                for (int i = column - 1; i >= 0; i--) {
                    if (board[row][i] == 2) {
                        break;
                    } else if (board[row][i] == 1) {
                        Location found = new Location(row, i);
                        foundLizards.add(found);
                        currE++;
                    }
                }
                // Fill each column after lizard
                for (int i = column + 1; i < board.length; i++) {
                    if (board[row][i] == 2) {
                        break;
                    } else if (board[row][i] == 1) {
                        Location found = new Location(row, i);
                        foundLizards.add(found);
                        currE++;
                    }
                }

                // Fill rows above in same column
                for (int i = row - 1; i >= 0; i--) {
                    if (board[i][column] == 2) {
                        break;
                    } else if (board[i][column] == 1) {
                        Location found = new Location(i, column);
                        foundLizards.add(found);
                        currE++;
                    }
                }
                // Fill rows below in same column
                for (int i = row + 1; i < board.length; i++) {
                    if (board[i][column] == 2) {
                        break;
                    } else if (board[i][column] == 1) {
                        Location found = new Location(i, column);
                        foundLizards.add(found);
                        currE++;
                    }
                }

                // Fill left to top diagonal
                int j = column - 1;
                for (int i = row - 1; i >= 0; i--) {
                    if (j >= 0) {
                        if (board[i][j] == 2) {
                            break;
                        } else if (board[i][j] == 1) {
                            Location found = new Location(i, j);
                            foundLizards.add(found);
                            currE++;
                        }
                        j--;
                    } else break;
                }
                // Fill right to top diagonal
                j = column + 1;
                for (int i = row - 1; i >= 0; i--) {
                    if (j < board.length) {
                        if (board[i][j] == 2) {
                            break;
                        } else if (board[i][j] == 1) {
                            Location found = new Location(i, j);
                            foundLizards.add(found);
                            currE++;
                        }
                        j++;
                    } else break;
                }

                // Fill left to bottom diagonal
                j = column - 1;
                for (int i = row + 1; i < board.length; i++) {
                    if (j >= 0) {
                        if (board[i][j] == 2) {
                            break;
                        } else if (board[i][j] == 1) {
                            Location found = new Location(i, j);
                            foundLizards.add(found);
                            currE++;
                        }
                        j--;
                    } else break;
                }

                // Fill right to bottom diagonal
                j = column + 1;
                for (int i = row + 1; i < board.length; i++) {
                    if (j < board.length) {
                        if (board[i][j] == 2) {
                            break;
                        } else if (board[i][j] == 1) {
                            Location found = new Location(i, j);
                            foundLizards.add(found);
                            currE++;
                        }
                        j++;
                    } else break;
                }


            }
        }

        return currE;
    }


    private static void initBoard(Integer boardLength, Integer numLizards, Location[] boardLocations, Location[] lizardLocations) {
        // Populate boardLocations array so random locations can be selected
        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                Location location = new Location(i, j);
                boardLocations[i] = location;
            }
        }

        // Create a random assortment of lizard locations
        double rand = Math.random();
        int x = (int) rand * (boardLength + 1);
        int y = (int) rand * (boardLength + 1);
        for (int i = 0; i < numLizards; i++) {
            Location location = new Location(x, y);
            lizardLocations[i] = location;
        }
    }
}
