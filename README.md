# ex2.1

This project is a simple spreadsheet application implemented in Java. It provides functionality to create, edit, and evaluate data in a grid-like table. The spreadsheet supports different types of cell content, including text, numbers, and formulas. A graphical user interface  allows users to interact with the spreadsheet seamlessly.
Text: Any string that does not represent a number or formula.
Numbers: Numeric values (e.g., 123, 45.67).
Formulas: Expressions starting with = that can reference other cells and perform basic arithmetic operations (e.g., =A1+B1, =A2*2).
Formulas: Supports arithmetic operations (+, -, *, /), allows referencing other cells (e.g., =A1+B2).

Error Handling: Detects and marks invalid formulas , prevents and identifies circular dependencies.

Save: Save the current state of the spreadsheet to a file in CSV format.
Load: Load data from a CSV file into the spreadsheet.
Cell Colors: Black: Numbers.
Blue: Formulas.
Red: Invalid formulas.
Custom red: Circular dependency errors.
￼

CellEntry: A utility class that manages cell coordinates intuitively, with automatic validation of indices and seamless conversion to a readable format (e.g., "A1").
Index Validation:    CellEntry ensures that cell coordinates are always valid based on the defined dimensions of the spreadsheet, enhancing robustness and preventing out-of-bound errors.
￼
￼
