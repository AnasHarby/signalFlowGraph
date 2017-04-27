package sfg;

/**
 * A representation of a *node* inside a SFG.
 */
public class Node {
    private String label = null;

    /**
     * Creates a new node.
     *
     * @param label label for this node, this is used later in SFG as an
     *              identifier.
     */
    public Node(final String label) {
        this.label = label;
    }

    /**
     * Gets label for this node.
     *
     * @return node label.
     */
    public String getLabel() {
        return label;
    }
}