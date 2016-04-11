package app.Presenter;

import app.Model.GuessScoresModel;
import app.View.RootPanelView;
import java.io.FileNotFoundException;

public class GuessScoresPresenter extends GuessScoresModel{
    public void btnGoActionPerformed(java.awt.event.ActionEvent evt) {                                      
        initialize();
    }   
    
    public void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {                                          
        calculatePercentage();
    }     
    
    public void btnExitActionPerformed(java.awt.event.ActionEvent evt) throws FileNotFoundException {                                        
        new RootPanelView().setVisible(true);
        this.dispose();
    }   
}
