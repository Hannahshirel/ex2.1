public class SCell implements Cell {
    private String line;
    private int type;
    private int order;
    private String data;

    public SCell(String s) {
        setData(s); // Initialize the cell with the provided data
        if (isNumber(s)) {
            setType(Ex2Utils.NUMBER); // Set type to number if the input is numeric
        } else if (isForm(s)) {
            setType(Ex2Utils.FORM); // Set type to formula if the input starts with '='
        } else if (isText(s)) {
            setType(Ex2Utils.TEXT); // Set type to text otherwise
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT); // Mark as an error if none of the above
        }
    }

    @Override
    public int getOrder() {
        return this.order; // Retrieve the computation order of the cell
    }

    @Override
    public String toString() {
        return getData(); // Convert cell content to a string representation
    }

    @Override
    public void setData(String s) {
        line = s; // Update the cell's content
        if (isNumber(s)) {
            setType(Ex2Utils.NUMBER); // Set type to number if the input is numeric
        } else if (isForm(s)) {
            setType(Ex2Utils.FORM); // Set type to formula if the input starts with '='
        } else if (isText(s)) {
            setType(Ex2Utils.TEXT); // Set type to text otherwise
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT); // Mark as an error if none of the above
        }
    }

    @Override
    public String getData() {
        return line; // Return the content of the cell
    }

    @Override
    public int getType() {
        return type; // Return the type of the cell
    }

    @Override
    public void setType(int t) {
        type = t; // Set the type of the cell
        if (type == Ex2Utils.NUMBER) {
            order = Ex2Utils.NUMBER; // Set computation order for numbers
        } else if (type == Ex2Utils.FORM) {
            order = Ex2Utils.FORM; // Set computation order for formulas
        } else if (type == Ex2Utils.TEXT) {
            order = Ex2Utils.TEXT; // Set computation order for text
        } else if (type == Ex2Utils.ERR_FORM_FORMAT) {
            order = Ex2Utils.ERR_FORM_FORMAT; // Set order for error type
        }
    }

    @Override
    public void setOrder(int t) {
        this.order = t; // Update the computation order of the cell
    }

    public boolean isNumber(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false; // Return false if the string is null or empty
        }
        try {
            Double.parseDouble(text); // Check if the string can be parsed to a number
            return true;
        } catch (NumberFormatException e) {
            return false; // Return false if parsing fails
        }
    }

    public boolean isText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false; // Return false if the string is null or empty
        }
        return !isNumber(text) && !text.startsWith("="); // Check if it's not a number or formula
    }

    public static boolean isForm(String text) {
        if (text == null || !text.startsWith("=")) {
            return false; // Return false if the string doesn't start with '='
        }
        text = text.substring(1); // Remove the '=' character
        if (!areParenthesesBalanced(text)) {
            return false; // Check if parentheses are balanced
        }
        return isValidSyntax(text); // Validate the formula syntax
    }

    private static boolean areParenthesesBalanced(String formula) {
        int count = 0; // Counter for open and close parentheses
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                count++; // Increment for open parenthesis
            } else if (c == ')') {
                count--; // Decrement for close parenthesis
                if (count < 0) {
                    return false; // Return false if close parenthesis exceeds open
                }
            }
        }
        return count == 0; // Return true if all parentheses are balanced
    }

    private static boolean isValidSyntax(String formula) {
        String[] invalidPatterns = {
                "\\+\\+", "--", "\\*\\*", "//", "\\+\\-", "\\-\\+", "\\+\\*", "\\*\\+", "/\\+", "\\+/"
        }; // Define invalid patterns like consecutive operators

        for (String pattern : invalidPatterns) {
            if (formula.matches(".*" + pattern + ".*")) {
                return false; // Return false if any invalid pattern is found
            }
        }

        if (formula.matches("^[+*/].*") || formula.matches(".*[+\\-*/]$")) {
            return false; // Check for invalid start or end operators
        }

        return true; // Return true if no syntax issues are detected
    }
}
