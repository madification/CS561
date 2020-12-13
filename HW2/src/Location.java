public class Location {
    int rowLocation;
    int columnLocation;


    public Location(int row, int column) {
        if (row >= 0 & row < 26 ) {
            rowLocation = row;
        } else System.out.println("row index out of bounds");

        if (column >= 0 & column < 26 ) {
        columnLocation = column;
        } else System.out.println("column index out of bounds");

    }
}
