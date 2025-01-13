import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    private int[][] depthArray;

    // Constructor to initialize the spreadsheet with specified dimensions
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y]; // Create a 2D array of cells
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell(""); // Initialize each cell with an empty value
            }
        }
        eval(); // Evaluate the spreadsheet after initialization
    }

    // Default constructor using predefined dimensions
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    // Get the value of a cell at specific coordinates
    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL; // Check if coordinates are within bounds
        Cell c = get(x, y); // Retrieve the cell
        if (c == null || c.getData().isEmpty()) return Ex2Utils.EMPTY_CELL; // Return empty if the cell is null or empty
        return switch (c.getType()) {
            case Ex2Utils.NUMBER, Ex2Utils.TEXT -> c.getData(); // Return data if it's a number or text
            case Ex2Utils.FORM -> eval(x, y); // Evaluate the cell if it contains a formula
            case Ex2Utils.ERR_FORM_FORMAT -> Ex2Utils.ERR_FORM; // Return error for invalid formula format
            case Ex2Utils.ERR_CYCLE_FORM -> Ex2Utils.ERR_CYCLE; // Return error for cyclic dependency
            default -> Ex2Utils.EMPTY_CELL; // Default to empty cell
        };
    }

    // Get the cell object at specific coordinates
    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    // Get a cell using string coordinates like "A1"
    @Override
    public Cell get(String cords) {
        if (cords == null || cords.length() < 2) {
            return null;
        }
        char columnChar = cords.charAt(0); // Extract the column character
        if (isChar(columnChar)) {
            try {
                int x = columnChar - 'A'; // Convert column character to index
                int y = Integer.parseInt(cords.substring(1)); // Convert row part to integer
                if (isIn(x, y)) {
                    return table[x][y];
                }
            } catch (NumberFormatException e) {
                return null; // Return null if parsing fails
            }
        }
        return null;
    }

    // Check if a character is a valid column letter
    private boolean isChar(char c) {
        return c >= 'A' && c <= 'Z';
    }

    // Get the width of the spreadsheet (number of columns)
    @Override
    public int width() {
        return table.length;
    }

    // Get the height of the spreadsheet (number of rows)
    @Override
    public int height() {
        return table[0].length;
    }

    // Set a value to a cell at specific coordinates
    @Override
    public void set(int x, int y, String c) {
        Cell cell = new SCell(c); // Create a new cell with the given value
        table[x][y] = cell; // Update the table
        eval(); // Re-evaluate the spreadsheet
    }

    // Evaluate all the cells in the spreadsheet
    @Override
    public void eval() {
        int[][] dependencyDepths = depth(); // Calculate dependency depths
        int maxDepth = findMaxDepth(dependencyDepths); // Find the maximum depth
        for (int currentDepth = 0; currentDepth <= maxDepth; currentDepth++) {
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    if (dependencyDepths[x][y] == currentDepth && table[x][y].getType() == Ex2Utils.FORM) {
                        eval(x, y); // Evaluate the cell if it matches the current depth and is a formula
                    }
                }
            }
        }
    }

    // Find the maximum depth in the dependency array
    private int findMaxDepth(int[][] dependencyDepths) {
        int maxDepth = 0;
        for (int[] row : dependencyDepths) {
            for (int depth : row) {
                if (depth > maxDepth) {
                    maxDepth = depth;
                }
            }
        }
        return maxDepth;
    }

    // Check if given coordinates are within the spreadsheet bounds
    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx >= 0 && yy >= 0;
        ans = ans && xx < width() && yy < height();
        return ans;
    }

    // Calculate the dependency depth of all cells
    @Override
    public int[][] depth() {
        int w = width();
        int h = height();
        int[][] ans = new int[width()][height()];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                ans[i][j] = -1; // Initialize all cells as not computed
            }
        }
        int depth = 0;
        int count = 0;
        int max = w * h;
        boolean flagC = true;
        while (count < max && flagC) {
            flagC = false;
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    if (ans[x][y] == -1 && canBeComputedNow(this, x, y, ans)) {
                        ans[x][y] = depth; // Set depth for the cell
                        count++;
                        flagC = true;
                    }
                }
            }
            depth++; // Increment depth for the next level
        }
        return ans;
    }

    // Check if a cell can be computed based on its dependencies
    private boolean canBeComputedNow(Sheet sheet, int currentCol, int currentRow, int[][] dependencyDepths) {
        Cell currentCell = sheet.get(currentCol, currentRow);
        if (currentCell == null || !currentCell.getData().startsWith("=")) {
            return true; // Non-formula cells are always computable
        }
        String formulaContent = currentCell.getData().substring(1).trim();
        int formulaIndex = 0;
        while (formulaIndex < formulaContent.length()) {
            if (Character.isLetter(formulaContent.charAt(formulaIndex))) {
                int refStart = formulaIndex;
                formulaIndex++;
                while (formulaIndex < formulaContent.length() && Character.isDigit(formulaContent.charAt(formulaIndex))) {
                    formulaIndex++;
                }
                String referencedCell = formulaContent.substring(refStart, formulaIndex);
                int referencedCol = referencedCell.charAt(0) - 'A';
                if (referencedCell.length() <= 1) {
                    return false; // Invalid reference
                }
                try {
                    int referencedRow = Integer.parseInt(referencedCell.substring(1));
                    if (!sheet.isIn(referencedCol, referencedRow)) {
                        return false; // Out-of-bounds reference
                    }
                    if (dependencyDepths[referencedCol][referencedRow] == -1) {
                        return false; // Uncomputed dependency
                    }
                } catch (NumberFormatException e) {
                    return false; // Invalid reference
                }
            } else {
                formulaIndex++;
            }
        }
        return true;
    }

    // Load spreadsheet data from a file
    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        String data = parts[2].trim();
                        if (isIn(x, y)) {
                            set(x, y, data); // Set data to cell
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
    }

    // Save spreadsheet data to a file
    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment - this line should be ignored in the load method\n");
            for (int y = 0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    Cell cell = get(x, y);
                    if (cell != null && !cell.getData().isEmpty()) {
                        writer.write(x + "," + y + "," + cell.toString() + "\n");
                    }
                }
            }
        }
    }

    // Evaluate a specific cell
    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) {
            return "";
        }
        return switch (cell.getType()) {
            case Ex2Utils.NUMBER, Ex2Utils.TEXT -> cell.getData(); // Return value for number or text
            case Ex2Utils.FORM -> computeForm(cell.getData()); // Evaluate formula
            default -> "#ERROR"; // Default to error
        };
    }

    // Compute the result of a formula
    public String computeForm(String formula) {
        if (!formula.startsWith("=")) {
            return formula; // Return as-is if not a formula
        }
        try {
            String processed = formula.substring(1).trim();
            if (processed.startsWith("-")) {
                processed = "0" + processed; // Handle leading negative sign
            }
            StringBuilder result = new StringBuilder();
            int i = 0;
            while (i < processed.length()) {
                if (Character.isLetter(processed.charAt(i))) {
                    int start = i;
                    i++;
                    while (i < processed.length() && Character.isDigit(processed.charAt(i))) {
                        i++;
                    }
                    String cellRef = processed.substring(start, i);
                    Cell referencedCell = get(cellRef);
                    if (referencedCell == null) {
                        return "#ERROR"; // Invalid reference
                    }
                    String value = referencedCell.getData();
                    if (value == null || value.isEmpty()) {
                        return "#ERROR"; // Empty or invalid value
                    }
                    if (value.startsWith("=")) {
                        value = computeForm(value); // Recursive evaluation
                    }
                    try {
                        Double.parseDouble(value); // Validate numeric value
                        result.append(value);
                    } catch (NumberFormatException e) {
                        return "#ERROR";
                    }
                } else {
                    result.append(processed.charAt(i)); // Append non-referenced part
                    i++;
                }
            }
            return evaluateExpression(result.toString()); // Compute final expression
        } catch (Exception e) {
            return "#ERROR";
        }
    }

    // Evaluate a mathematical expression
    private String evaluateExpression(String expr) {
        try {
            expr = expr.replace(" ", "");
            if (expr.isEmpty()) return "0";
            return String.valueOf(evaluate(expr));
        } catch (Exception e) {
            return "#ERROR";
        }
    }

    private double evaluate(String expr) {
        while (expr.contains("(")) {
            int start = expr.lastIndexOf('(');
            int end = expr.indexOf(')', start);
            if (end == -1) throw new IllegalArgumentException("Mismatched parentheses");
            String innerExpression = expr.substring(start + 1, end);
            double innerResult = evaluate(innerExpression); // Evaluate inside parentheses
            expr = expr.substring(0, start) + innerResult + expr.substring(end + 1);
        }
        return evaluateAdditionAndSubtraction(expr);
    }

    private double evaluateAdditionAndSubtraction(String expr) {
        String[] terms = expr.split("(?=[+-])|(?<=[+-])");
        double result = 0;
        boolean add = true;
        for (String term : terms) {
            if (term.equals("+")) {
                add = true;
            } else if (term.equals("-")) {
                add = false;
            } else {
                double value = evaluateMultiplicationAndDivision(term);
                result = add ? result + value : result - value;
            }
        }
        return result;
    }

    private double evaluateMultiplicationAndDivision(String expr) {
        String[] factors = expr.split("(?=[*/])|(?<=[*/])");
        double result = Double.parseDouble(factors[0]);
        for (int i = 1; i < factors.length; i += 2) {
            char operator = factors[i].charAt(0);
            double value = Double.parseDouble(factors[i + 1]);
            if (operator == '*') {
                result *= value;
            } else if (operator == '/') {
                result /= value;
            }
        }
        return result;
    }
}
