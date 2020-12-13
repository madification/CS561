import java.util.ArrayList;


public class MiniMax {

    public int boardDimensions;
    public int[][] boardArrangement;
    private CalibrationInfo calInfo;
    private ArrayList<Node> initialNodeList;

    private double score;

    public MiniMax(InputInfo inputInfo, CalibrationInfo calInfo) {
        this.boardDimensions = inputInfo.boardDimensions;
        this.boardArrangement = inputInfo.initArrangement;
        this.calInfo = calInfo;
        this.initialNodeList = inputInfo.nodeList;
    }

    public OutputInfo determineMove(Boolean calibrate){

        Ply first = new Ply(Ply.whosTurn.UNINITIALIZED, null, this.initialNodeList, null);
        Ply alpha_ply = new Ply(Ply.whosTurn.MAX, null, this.initialNodeList, null);
        Ply beta_ply = new Ply(Ply.whosTurn.MIN, null, this.initialNodeList, null);
        alpha_ply.currScore = -1*Double.MAX_VALUE;
        beta_ply.currScore = Double.MAX_VALUE;

        OutputInfo outputInfo = new OutputInfo(null,this.boardArrangement);

        // Determine all possible moves for this board
        ArrayList<Group> availableMoves = Group.getAvailableMoves(first.nodesInBoard);

        if(!calibrate) {
            calInfo.setMaxPlys(availableMoves.size());

//            System.out.println("adjusted :  " + calInfo.maxPlys);
        }

        // RUN MINIMAX WOOOOOOOOOO!!!
        Ply lastSelected = MaxMove(first, alpha_ply, beta_ply);

        Ply returnPly = new Ply(lastSelected.who, lastSelected.move, lastSelected.nodesInBoard, lastSelected.parent);

        if(lastSelected.who == Ply.whosTurn.UNINITIALIZED){
            // We didn't get a chance to move, but my isEndState function should have given us a last ditch effort move
            lastSelected.currScore = lastSelected.move.points;
            returnPly = lastSelected;
        }
        else {
            while (lastSelected.parent.who != Ply.whosTurn.UNINITIALIZED) {
                returnPly = lastSelected.parent;
                lastSelected = lastSelected.parent;
            }
        }

        //TODO find test cases on vocarem and find out why I'm getting 0 scores
        //System.out.println("Score: " + returnPly.currScore);
        //this.score = lastSelected.currScore;

        outputInfo.selectedLocation = returnPly.move.connectedCells.get(0).boardPosition;

        outputInfo.outputArrangement = returnPly.move.postGravityArrangement;

        return outputInfo;

    }

    public double getScore() {
        return score;
    }

    // ply = state, currMax_a = alpha, currMin_b = beta, Double(score) return = return utility
    private Ply MaxMove(Ply ply, Ply alpha_ply, Ply beta_ply){ //, Double currMax_a,Double currMin_b
        // stateScore is the place holder for v is the place holder for alpha, set to negative infinity
        double stateScore = -1*Double.MAX_VALUE;
        double alpha = alpha_ply.currScore;
        double beta = beta_ply.currScore;

        //Initialization of returnPly is redundant. It will always be overwritten
        Ply returnPly = new Ply(Ply.whosTurn.MAX, ply.move, ply.nodesInBoard, ply);
        returnPly.currScore = stateScore;
        returnPly.incrementPly();

        // Check if we're at the goal
        if(isEndState(ply)) return ply;

        // Determine all possible moves for this board
        ArrayList<Group> availableMoves = Group.getAvailableMoves(ply.nodesInBoard);

        // for each a in ACTIONS(state)
        // Explore each available move largest move at end of list so we move backwards through availableMoves list
        for(int i = 0; i < availableMoves.size() ; i++) {

            // get move
            Group move = availableMoves.get(availableMoves.size()-1-i);//availableMoves.size()-1-i);

            // apply the move to the board (remove nodes in the selected group/move and apply gravity) to get the new state
            ArrayList<Node> newNodeList = move.makeMove(ply.nodesInBoard);

            // RESULT(s,a) result of action a applied to state s
            Ply newPly = new Ply(Ply.whosTurn.MAX, move, newNodeList, ply);
            // Keep track of how many moves we've made/how many plys deep we are
            newPly.incrementPly();

            Ply minPly = MinMove(newPly, alpha_ply, beta_ply);
            stateScore = Math.max(stateScore, minPly.currScore);

            if(stateScore != returnPly.currScore){
                // score of minPly was better, keep that ply
                returnPly = minPly; //new Ply(minPly.move, minPly.nodesInBoard, minPly.parent);
            } //else returnPly still gets us a better score, keep it


            //Alpha-Beta pruning
            if(stateScore >= beta) {
//                System.out.println("Pruned x moves: " + (availableMoves.size()-1-i));
                return returnPly;
            }
            alpha = Math.max(alpha, stateScore);
            if(alpha == returnPly.currScore){
                alpha_ply = returnPly;
            }

        }

        return returnPly;
    }

    private Ply MinMove(Ply ply, Ply alpha_ply, Ply beta_ply){ //, Double currMax_a,Double currMin_b
        // stateScore is the place holder for v is the place holder for alpha, set to negative infinity
        double stateScore = Double.MAX_VALUE;
        double alpha = alpha_ply.currScore;
        double beta = beta_ply.currScore;

        //Initialization of returnPly is redundant. It will always be overwritten
        Ply returnPly = new Ply(Ply.whosTurn.MIN, ply.move, ply.nodesInBoard, ply);
        returnPly.currScore = stateScore;
        returnPly.incrementPly();

        if(isEndState(ply)) return ply;

        // Determine all possible moves for this board
        ArrayList<Group> availableMoves = Group.getAvailableMoves(ply.nodesInBoard);

        // for each a in ACTIONS(state)
        // Explore each available move
        for(int i = 0; i < availableMoves.size(); i++) {

            // get move
            Group move = availableMoves.get(availableMoves.size()-1-i);//availableMoves.size()-1-i);

            // apply the move to the board (remove nodes in the selected group/move and apply gravity) to get the new state
            ArrayList<Node> newNodeList = move.makeMove(ply.nodesInBoard);

            // RESULT(s,a) result of action a applied to state s
            Ply newPly = new Ply(Ply.whosTurn.MIN, move, newNodeList, ply);
            // Keep track of how many moves we've made/how many plys deep we are
            newPly.incrementPly();

            Ply maxPly =  MaxMove(newPly, alpha_ply, beta_ply);
            stateScore = Math.min(stateScore, maxPly.currScore);

            if(stateScore != returnPly.currScore){
                // score of minPly was better, keep that ply
                returnPly = maxPly; //new Ply(minPly.move, minPly.nodesInBoard, minPly.parent);
            } //else returnPly still gets us a better score, keep it

            if(stateScore <= alpha){
//                System.out.println("Pruned n moves: " + (availableMoves.size()-1-i));
                return returnPly;
            }
            beta = Math.min(beta, stateScore);
            if(beta == returnPly.currScore){
                beta_ply = returnPly;
            }
        }

        return returnPly;
    }

    private Boolean isEndState(Ply ply){

        // If the list of Nodes is empty, that means we have no non-empty spaces left on the board ie no fruit left
        if(ply.nodesInBoard.isEmpty()) {
            return Boolean.TRUE;
        }

        if(ply.plyNumber > calInfo.maxPlys || !calInfo.stillTime()) {
            // If we've reached the maximum number of plys or if there is not still time left, we're in the end state
            //Check if we've been able to select a move yet
            if(ply.move == null){
                // If we selected a move yet, but we're out of time, at least return one node in the board
                ArrayList<Group> availableMoves = Group.getAvailableMoves(ply.nodesInBoard);
                Group lastEffortMove = availableMoves.get(availableMoves.size()-1);
                lastEffortMove.makeMove(ply.nodesInBoard);
                ply.move = lastEffortMove;
            }
            return Boolean.TRUE;
        }
        else return Boolean.FALSE;

    }





}
