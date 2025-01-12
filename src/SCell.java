
// Add your documentation below:

public class SCell implements Cell {
    private String line;// donne dans la cellule ("123" ou "=A1+B1")
    private int type;// type de la cellule (nombre , texte , formule , erreur)
    private int order; // ordre de calcul
    private String data;

    public SCell(String s) {
        // Add your code here
        setData(s);
        if (isNumber(s)) {
            setType(Ex2Utils.NUMBER); // Si c'est un nombre.
        } else if (isForm(s)) {
            setType(Ex2Utils.FORM); // Si c'est une formule valide.
        } else if (isText(s)) {
            setType(Ex2Utils.TEXT); // Si c'est du texte.
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT); // Si c'est une erreur.
        }
    }


    @Override
    public int getOrder() {
        //add your code
        return this.order; // Retourne l'ordre de calcul actuel
    }



    @Override
    public String toString() {
        return getData();
    }



    @Override
    public void setData(String s) {
        line = s; // Met à jour la ligne
        // Déterminer le type de la cellule en utilisant les méthodes appropriées
        if (isNumber(s)) {
            setType( Ex2Utils.NUMBER ); // Définit le type comme IS_NUMBER
        } else if (isForm(s)) {
            System.out.println("INSIDE FORM SECTION");
            System.out.println("FORM= " + Ex2Utils.FORM);
            setType(Ex2Utils.FORM) ; // Définit le type comme FORM
        } else if (isText(s)) {
            setType(Ex2Utils.TEXT); // Définit le type comme TEXT
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT); // Définit le type en cas d'erreur (par exemple, si c'est vide)
        }
    }


    @Override
    public String getData() {
        return line;
        //add your code
    }



    @Override
    public int getType() {

        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
        if (type == Ex2Utils.NUMBER) {
            order = Ex2Utils.NUMBER;
        }
        if (type == Ex2Utils.FORM) {
            order = Ex2Utils.FORM;
        }
        if (type == Ex2Utils.TEXT) {
            order = Ex2Utils.TEXT;
        }
        if (type == Ex2Utils.ERR_FORM_FORMAT) {
            order = Ex2Utils.ERR_FORM_FORMAT;
        }
    }

    @Override
    public void setOrder(int t) {
        // Add your code here
        this.order = t;

    }

    // ne pas changer le prof a dit c bon isNumber, isText
    public boolean isNumber(String text) {// je verifie sir une chaine de caracter text represente un nombre valid
        // Vérifie si la chaîne est null ou vide
        if (text == null || text.trim().isEmpty()) {
            return false; // Retourne false si c'est null ou vide
        }
        try {
            Double.parseDouble(text); // essaye  de convertir text en un nombre
            return true;// true si sa marche , c un nombre
        } catch (NumberFormatException e) {
            return false;// si sa marche pas, c pas un nombre
        }
    }

    public boolean isText(String text) {
        // Une chaîne vide ou null est considérée comme un texte (dépend du contexte attendu)
        if (text == null || text.trim().isEmpty()) {
            return false; // Considérez les chaînes vides comme du texte
        }
        // Retourne true si la chaîne n'est ni un nombre ni une formule
        return !isNumber(text) && !text.startsWith("=");
    }


    // refaire le isForm avec boucle true et false ( si je suis vide dans des parenthese , vide ou null , : soit vrai soit faux directemtn )
    public static boolean isForm(String text) {
        // Vérifie si la formule commence par '='
        if (text == null || !text.startsWith("=")) {
            return false;
        }

        // Supprime le '=' pour analyser le reste
        text = text.substring(1);

        // Vérifie si les parenthèses sont équilibrées
        if (!areParenthesesBalanced(text)) {
            return false;
        }

        // Vérifie les opérateurs et la syntaxe générale
        if (!isValidSyntax(text)) {
            return false;
        }

        return true;
    }

    // Vérifie si les parenthèses sont équilibrées
    private static boolean areParenthesesBalanced(String formula) {
        int count = 0;

        for (char c : formula.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
                if (count < 0) {
                    return false; // Une parenthèse fermante avant une ouvrante
                }
            }
        }

        return count == 0;
    }

    // Vérifie la validité des opérateurs et de la syntaxe générale
    private static boolean isValidSyntax(String formula) {
        // Vérifie qu'il n'y a pas deux opérateurs consécutifs
        String[] invalidPatterns = { "\\+\\+", "--", "\\*\\*", "//", "\\+\\-", "\\-\\+", "\\+\\*", "\\*\\+", "/\\+", "\\+/"};
        for (String pattern : invalidPatterns) {
            if (formula.contains(pattern)) {
                return false;
            }
        }

        // Vérifie qu'il n'y a pas d'opérateur au début ou à la fin
        if (formula.matches("^[+\\-*/].*") || formula.matches(".*[+\\-*/]$")) {
            return false;
        }

        return true;
    }
}




