package gui;

import com.jfoenix.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
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
import sfg.*;

import java.net.URL;
import java.util.List;
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

    @FXML
    private JFXListView outputList;

    private JFXPopup pathsPopup;
    private JFXPopup loopsPopup;

    private Graph graph;
    private Sfg sfg;

    public ViewController() {
        this.sfg = new Sfg();
        this.graph = new SingleGraph("SFG");
        this.graph.addAttribute("ui.stylesheet", "url('" + this
                .getClass().getClassLoader().getResource("gui/graph.css") + "')");
        this.graph.addAttribute("ui.quality");
        this.graph.addAttribute("ui.antialias");

//        this.
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
        this.sfg.addEdges(new sfg.Edge(this.sfg.getNode(src),
                this.sfg.getNode(dest), gain));
    }

    @FXML
    void clearGraph(final ActionEvent event) {
        this.graph.getNodeSet().clear();
        this.sfg.clear();
        this.logger.clear();
        this.graph.addAttribute("ui.stylesheet", "url('" + this
                .getClass().getClassLoader().getResource("gui/graph.css") + "')");
        this.graph.addAttribute("ui.quality");
        this.graph.addAttribute("ui.antialias");
        this.outputList.getItems().clear();
        resetGraphColors();
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
        sfg.addNodes(new sfg.Node(label));
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
        SfgMetadata metadata = this.sfg.solve(
                this.sfg.getNode(start), this.sfg.getNode(end));
        this.logger.appendText(printForwardPaths(metadata.getForwardPaths()));
        this.logger.appendText("----------------------------\n");
        this.logger.appendText(printLoops(metadata.getLoops()));
        this.logger.appendText("----------------------------\n");
        this.logger.appendText(printNonTouchingCombinations(metadata.getDelta(),
                metadata.getLoops()));
        this.logger.appendText("----------------------------\n");
        this.logger.appendText("Result = " + metadata.getResult() + "\n");
        this.pathsPopup = this.popUpPaths(metadata.getForwardPaths());
        this.loopsPopup = this.popUpLoops(metadata.getLoops());
        this.pathsPopup.setHideOnEscape(true);
        this.pathsPopup.setAutoHide(true);
        this.pathsPopup.setConsumeAutoHidingEvents(true);
        this.loopsPopup.setHideOnEscape(true);
        this.loopsPopup.setAutoHide(true);
        this.loopsPopup.setConsumeAutoHidingEvents(true);
        this.initViewList();
    }

    private String printForwardPaths(final List<Path> forwardPaths) {
        StringBuilder log = new StringBuilder();
        for (int i = 0; i < forwardPaths.size(); i++) {
            log.append("Path\t" + (i + 1) + ": ");
            for (sfg.Node node : forwardPaths.get(i).getNodeList())
                log.append(node.getLabel() + " ");
            log.append("\n");
        }
        return log.toString();
    }

    private String printLoops(final List<Path> loops) {
        StringBuilder log = new StringBuilder();
        for (int i = 0; i < loops.size(); i++) {
            log.append("Loop " + (i + 1) + ": ");
            for (sfg.Node node : loops.get(i).getNodeList())
                log.append(node.getLabel() + " ");
            log.append("\n");
        }
        return log.toString();
    }

    private JFXPopup popUpLoops(final List<Path> loops) {
        JFXPopup loopsPop = new JFXPopup();
        VBox vBox = new VBox();
        for (int i = 0; i < loops.size(); i++) {
            JFXButton button = new JFXButton("Loop " + (i + 1));
            button.setPadding(new Insets(10));
            final int I = i;
            button.setOnAction(e -> {
                colorizeGraphNodes(loops.get(I).getNodeList(), "loop");
                loopsPop.hide();
            });
            vBox.getChildren().add(button);
        }
        loopsPop.setPopupContent(vBox);
        return loopsPop;
    }

    private JFXPopup popUpPaths(final List<Path> forwardPaths) {
        JFXPopup paths = new JFXPopup();
        VBox vBox = new VBox();
        for (int i = 0; i < forwardPaths.size(); i++) {
            JFXButton button = new JFXButton("Path " + (i + 1));
            button.setPadding(new Insets(10));
            final int I = i;
            button.setOnAction(e -> {
                colorizeGraphNodes(forwardPaths.get(I).getNodeList(), "path");
                paths.hide();
            });
            vBox.getChildren().add(button);
        }
        paths.setPopupContent(vBox);
        return paths;
    }

    private void initViewList() {
        Label paths = new Label("Paths");
        Label loops = new Label("Loops");
        this.outputList.getItems().addAll(paths, loops);
        this.outputList.setOnMouseClicked(e -> {
            if (outputList.getSelectionModel().getSelectedItem() instanceof Label) {
                Label l = (Label) outputList.getSelectionModel().getSelectedItem();
                if (l.getText().equals("Paths"))
                    this.pathsPopup.show(this.outputList, JFXPopup.PopupVPosition
                            .TOP, JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
                else if (l.getText().equals("Loops"))
                    this.loopsPopup.show(this.outputList, JFXPopup.PopupVPosition
                            .TOP, JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
            }
            outputList.getSelectionModel().clearSelection();
        });
    }

    private String printNonTouchingCombinations(final Delta delta,
                                                final List<Path> loops) {
        StringBuilder log = new StringBuilder();
        for (int k = 0; k < delta.getContainerList().size(); k++) {
            LoopGroupContainer container = delta.getContainerList().get(k);
            log.append("Non touching loops of size " + (k + 1) + ": \n");
            for (LoopGroup loopGroup : container.getGroupList()) {
                log.append("(");
                for (Path loop : loopGroup.getLoopList()) {
                    log.append("L" + (loops.indexOf(loop) + 1) + " ");
                }
                log.deleteCharAt(log.length() - 1);
                log.append(")");
            }
            log.append("\n");
        }
        return log.toString();
    }

    @FXML
    void initialize() {
        assert canvas != null;
        assert logger != null;

        this.logger.textProperty().addListener(e -> logger.setScrollTop(Double.MAX_VALUE));

        this.initCanvas();
        this.outputList.setOnMouseEntered(e -> {
            this.outputList.setExpanded(true);
            this.outputList.depthProperty().setValue(1);
        });
        this.outputList.setOnMouseExited(e -> {
            this.outputList.setExpanded(false);
            this.outputList.depthProperty().setValue(0);
        });

        this.outputList.setCellVerticalMargin(10d);
        this.outputList.setVerticalGap(10d);

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
                gain.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    }

    private void colorizeGraphNodes(final List<sfg.Node> nodeList, final String cssClass) {
        resetGraphColors();
        for (sfg.Node sfgNode : nodeList)
            this.graph.getNode(sfgNode.getLabel()).addAttribute(
                    "ui.class", cssClass);
    }

    private void resetGraphColors() {
        for (Node node : this.graph.getNodeSet())
            node.removeAttribute("ui.class");
    }
}

