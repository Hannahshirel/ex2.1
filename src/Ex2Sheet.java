import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    private int[][] depthArray;

    // Constructor initializing the sheet with specified dimensions
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }


    // Default constructor using predefined dimensions
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    // Returns the value of a cell at given coordinates
    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;

        Cell c = get(x, y);
        if (c == null || c.getData().isEmpty()) return Ex2Utils.EMPTY_CELL;

        return switch (c.getType()) {
            case Ex2Utils.NUMBER, Ex2Utils.TEXT -> c.getData();
            case Ex2Utils.FORM -> eval(x, y);
            case Ex2Utils.ERR_FORM_FORMAT -> Ex2Utils.ERR_FORM;
            case Ex2Utils.ERR_CYCLE_FORM -> Ex2Utils.ERR_CYCLE;
            default -> Ex2Utils.EMPTY_CELL;
        };
    }

    // Returns the cell object at specified coordinates
    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        if (cords == null || cords.length() < 2) {
            return null;
        }

        char columnChar = cords.charAt(0);
        if (isChar(columnChar)) {
            try {
                int x = columnChar - 'A'; // Convert column 'A', 'B', etc. to index
                int y = Integer.parseInt(cords.substring(1)); // Parse row index
                if (isIn(x, y)) {
                    return table[x][y];
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Checks if a character represents a valid column
    private boolean isChar(char c) {
        return c >= 'A' && c <= 'Z';
    }

    // Returns the width of the sheet
    @Override
    public int width() {
        return table.length;
    }

    // Returns the height of the sheet
    @Override
    public int height() {
        return table[0].length;
    }

    // Sets a cell value at given coordinates
    @Override
    public void set(int x, int y, String c) {
        Cell cell = new SCell(c);
        table[x][y] = cell;
        eval();
    }

    // Evaluates the entire sheet, processing cells based on dependencies
    @Override
    public void eval() {
        int[][] dependencyDepths = depth();
        int maxDepth = findMaxDepth(dependencyDepths);

        for (int currentDepth = 0; currentDepth <= maxDepth; currentDepth++) {
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    if (dependencyDepths[x][y] == currentDepth && table[x][y].getType() == Ex2Utils.FORM) {
                        eval(x, y);
                    }
                }
            }
        }
    }

    // Finds the maximum depth of dependencies in the sheet
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

    // Checks if the given coordinates are within bounds of the sheet
    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx >= 0 && yy >= 0;
        ans = ans && xx < width() && yy < height();
        return ans;
    }

    // Computes the dependency depths for all cells
    @Override
    public int[][] depth() {
        int w = width();
        int h = height();
        int[][] ans = new int[width()][height()];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                ans[i][j] = -1;
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
                        ans[x][y] = depth;
                        count++;
                        flagC = true;
                    }
                }
            }
            depth++;
        }

        return ans;
    }

    // Determines if a cell can be computed based on its dependencies
    private boolean canBeComputedNow(Sheet sheet, int currentCol, int currentRow, int[][] dependencyDepths) {
        Cell currentCell = sheet.get(currentCol, currentRow);

        if (currentCell == null || !currentCell.getData().startsWith("=")) {
            return true;
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
                    return false;
                }

                try {
                    int referencedRow = Integer.parseInt(referencedCell.substring(1));

                    if (!sheet.isIn(referencedCol, referencedRow)) {
                        return false;
                    }

                    if (dependencyDepths[referencedCol][referencedRow] == -1) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                formulaIndex++;
            }
        }

        return true;
    }

    // Loads the sheet data from a file
    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine(); // Skip the header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        String data = parts[2].trim();
                        if (isIn(x, y)) {
                            set(x, y, data);
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid lines
                    }
                }
            }
        }
    }

    // Saves the sheet data to a file
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

    // Evaluates a single cell
    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) {
            return "";
        }

        switch (cell.getType()) {
            case Ex2Utils.NUMBER:
            case Ex2Utils.TEXT:
                return cell.getData();

            case Ex2Utils.FORM:
                return computeForm(cell.getData());

            default:
                return "#ERROR";
        }
    }

    // Processes a formula string and computes its value
    public String computeForm(String formula) {
        if (!formula.startsWith("=")) {
            return formula;
        }

        try {
            String processed = formula.substring(1).trim();
            if (processed.startsWith("-")) {
                processed = "0" + processed;
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
                        return "#ERROR";
                    }

                    String value = referencedCell.getData();
                    if (value == null || value.isEmpty()) {
                        return "#ERROR";
                    }

                    if (value.startsWith("=")) {
                        value = computeForm(value); // Recursive evaluation
                    }

                    try {
                        Double.parseDouble(value); // Validate if value is numeric
                        result.append(value);
                    } catch (NumberFormatException e) {
                        return "#ERROR";
                    }
                } else {
                    result.append(processed.charAt(i));
                    i++;
                }
            }

            return evaluateExpression(result.toString());
        } catch (Exception e) {
            return "#ERROR";
        }
    }

    // Evaluates a mathematical expression
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
        // Handle parentheses
        while (expr.contains("(")) {
            int start = expr.lastIndexOf('(');
            int end = expr.indexOf(')', start);
            if (end == -1) throw new IllegalArgumentException("Mismatched parentheses");

            String innerExpression = expr.substring(start + 1, end);
            double innerResult = evaluate(innerExpression);
            expr = expr.substring(0, start) + innerResult + expr.substring(end + 1);
        }

        // Split and evaluate by precedence: *, / first, then +, -
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
