package gui;

import com.jfoenix.controls.*;
import com.jfoenix.validation.DoubleValidator;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Sample Skeleton for 'sample.fxml' ViewController Class
 */

public class ViewController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="canvas"
    private StackPane canvas; // Value injected by FXMLLoader

    @FXML // fx:id="gainTF"
    private JFXTextField gainTF; // Value injected by FXMLLoader

    @FXML // fx:id="logger"
    private JFXTextArea logger; // Value injected by FXMLLoader

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane dialogParent;

    private Graph graph;

    public ViewController() {
        this.graph = new SingleGraph("SFG");
    }

    @FXML
    void addEdge(MouseEvent event) {

    }

    @FXML
    void addNode(ActionEvent event) {
        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        JFXDialog jfxDialog = new JFXDialog(this.dialogParent, jfxDialogLayout, JFXDialog.DialogTransition.CENTER);

        jfxDialogLayout.setHeading(new Text("Add Node\n"));

        JFXTextField jfxTextField = new JFXTextField();
        jfxTextField.setPromptText("Label");
        jfxTextField.setLabelFloat(true);

        jfxDialogLayout.setBody(jfxTextField);

        JFXButton addButton = new JFXButton("Add");
        addButton.setDisable(true);
        addButton.setOnAction(e -> jfxDialog.close());


        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setOnAction(e -> jfxDialog.close());

        jfxDialogLayout.setActions(addButton, cancelButton);

        jfxTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (isValidNodeLabel(newValue))
                addButton.setDisable(false);
            else
                addButton.setDisable(true);
        });

        jfxDialog.show();
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

        this.initCanvas();
    }

    private void initCanvas() {
        //A must-set property to apply css tp the graph.
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        ViewPanel view = viewer.addDefaultView(false);
        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);
        SwingNode node = new SwingNode();
        node.setContent(view);
        this.canvas.getChildren().add(node);
    }

    private boolean isValidNodeLabel(final String label) {
        return !label.isEmpty() && label.matches("[a-zA-Z_]\\w*");
    }
}

