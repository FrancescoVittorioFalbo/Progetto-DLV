package progettoDLV;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static progettoDLV.Progetto.*;

public class ProgettoGui extends JFrame {
    private JButton CALCOLAButton;
    private JPanel panel1;
    private JTextArea INSIEME_A;
    private JTextArea INSIEME_B;
    private JTextArea RISULTATO;
    private JButton NEWButton;
    private JCheckBox SHOWSOLUTIONIN3CheckBox;
    private String variabile;

    public ProgettoGui() {
        this.setVisible(true);
        this.setContentPane(panel1);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setTitle("GENERALIZED 3-CNF CONSISTENCY");
        this.setLocationRelativeTo(null);

        RISULTATO.setEditable(false);

        CALCOLAButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println(INSIEME_A.getText());
                leggi(INSIEME_A.getText(), INSIEME_B.getText());
                converti(formuleGrezzeA, regoleA, testeImportantiA, "fm_a");
                converti(formuleGrezzeB, regoleB, testeImportantiB, "fm_b");
                creaFile();
                String output = eseguiFile_catturaOutput();
                if (output == null) {
                    variabile = "Non esiste nessuna formula phi che rispecchia la 3-CNF Consistency!";
                } else variabile = stampaSoluzione(output, SHOWSOLUTIONIN3CheckBox.isSelected());
                RISULTATO.setText("");
                RISULTATO.append(variabile);
                pulisci();
                JOptionPane.showMessageDialog(panel1, "E' stato creato un File nominato: " + nome_programma + ".\n" +
                        "Esso contiene il programma DLV che è stato generato appositamente per gli insiemi A e B dati");
            }
        });

        NEWButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                INSIEME_A.setText("");
                INSIEME_B.setText("");
                RISULTATO.setText("");
                pulisci();
            }
        });
    }
}
