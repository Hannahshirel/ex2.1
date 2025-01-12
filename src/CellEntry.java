

// Add your documentation below:

public class CellEntry  implements Index2D {
    private int x;
    private int y;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isValid() {
        return x >= 0 && x< Ex2Utils.WIDTH &&  y >= 0 && y< Ex2Utils.HEIGHT; // Une cellule est valide si ses coordonnées sont positives
    }

    @Override
    public int getX() {
        return this.x; // Retourne l'indice X (colonne)
    }

    @Override
    public int getY() {
        return this.y; // Retourne l'indice Y (ligne)
    }

    @Override
    public String toString() {
        return Ex2Utils.ABC[x]+ y;
    }
}

