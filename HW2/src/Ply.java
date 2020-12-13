
import java.util.ArrayList;

public class Ply {

    public enum whosTurn {MIN, MAX, UNINITIALIZED}

    public whosTurn who;
    public Group move;
    public ArrayList<Node> nodesInBoard;
    public double currScore; // This is the number of points collected from the all moves to this point
    public int plyNumber;
    public Ply parent = null;


    public Ply(whosTurn who, Group selectedMove, ArrayList<Node> newNodeList, Ply parentPly) {
        this.who = who;
        this.move = selectedMove;
        // Non-empty spaces in board, could be seen as remaining available moves
        this.nodesInBoard = newNodeList;
        this.parent = parentPly;


        if(this.parent == null){
            this.plyNumber = 1;
            if(selectedMove == null){
                this.currScore = 0.0;
            }
            else {
                if(this.who == whosTurn.MAX){
                    this.currScore = selectedMove.points;
                }
                else this.currScore = -1*selectedMove.points;
            }
        }
        else if (this.parent.parent == null){
            if(selectedMove == null){
                this.currScore = 0.0;
            }
            else {
                if(this.who == whosTurn.MAX){
                    this.currScore = selectedMove.points;
                }
                else this.currScore = -1*selectedMove.points;
            }
        }
        else {
            if (this.who == whosTurn.MAX) {
                //if MAX just made the move, add the point to the move
                this.currScore = this.parent.currScore + selectedMove.points;
            } //else if MIN made the move, subtract the points
            else this.currScore = this.parent.currScore - selectedMove.points;

            if(this.who == whosTurn.UNINITIALIZED){
                System.out.println("Something's wrong. Uninitialized whosTurn Ply has parents and gparents.");
            }
        }
    }

    public void incrementPly(){
        this.plyNumber = parent.plyNumber + 1;
    }
}
