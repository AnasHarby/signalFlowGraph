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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import sfg.Sfg;

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
    private Sfg sfg;

    public ViewController() {
        this.graph = new SingleGraph("SFG");
        this.sfg = new Sfg();
    }

    @FXML
    void addEdge(final MouseEvent event) {
        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        JFXDialog jfxDialog = new JFXDialog(this.dialogParent, jfxDialogLayout,
                JFXDialog.DialogTransition.CENTER);
        jfxDialogLayout.setHeading(new Text("Add Edge\n"));

        JFXTextField srcNodeTf = createTextField("Source");
        JFXTextField destNodeTf = createTextField("Destination");
        JFXTextField gainTf = createTextField("Gain");

        VBox vbox = new VBox();
        vbox.getChildren().add(srcNodeTf);
        vbox.getChildren().add(destNodeTf);
        vbox.getChildren().add(gainTf);
        jfxDialogLayout.setBody(vbox);

        JFXButton addButton = new JFXButton("Add");
        addButton.setDisable(true);
        addButton.setOnAction(e -> {
            addEdgeUtil(srcNodeTf.getText(), destNodeTf.getText(), Double
                    .parseDouble(gainTf.getText()));
            jfxDialog.close();
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setOnAction(e -> jfxDialog.close());

        jfxDialogLayout.setActions(addButton, cancelButton);

        srcNodeTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEdge(newValue, destNodeTf.getText(), gainTf.getText()))
                addButton.setDisable(false);
            else
                addButton.setDisable(true);
        });

        destNodeTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEdge(srcNodeTf.getText(), newValue, gainTf.getText()))
                addButton.setDisable(false);
            else
                addButton.setDisable(true);
        });

        gainTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEdge(srcNodeTf.getText(), destNodeTf.getText(), newValue))
                addButton.setDisable(false);
            else
                addButton.setDisable(true);
        });

        jfxDialog.show();
    }

    private void addEdgeUtil(final String src, final String dest, double gain) {
        this.graph.addEdge(src + dest, src, dest, true);
        this.sfg.addEdge(src, dest, gain);
    }

    @FXML
    void addNode(final ActionEvent event) {
        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        JFXDialog jfxDialog = new JFXDialog(this.dialogParent, jfxDialogLayout,
                JFXDialog.DialogTransition.CENTER);
        jfxDialogLayout.setHeading(new Text("Add Node\n"));

        JFXTextField labelTf = createTextField("Label");
        jfxDialogLayout.setBody(labelTf);

        JFXButton addButton = new JFXButton("Add");
        addButton.setDisable(true);
        addButton.setOnAction(e -> {
            addNodeUtil(labelTf.getText());
            jfxDialog.close();
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setOnAction(e -> jfxDialog.close());

        jfxDialogLayout.setActions(addButton, cancelButton);

        labelTf.textProperty().addListener((ObservableValue<?
                extends String> observable, String oldValue, String newValue) -> {
            if (isValidLabel(newValue) && !nodeExists(newValue))
                addButton.setDisable(false);
            else {
                addButton.setDisable(true);
            }
        });

        jfxDialog.show();
        labelTf.requestFocus();
    }

    private void addNodeUtil(final String label) {
        graph.addNode(label);
        sfg.addNodes(new Sfg.Node(label));
    }

    @FXML
    void getResult(final MouseEvent event) {
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

    private JFXTextField createTextField(final String prompt) {
        JFXTextField jfxTextField = new JFXTextField();
        jfxTextField.setPromptText(prompt);
        jfxTextField.setLabelFloat(true);
        return jfxTextField;
    }

    private boolean isValidLabel(final String label) {
        return !label.isEmpty() && label.matches("[a-zA-Z_]\\w*");
    }

    private boolean nodeExists(final String label) {
        return this.sfg.getNode(label) != null;
    }

    private boolean isValidEdge(final String src, final String dest,
                                final String gain) {
        return isValidLabel(src) && isValidLabel(dest) && nodeExists(src) &&
                nodeExists(dest) && !gain.isEmpty() &&
                gain.matches("^[+-]?\\d*\\.?\\d*");
    }
}
