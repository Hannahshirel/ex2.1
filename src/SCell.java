// Add your documentation below:

import static jdk.internal.org.jline.utils.Colors.s;

public class SCell implements Cell {
    private String line;// donne dans la cellule ("123" ou "=A1+B1")
    private int type;// type de la cellule (nombre , texte , formule , erreur)
    private int order; // ordre de calcul
    private String data;

    public SCell(String s) {
        // Add your code here
    }

    @Override
    public int getOrder() {
        //add your code
        return this.order; // Retourne l'ordre de calcul actuel
    }



    //@Override
    @Override
    public String toString() {

        return getData();
    }

    @Override
    public void setData(String s) {
        this.data = s; // Met à jour les données de la cellule

        // Déterminer le type de la cellule en utilisant les méthodes appropriées
        if (isNumber(s)) {
            this.type = Ex2Utils.NUMBER ; // Définit le type comme IS_NUMBER
        } else if (isForm(s)) {
            this.type = Ex2Utils.FORM ; // Définit le type comme FORM
        } else if (isText(s)) {
            this.type = Ex2Utils.TEXT; // Définit le type comme TEXT
        } else {
            this.type = Ex2Utils.ERR_FORM_FORMAT; // Définit le type en cas d'erreur (par exemple, si c'est vide)
        }
    }


    @Override
    public String getData() {
        this.line = String.valueOf(s);
        if (isNumber(String.valueOf(s))) {
            setType(Ex2Utils.NUMBER);
        } else if (isText(String.valueOf(s))) {
            setType(Ex2Utils.TEXT);
        } else if (isForm(String.valueOf(s))) {
            setType(Ex2Utils.FORM);
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT); // Si aucun type ne correspond
        }
        return line;
    }

    @Override
    public int getType() {

        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        // Add your code here

    }

    // ne pas changer le prof a dit c bon isNumber, isText
    public boolean isNumber(String text) {// je verifie sir une chaine de caracter text represente un nombre valid
        try {
            Double.parseDouble(text); // essaye  de convertir text en un nombre
            return true;// true si sa marche , c un nombre
        } catch (NumberFormatException e) {
            return false;// si sa marche pas, c pas un nombre
        }
    }

    public boolean isText(String text) {// sa verifie si la chaine est ni un nombre ni une formule , mais un texte
        return !isNumber(text) && !text.startsWith("=");// return true si la chaine nest pas un nombre (isNumber(text) est faux
        // && si elle ne commence pas par un "=" cad nest pas une formule
    }

    // refaire le isForm avec boucle true et false ( si je suis vide dans des parenthese , vide ou null , : soit vrai soit faux directemtn )
//verifie si la chaine est une formule vrai
    public boolean isForm(String text) {
        if (text == null || text.isEmpty()) { // chaine nul ou vide
            return false;
        }
        if (text.startsWith("=")) { // commence par = alors c true
            return true;
        }
        if (!text.startsWith("=")) { // commence pas par = alors c false
            return false;
        }
        String formulaContent = text.substring(1);
        char[] chars = formulaContent.toCharArray();// nous aide a verifie chaque caracter 1 a 1
        int openParens = 0;
        for (char c : chars) {
            if (Character.isDigit(c) || c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {// tous les caracteres autorise dans la formule
                if (c == '(') {
                    openParens++;
                } else if (c == ')') {
                    openParens--;
                }
                if (openParens < 0) { // compte si le nombre de parenthese est bon des deux cotee
                    return false;
                } else {
                    return true;
                }
            }
        }
        return openParens == 0;
    }

    public Double computeForm(String form) {
        if(form == null || form.isEmpty() || !isNumber(form)) {
            System.out.println("invalid form: "+form);
        }
        String formulaContent = form.substring(1);//retire le = pour ne garder que la formule
        char[] chars = formulaContent.toCharArray();//converti le tout en tableau de character
        double result = 0;// resultat temporaire
        double currentNumber = 0;
        char currentOp = '+';
        for (int i = 0; i<chars.length;i++){
            char c = chars[i];
            if(Character.isDigit(c)) {
                currentNumber = currentNumber*10 + (c- '0');
            }
            if ((!Character.isDigit(c) && c != ' ') || i== chars.length -1){
        switch (currentOp){
            case '+':
                result += currentNumber;
                break;
                case '-':
                    result -= currentNumber;
                    break;
                    case '*':
                        result *= currentNumber;
                        break;
                        case '/':
                            if (currentNumber ==0 ){// verfie si le nmb est 0 pour ne pas diviser par 0
                                throw new ArithmeticException("Division by zero");
                            }
                            result /= currentNumber;
                            break;
        }
        currentOp = c; // met a jour loperateur
        currentNumber = 0; // reinitialise le nombre
            }
        }
        return result;
    }
}

