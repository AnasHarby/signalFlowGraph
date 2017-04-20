package gui;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.DoubleValidator;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
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

    private Graph graph;

    public ViewController() {
        this.graph = new SingleGraph("SFG");
    }

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
}

