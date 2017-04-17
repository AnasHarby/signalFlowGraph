package gui;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.DoubleValidator;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Sample Skeleton for 'sample.fxml' Controller Class
 */

public class Controller {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="canvas"
    private Canvas canvas; // Value injected by FXMLLoader

    @FXML // fx:id="gainTF"
    private JFXTextField gainTF; // Value injected by FXMLLoader

    @FXML // fx:id="logger"
    private JFXTextArea logger; // Value injected by FXMLLoader

    @FXML
    private ScrollPane scrollPane;

    @FXML
    void addEdge(MouseEvent event) {

    }

    @FXML
    void addNode(MouseEvent event) {

    }

    @FXML
    void getResult(MouseEvent event) {
        this.logger.appendText("Test Scroll!\n");
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert canvas != null;
        assert gainTF != null;
        assert logger != null;

        DoubleValidator dv = new DoubleValidator();
        this.gainTF.getValidators().add(dv);
        dv.setMessage("Double value!");

        this.gainTF.focusedProperty().addListener((e, old, newVal) -> {
            if (!newVal) this.gainTF.validate();
        });

        this.logger.textProperty().addListener(e -> logger.setScrollTop(Double.MAX_VALUE));
    }
}

