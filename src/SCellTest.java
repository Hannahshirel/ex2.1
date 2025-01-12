import org.junit.jupiter.api.Test;

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
        assertTrue(SCell.isForm("=A1+B1"), "=A1+B1 should be a valid formula");
        assertTrue(SCell.isForm("=SUM(A1:A10)"), "=SUM(A1:A10) should be a valid formula");
        assertTrue(SCell.isForm("=123"), "=123 should be a valid formula");
        assertTrue(SCell.isForm("=A1+(B2*2)"), "=A1+(B2*2) should be a valid formula");
        assertFalse(SCell.isForm("A1+B1"), "A1+B1 should not be a valid formula");
        assertFalse(SCell.isForm("="), "= should not be a valid formula");
        assertFalse(SCell.isForm(null), "null should not be a valid formula");
        assertFalse(SCell.isForm("=A1++B1"), "=A1++B1 should not be a valid formula");
        assertFalse(SCell.isForm("=A1+"), "=A1+ should not be a valid formula");
        assertTrue(SCell.isForm("=A1+(B2*2)"), "=A1+(B2*2) should be a valid formula");
    }
    @Test
    public void testComputeForm() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "123");
        sheet.set(1, 0, "=A1+10");
        sheet.set(2, 0, "=A1+B1");

        assertEquals("133", sheet.computeForm("=A1+10"), "Formula should evaluate correctly");
        assertEquals("256", sheet.computeForm("=A1+B1"), "Formula with references should evaluate correctly");
        assertEquals("#ERROR3", sheet.computeForm("=INVALID"), "Invalid formula should return an error");
    }



}
