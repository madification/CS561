public class OutputInfo {

    public Location selectedLocation;
    public int [][] outputArrangement;
    public int boardDimensions;


    public OutputInfo(Location selectedLocation, int[][] resultantBoard) {
        this.selectedLocation = selectedLocation;
        this.outputArrangement = resultantBoard;
        if (resultantBoard != null) {
            this.boardDimensions = resultantBoard.length;
        }

    }
}
