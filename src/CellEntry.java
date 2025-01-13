public class CellEntry implements Index2D {
    private int x;
    private int y;

    // Constructor to initialize a cell entry with specific x and y coordinates
    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Checks if the cell coordinates are within valid bounds of the spreadsheet
    @Override
    public boolean isValid() {
        return x >= 0 && x < Ex2Utils.WIDTH && y >= 0 && y < Ex2Utils.HEIGHT;
    }

    // Returns the X coordinate (column index) of the cell
    @Override
    public int getX() {
        return this.x;
    }

    // Returns the Y coordinate (row index) of the cell
    @Override
    public int getY() {
        return this.y;
    }

    // Converts the cell coordinates to a readable string format (e.g., "A1")
    @Override
    public String toString() {
        return Ex2Utils.ABC[x] + y;
    }
}
