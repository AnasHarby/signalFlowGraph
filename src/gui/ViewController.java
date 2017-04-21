package gui;

import com.jfoenix.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
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

    @FXML // fx:id="logger"
    private JFXTextArea logger; // Value injected by FXMLLoader

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane dialogParent;

    private Graph graph;
    private Sfg sfg;

    public ViewController() {
        this.sfg = new Sfg();
        this.graph = new SingleGraph("SFG");
        this.graph.addAttribute("ui.stylesheet", "url('" + this
                .getClass().getClassLoader().getResource("gui/graph.css") + "')");
        this.graph.addAttribute("ui.quality");
        this.graph.addAttribute("ui.antialias");
    }

    @FXML
    void addEdge(final ActionEvent event) {
        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        JFXDialog jfxDialog = new JFXDialog(this.dialogParent, jfxDialogLayout,
                JFXDialog.DialogTransition.CENTER);

        Text text = new Text("Add Edge\n");
        text.getStyleClass().add("fancyText");
        jfxDialogLayout.setHeading(text);

        JFXTextField srcNodeTf = createTextField("Source");
        srcNodeTf.getStyleClass().add("dialog-textField");
        JFXTextField destNodeTf = createTextField("Destination");
        destNodeTf.getStyleClass().add("dialog-textField");
        JFXTextField gainTf = createTextField("Gain");
        gainTf.getStyleClass().add("dialog-textField");

        VBox vbox = new VBox();
        vbox.setSpacing(15d);
        vbox.getChildren().add(srcNodeTf);
        vbox.getChildren().add(destNodeTf);
        vbox.getChildren().add(gainTf);
        jfxDialogLayout.setBody(vbox);

        JFXButton addButton = new JFXButton("Add");
        addButton.setRipplerFill(Paint.valueOf("#e0f2f1"));
        addButton.getStyleClass().add("dialog-button");
        addButton.setDisable(true);
        addButton.setDefaultButton(true);
        addButton.setOnAction(e -> {
            addEdgeUtil(srcNodeTf.getText(), destNodeTf.getText(), Double
                    .parseDouble(gainTf.getText()));
            jfxDialog.close();
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setRipplerFill(Paint.valueOf("#4286f4"));
        cancelButton.getStyleClass().add("dialog-cancel-button");
        cancelButton.setOnAction(e -> jfxDialog.close());

        HBox hbox = new HBox();
        hbox.setSpacing(15d);
        hbox.getChildren().add(addButton);
        hbox.getChildren().add(cancelButton);
        jfxDialogLayout.setActions(hbox);

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

        jfxDialog.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE)
                jfxDialog.close();
        });

        jfxDialog.setOnDialogOpened(e -> srcNodeTf.requestFocus());
        jfxDialog.show();
    }

    private void addEdgeUtil(final String src, final String dest, double gain) {
        Edge edge = this.graph.addEdge(src + dest, src, dest, true);
        edge.addAttribute("ui.label", Double.toString(gain));
        this.sfg.addEdges(new Sfg.Edge(this.sfg.getNode(src),
                this.sfg.getNode(dest), gain));
    }

    @FXML
    void addNode(final ActionEvent event) {
        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        JFXDialog jfxDialog = new JFXDialog(this.dialogParent, jfxDialogLayout,
                JFXDialog.DialogTransition.CENTER);

        Text text = new Text("Add Node\n");
        text.getStyleClass().add("fancyText");
        jfxDialogLayout.setHeading(text);

        JFXTextField labelTf = createTextField("Label");
        labelTf.getStyleClass().add("dialog-textField");
        jfxDialogLayout.setBody(labelTf);

        JFXButton addButton = new JFXButton("Add");
        addButton.setRipplerFill(Paint.valueOf("#e0f2f1"));
        addButton.getStyleClass().add("dialog-button");
        addButton.setDisable(true);
        addButton.setDefaultButton(true);
        addButton.setOnAction(e -> {
            addNodeUtil(labelTf.getText());
            jfxDialog.close();
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setRipplerFill(Paint.valueOf("#4286f4"));
        cancelButton.getStyleClass().add("dialog-cancel-button");
        cancelButton.setOnAction(e -> jfxDialog.close());

        HBox hbox = new HBox();
        hbox.setSpacing(15d);
        hbox.getChildren().add(addButton);
        hbox.getChildren().add(cancelButton);
        jfxDialogLayout.setActions(hbox);

        labelTf.textProperty().addListener((ObservableValue<?
                extends String> observable, String oldValue, String newValue) -> {
            if (isValidLabel(newValue) && !nodeExists(newValue))
                addButton.setDisable(false);
            else {
                addButton.setDisable(true);
            }
        });

        jfxDialog.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE)
                jfxDialog.close();
        });

        jfxDialog.setOnDialogOpened(e -> labelTf.requestFocus());
        jfxDialog.show();
    }

    private void addNodeUtil(final String label) {
        Node n = graph.addNode(label);
        n.addAttribute("ui.label", label);
        n.addAttribute("ui.class", "big");
        sfg.addNodes(new Sfg.Node(label));
    }

    @FXML
    void solveGraph(final ActionEvent event) {
        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        JFXDialog jfxDialog = new JFXDialog(this.dialogParent, jfxDialogLayout,
                JFXDialog.DialogTransition.CENTER);

        Text text = new Text("Solve\n");
        text.getStyleClass().add("fancyText");
        jfxDialogLayout.setHeading(text);

        JFXTextField startTF = createTextField("Start");
        startTF.getStyleClass().add("dialog-textField");
        JFXTextField endTF = createTextField("End");
        endTF.getStyleClass().add("dialog-textField");

        VBox vbox = new VBox();
        vbox.setSpacing(15d);
        vbox.getChildren().add(startTF);
        vbox.getChildren().add(endTF);
        jfxDialogLayout.setBody(vbox);

        JFXButton addButton = new JFXButton("Solve");
        addButton.setRipplerFill(Paint.valueOf("#e0f2f1"));
        addButton.getStyleClass().add("dialog-button");
        addButton.setDisable(true);
        addButton.setDefaultButton(true);
        addButton.setOnAction(e -> {
            solveGraphUtil(startTF.getText(), endTF.getText());
            jfxDialog.close();
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setRipplerFill(Paint.valueOf("#4286f4"));
        cancelButton.getStyleClass().add("dialog-cancel-button");
        cancelButton.setOnAction(e -> jfxDialog.close());

        HBox hbox = new HBox();
        hbox.setSpacing(15d);
        hbox.getChildren().add(addButton);
        hbox.getChildren().add(cancelButton);
        jfxDialogLayout.setActions(hbox);

        startTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (this.isValidLabel(newValue) && this.nodeExists(newValue) &&
                    this.isValidLabel(endTF.getText()) &&
                    this.nodeExists(endTF.getText()))
                addButton.setDisable(false);
            else
                addButton.setDisable(true);
        });

        endTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (this.isValidLabel(newValue) && this.nodeExists(newValue) &&
                    this.isValidLabel(startTF.getText()) &&
                    this.nodeExists(startTF.getText()))
                addButton.setDisable(false);
            else
                addButton.setDisable(true);
        });

        jfxDialog.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE)
                jfxDialog.close();
        });

        jfxDialog.setOnDialogOpened(e -> startTF.requestFocus());
        jfxDialog.show();
    }

    private void solveGraphUtil(final String start, final String end) {
        Sfg.SfgMetadata metadata = this.sfg.solve(
                this.sfg.getNode(start), this.sfg.getNode(end));
        metadata.getForwardPaths().get(0).addNodes(new Sfg.Node("S"));
        logger.appendText("Result = " + metadata.getResult() + "\n");
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert canvas != null;
        assert logger != null;

        this.logger.textProperty().addListener(e -> logger.setScrollTop(Double.MAX_VALUE));

        this.initCanvas();
    }

    private void initCanvas() {
        //A must-set property to apply css tp the graph.
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
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

