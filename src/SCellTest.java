import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SCellTest {

    @Test
    public void testIsNumber() {
        SCell cell = new SCell("");

        assertTrue(cell.isNumber("123"), "123 should be a valid number");
        assertTrue(cell.isNumber("123.45"), "123.45 should be a valid number");
        assertTrue(cell.isNumber("-123"), "-123 should be a valid number");
        assertFalse(cell.isNumber("abc"), "abc should not be a valid number");
        assertFalse(cell.isNumber(null), "null should not be a valid number");
        assertFalse(cell.isNumber(""), "Empty string should not be a valid number");
        assertFalse(cell.isNumber("   "), "Whitespace should not be a valid number");
    }

    @Test
    public void testIsText() {
        SCell cell = new SCell("");

        assertTrue(cell.isText("Hello"), "Hello should be considered text");
        assertFalse(cell.isText("=A1+B1"), "=A1+B1 should not be considered text");
        assertFalse(cell.isText("123"), "123 should not be considered text");
        assertFalse(cell.isText(null), "null should not be considered text");
        assertFalse(cell.isText(""), "Empty string should not be considered text");
        assertFalse(cell.isText("   "), "Whitespace should not be considered text");
    }

    @Test
    public void testIsForm() {
        // Valid formulas
        assertTrue(SCell.isForm("=A1+B1"), "=A1+B1 should be recognized as a valid formula");
        assertTrue(SCell.isForm("=C1-D1"), "=C1-D1 should be recognized as a valid formula");
        assertTrue(SCell.isForm("=E1/2"), "=E1/2 should be recognized as a valid formula");
        assertTrue(SCell.isForm("=A1*B1"), "=A1*B1 should be recognized as a valid formula");

        // Invalid formulas
        assertFalse(SCell.isForm("A1+B1"), "A1+B1 should not be recognized as a valid formula (missing =)");
        assertFalse(SCell.isForm("=+A1"), "=+A1 should not be recognized as a valid formula");
        assertFalse(SCell.isForm("=A1++B1"), "=A1++B1 should not be recognized as a valid formula");
        assertFalse(SCell.isForm("=A1/"), "=A1/ should not be recognized as a valid formula");
        assertFalse(SCell.isForm(""), "Empty string should not be recognized as a valid formula");
    }
    @Test
    public void testComputeForm() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set up cells with valid data
        sheet.set(0, 0, "10"); // A1
        sheet.set(1, 0, "20"); // B1
        sheet.set(2, 0, "30"); // C1
        sheet.set(3, 0, "5");  // D1
        sheet.set(4, 0, "15"); // E1

        // Formulas that are guaranteed to work based on the setup
        String formula1 = "=A1+B1"; // 10 + 20 = 30
        String formula2 = "=A1*2";  // 10 * 2 = 20
        String formula3 = "=C1-D1"; // 30 - 5 = 25
        String formula4 = "=E1/5";  // 15 / 5 = 3
        String formula5 = "=B1+C1"; // 20 + 30 = 50


    }

    @Test
    public void testSaveAndLoad() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set up cells with data
        sheet.set(0, 0, "10"); // A1
        sheet.set(1, 0, "20"); // B1

        String fileName = "testSheet.csv";

        try {
            // Save the sheet to a file
            sheet.save(fileName);

            // Create a new sheet and load the saved data
            Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
            loadedSheet.load(fileName);

            // Validate the loaded data
            assertEquals("10", loadedSheet.value(0, 0), "Cell A1 should contain 10");
            assertEquals("20", loadedSheet.value(1, 0), "Cell B1 should contain 20");

        } catch (Exception e) {
            fail("Exception occurred during save/load test: " + e.getMessage());
        } finally {
            // Clean up test file
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }


}