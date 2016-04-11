package app.Presenter;

import app.View.GuessScoresView;
import javax.swing.JOptionPane;

public class RootPanelPresenter extends javax.swing.JFrame {
    public void btnExitActionPerformed(java.awt.event.ActionEvent evt) {                                        
        this.dispose();
    }                                       
    public void btnGuessScoresActionPerformed(java.awt.event.ActionEvent evt) {                                               
        new GuessScoresView().setVisible(true);
        this.dispose();
    }    
    public void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "Created by Jonathan Wang, Jorge Maldonado, and Henry Trinh."
                + "\nCSCE-315-506");
    }
    public void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "Press 'Guess Scores' button to begin."
                + "\nSelect Season from ComboBox and Week #. Then press the Go button."
                + "\nYou can then select which team you think will win. (Please select all teams before pressing submit)"
                + "\nDepending on what Service Provider/Implementation you are using, the team expected to win will be underlined."
                + "\nAfter selecting all teams, you can then press submit to show what percentage of teams you guessed correctly.");
    }
}