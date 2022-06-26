/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import it.polito.tdp.yelp.model.Review;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnMiglioramento"
    private Button btnMiglioramento; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    @FXML
    void doRiempiLocali(ActionEvent event) {
    	this.cmbLocale.getItems().clear();
    	String citta = this.cmbCitta.getValue();
    	if(citta != null) {
    		List<Business> temp = this.model.getBusinessesCity(citta);
    		Collections.sort(temp);
    		this.cmbLocale.getItems().addAll(temp);
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	//controllo sugli input
    	String citta = this.cmbCitta.getValue();
    	if(citta == null) {
    		this.txtResult.setText("Devi prima selezionare una città");
    		return;
    	}
    	Business locale = this.cmbLocale.getValue();
    	if(locale == null) {
    		this.txtResult.setText("Devi prima selezionare un locale");
    		return;
    	}
    	//se sono qui posso proseguire con la creazione del grafo
    	this.model.creaGrafo(citta, locale);
    	this.txtResult.setText("Grafo creato: "+this.model.nVertices()+" vertici, "+this.model.nArchi()+" archi.\n\n");
    	//trovare vertice/i per cui numero di archi uscenti è massimo
    	List<Review> temp = this.model.getMaxDegreeOf();
    	for(Review r : temp) {
    		this.txtResult.appendText(r.getReviewId()+ "     #ARCHI USCENTI: "+this.model.maxDegree());
    	}
    }

    @FXML
    void doTrovaMiglioramento(ActionEvent event) {
    	this.txtResult.clear();
    	String citta = this.cmbCitta.getValue();
    	if(citta == null) {
    		this.txtResult.setText("Devi prima selezionare una città");
    		return;
    	}
    	Business locale = this.cmbLocale.getValue();
    	if(locale == null) {
    		this.txtResult.setText("Devi prima selezionare un locale");
    		return;
    	}
    	if(!this.model.isGraphCreated()) {
    		this.txtResult.setText("Devi prima creare il grafo");
    		return;
    	}
    	//se sono qui posso proseguire con la ricorsione
    	List<Review> percorso = this.model.cercaMiglioramento();
    	if(percorso.size() == 0) {
    		this.txtResult.setText("Non è stato trovato alcun percorso");
    	}
    	this.txtResult.setText("Percorso trovato: \n");
    	for(Review r : percorso) {
    		this.txtResult.appendText(r.getReviewId()+" - "+r.getDate()+"\n");
    	}
    	this.txtResult.appendText("Numero giorni coperti: "+this.model.getDiffGiorni()+"\n");
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnMiglioramento != null : "fx:id=\"btnMiglioramento\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbCitta.getItems().clear();
    	this.cmbCitta.getItems().addAll(this.model.getAllCities());
    }
}
