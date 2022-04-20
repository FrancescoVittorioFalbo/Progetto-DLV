package progettoDLV;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Progetto {

    //STATIC-FINAL
    public static final String nome_programma = "DLV_script.txt";
    private static final Logger logger = Logger.getGlobal();
    //FASE 1
    public static Set<String> var = new HashSet<>();
    //FASE 2
    public static LinkedList<String> formuleGrezzeA = new LinkedList<>();
    public static LinkedList<String> formuleGrezzeB = new LinkedList<>();
    //FASE 3
    public static LinkedList<String> regoleA = new LinkedList<>();
    public static LinkedList<String> regoleB = new LinkedList<>();
    public static LinkedList<String> testeImportantiA = new LinkedList<>();
    public static LinkedList<String> testeImportantiB = new LinkedList<>();

    public static void leggi(String formulaA, String formulaB) {
        String fa, fb;
        Scanner sc = new Scanner(formulaA);
        while (sc.hasNextLine()) {
            fa = sc.nextLine();
            StringTokenizer st = new StringTokenizer(fa, ",");

            while (st.hasMoreTokens()) {
                String tk = st.nextToken().trim().toLowerCase();
                if (!tk.equals(""))
                    formuleGrezzeA.add(tk);
            }

        }
        sc = new Scanner(formulaB);
        while (sc.hasNextLine()) {
            fb = sc.nextLine();
            StringTokenizer st2 = new StringTokenizer(fb, ",");
            while (st2.hasMoreTokens()) {
                String tk = st2.nextToken().trim().toLowerCase();
                if (!tk.equals(""))
                    formuleGrezzeB.add(tk);
            }
        }
        sc.close();
    }

    public static void converti(LinkedList<String> formuleGrezze, LinkedList<String> regole, LinkedList<String> testeImportanti, String nome) {
        int cont = 0;
        for (String formula : formuleGrezze) {

            //aggiunge le variabili al SET
            StringTokenizer st = new StringTokenizer(formula, " ()");
            while (st.hasMoreTokens()) {
                String variabile = st.nextToken();
                if (!variabile.equals("and") && !variabile.equals("or") && !variabile.equals("not"))
                    var.add(variabile);
            }

            int aperte = -1;
            int chiuse = -1;

            for (int i = 0; i < formula.length(); i++) {
                if (formula.charAt(i) == '(') aperte = i;
                else if (formula.charAt(i) == ')' && chiuse == -1) chiuse = i;
            }

            int name = 0;
            while (aperte != -1) {

                int initIndex = aperte;
                int endIndex = chiuse;

                String f = formula.substring(initIndex + 1, endIndex);
                creaFormula(nome + cont + name, f, regole);

                f = formula.substring(0, initIndex) + nome + cont + name + formula.substring(endIndex + 1);

                formula = f;

                aperte = -1;
                chiuse = -1;

                for (int i = 0; i < formula.length() && (aperte == -1 || chiuse == -1); i++) {
                    if (formula.charAt(i) == '(') aperte = i;
                    else if (formula.charAt(i) == ')' && chiuse == -1) chiuse = i;
                }
                name++;
            }
            creaFormula(nome + cont, formula, regole);
            testeImportanti.add(nome + cont);
            cont++;
        }
    }

    private static void creaFormula(String testa, String corpo, LinkedList<String> regole) {
        String[] vs = corpo.split("or");
        Arrays.stream(vs)
                .map(x -> testa + " :- " + x.trim().replaceAll(" and", ",") + ".")
                .forEach(regole::add);
    }

    public static void creaFile() {
        StringBuilder sb = new StringBuilder();

        //FASE 1 : Guess Variabili
        for (String variabile : var)
            sb.append(variabile).append(" | ").append("n").append(variabile).append(".\n");

        //FASE 2 : Predicati per ogni gruppo di regole
        for (String predicate : regoleA)
            sb.append(predicate).append("\n");
        for (String predicate : regoleB)
            sb.append(predicate).append("\n");

        //FASE 3 : Regola per esistenza phi
        sb.append("esiste :- ");
        for (String testa : testeImportantiA)
            sb.append(testa).append(", ");
        for (int i = 0; i < testeImportantiB.size(); i++)
            if (i != testeImportantiB.size() - 1)
                sb.append("not ").append(testeImportantiB.get(i)).append(", ");
            else
                sb.append("not ").append(testeImportantiB.get(i)).append(".\n");
        sb.append(":- not esiste.\n");

        //FASE 4 : Programma Finito, costruisco il File
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(nome_programma));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String eseguiFile_catturaOutput() {
        Runtime rt = Runtime.getRuntime();
        String RESULT = "";

        String command = "";

        switch (OSValidator.getOS()) {
            case "win":
                command = "dlv.mingw.exe " + nome_programma;
                break;
            case "uni":
                command = "./dlv.x86-64-linux-elf-unixodbc.bin " + nome_programma;
                break;
            case "osx":
                command = "./dlv.i386-apple-darwin.bin " + nome_programma;
                break;
        }

        logger.log(Level.INFO, "comando: " + command + "\n");

        try {
            Process pr = rt.exec(command);
            BufferedReader bri = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            bri.readLine();
            bri.readLine();   // servono per non leggere le prime due righe inutili!
            RESULT = bri.readLine();       //basta una soluzione, la prima che si trova va bene!
            bri.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Answer Set: " + RESULT + "\n");
        return RESULT;
    }

    public static String stampaSoluzione(String output, boolean cnf) {


        int numVar = var.size();
        //Esempio di Output : {na, b, c, a1, a2, a31, a3, esiste}

        //prendo i letterali positivi o negativi (se c'è la "n" prima)
        List<String> interpretazione = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(output, ",{} ");
        for (int i = 0; st.hasMoreTokens() && i < numVar; i++) {
            interpretazione.add(st.nextToken());
        }
        logger.log(Level.INFO, "INTERPRETAZIONE: " + interpretazione);
        StringBuilder sb = new StringBuilder();

        sb.append(valori(interpretazione));

        sb.append("FORMULA: ");

        if (cnf)
            for (int i = 0; i < numVar; i++) {
                sb.append("( ");
                for (int j = 0; j < 3; j++) {
                    String x = interpretazione.get(i);

                    if (x.charAt(0) == 'n')
                        if (j < 2) sb.append("not ").append(x.substring(1)).append(" OR ");
                        else sb.append("not ").append(x.substring(1));
                    else if (j < 2) sb.append(x).append(" OR ");
                    else sb.append(x);
                }

                if (i == numVar - 1) sb.append(")");
                else sb.append(") AND ");
            }
        else {
            sb.append("(");
            for (int i = 0; i < numVar; i++) {
                String x = interpretazione.get(i);
                if (x.charAt(0) == 'n')
                    if (i != numVar - 1) sb.append("not ").append(x.substring(1)).append(" AND ");
                    else sb.append("not ").append(x.substring(1));
                else if (i != numVar - 1) sb.append(x).append(" AND ");
                else sb.append(x);
            }
            sb.append(")");
        }
        return sb.toString();
    }

    private static String valori(List<String> interpretazione) {
        StringBuilder sb = new StringBuilder();
        sb.append("ECCO UNA POSSIBILE INTERPRETAZIONE:\n");

        for (String s : interpretazione) {
            if (s.charAt(0) != 'n') sb.append("    ").append(s).append(": TRUE\n");
            else sb.append("    ").append(s.substring(1)).append(": FALSE\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    public static void pulisci() {
        formuleGrezzeA.clear();
        regoleA.clear();
        testeImportantiA.clear();
        formuleGrezzeB.clear();
        regoleB.clear();
        testeImportantiB.clear();
        var.clear();
    }

    public static void main(String[] args) {
        logger.setLevel(Level.OFF);
        String OS = OSValidator.getOS();
        logger.log(Level.INFO, OS);

        new ProgettoGui();
    }
}
/*
        d1 | nd1.
        d2 | nd2.
        d3 | nd3.

        a1 = d1 or d2
        a2 = d1 or (d2 and d3)
        a3 = d2 and (d1 or d3)

        a1 :- d1.
        a1 :- d2.

        a2 :- d2, d3.
        a2 :- d1.

        a31 :- d1.
        a31 :- d3.
        a3 :-  d2, a31.

        ins2 :- d1.

        esiste :- a1, a2, a3, not ins2.
        :- not esiste.
*/
