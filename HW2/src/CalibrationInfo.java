public class CalibrationInfo {

    public double timeForOnePly;
    public double remainingTime;
    public double maxPlys;
    public double startTime;
    public double[] onePlyTimeArray;
    public int boardDimensions;


    public CalibrationInfo(double timeLeft, double startTime, int boardDimensions){
        //TODO fix this for cases where inputTime is <= 5
        this.remainingTime = timeLeft;
        this.startTime = startTime;
        this.boardDimensions = boardDimensions;

        //Convert time to nano seconds
        // Save 5 seconds for reading input, backing out of the tree, and for writing output


    }

    public void setMaxPlys(int branchingFactor){
        double currElapsed = System.nanoTime() - this.startTime;
        double totalTime = ((this.remainingTime)*Math.pow(10, 9))-currElapsed;
        if(branchingFactor != 0){
            double calculatedPlys =  Math.floor(Math.log10(totalTime) / Math.log10(branchingFactor*timeForOnePly));
//            System.out.println("Calculated: " + calculatedPlys);

//            if(calculatedPlys > 1000000) calculatedPlys = 2;
//            System.out.println("Calculated: " + calculatedPlys);
            double limit_1 = 250*Math.pow(10, 9);

            if(totalTime > limit_1){
                if(boardDimensions > 20) {
                    this.maxPlys = calculatedPlys + 2;
                }
                else if(boardDimensions > 13) {
                    this.maxPlys = calculatedPlys + 3;
                }
                else if(boardDimensions > 9){
                    this.maxPlys = calculatedPlys + 4;

                }
                else if(boardDimensions > 5){
                    this.maxPlys = calculatedPlys + 5;
                }
            }
            else if (totalTime > (120*Math.pow(10, 9))){
                if(boardDimensions > 20) {
                    this.maxPlys = calculatedPlys + 2;
                }
                else if(boardDimensions > 13) {
                    this.maxPlys = calculatedPlys + 3;
                }
                else if(boardDimensions > 9){
                    this.maxPlys = calculatedPlys + 3;

                }
                else if(boardDimensions > 5){
                    this.maxPlys = calculatedPlys + 4;
                }
            }
            else if (totalTime > (30*Math.pow(10, 9))){
                if(boardDimensions > 20) {
                    this.maxPlys = calculatedPlys + 1;
                }
                else if(boardDimensions > 13) {
                    this.maxPlys = calculatedPlys + 1;
                }
                else if(boardDimensions > 9){
                    this.maxPlys = calculatedPlys + 2;

                }
                else if(boardDimensions > 5){
                    this.maxPlys = calculatedPlys + 2;
                }
            }

        }
        else this.maxPlys = 1;

    }

    public Boolean stillTime() {
        //
        double elapsed = System.nanoTime() - this.startTime;
//TODO fix this. Don't want to run until there's only 5 seconds left...
        if( elapsed+5 < this.remainingTime*Math.pow(10, 9)) {
            return Boolean.TRUE;
        }
        else {
            System.out.println("timeout");
            return Boolean.FALSE;
        }
    }
}
