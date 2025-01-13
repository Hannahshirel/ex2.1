public class SCell implements Cell {
    private String line;
    private int type;
    private int order;
    private String data;

    // Constructor to initialize a cell with a given value
    public SCell(String s) {
        setData(s);
        if (isNumber(s)) {
            setType(Ex2Utils.NUMBER);
        } else if (isForm(s)) {
            setType(Ex2Utils.FORM);
        } else if (isText(s)) {
            setType(Ex2Utils.TEXT);
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT);
        }
    }

    // Returns the calculation order of the cell
    @Override
    public int getOrder() {
        return this.order;
    }

    // Returns the string representation of the cell
    @Override
    public String toString() {
        return getData();
    }

    // Sets the data for the cell and determines its type
    @Override
    public void setData(String s) {
        line = s;
        if (isNumber(s)) {
            setType(Ex2Utils.NUMBER);
        } else if (isForm(s)) {
            setType(Ex2Utils.FORM);
        } else if (isText(s)) {
            setType(Ex2Utils.TEXT);
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT);
        }
    }

    // Returns the current data of the cell
    @Override
    public String getData() {
        return line;
    }

    // Returns the type of the cell
    @Override
    public int getType() {
        return type;
    }

    // Sets the type of the cell and adjusts the calculation order
    @Override
    public void setType(int t) {
        type = t;
        if (type == Ex2Utils.NUMBER) {
            order = Ex2Utils.NUMBER;
        } else if (type == Ex2Utils.FORM) {
            order = Ex2Utils.FORM;
        } else if (type == Ex2Utils.TEXT) {
            order = Ex2Utils.TEXT;
        } else if (type == Ex2Utils.ERR_FORM_FORMAT) {
            order = Ex2Utils.ERR_FORM_FORMAT;
        }
    }

    // Sets the calculation order of the cell
    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    // Checks if the provided string represents a valid number
    public boolean isNumber(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if the provided string is valid text
    public boolean isText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        return !isNumber(text) && !text.startsWith("=");
    }

    // Checks if the provided string is a valid formula
    public static boolean isForm(String text) {
        if (text == null || !text.startsWith("=")) {
            return false;
        }
        text = text.substring(1);
        if (!areParenthesesBalanced(text)) {
            return false;
        }
        return isValidSyntax(text);
    }

    // Checks if parentheses in a formula are balanced
    private static boolean areParenthesesBalanced(String formula) {
        int count = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
                if (count < 0) {
                    return false;
                }
            }
        }
        return count == 0;
    }

    // Checks if the formula syntax is valid
    private static boolean isValidSyntax(String formula) {
        // Vérifie les motifs invalides, comme les opérateurs consécutifs
        String[] invalidPatterns = {
                "\\+\\+", "--", "\\*\\*", "//", "\\+\\-", "\\-\\+", "\\+\\*", "\\*\\+", "/\\+", "\\+/"
        };

        // Vérifie si la formule contient un motif invalide
        for (String pattern : invalidPatterns) {
            if (formula.matches(".*" + pattern + ".*")) { // Utilisation de regex pour rechercher le motif
                return false;
            }
        }

        // Vérifie si la formule commence ou se termine par un opérateur
        if (formula.matches("^[+\\-*/].*") || formula.matches(".*[+\\-*/]$")) {
            return false;
        }

        return true;
    }

}
