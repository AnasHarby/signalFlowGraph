package sfg;

/**
 * A representation of an *edge* between two nodes inside a SFG.
 */
public class Edge {
    private Node src = null;
    private Node dest = null;
    private double gain = 0.0;

    /**
     * Creates a new edge.
     *
     * @param src  Source node.
     * @param dest Destination node.
     * @param gain gain of the edge.
     */
    public Edge(final Node src, final Node dest, final double gain) {
        this.src = src;
        this.dest = dest;
        this.gain = gain;
    }

    /**
     * Gets the edge's source node.
     *
     * @return source node.
     */
    public Node getSrc() {
        return src;
    }

    public void setSrc(final Node src) {
        this.src = src;
    }

    /**
     * Gets the edge's destination node.
     *
     * @return destination node.
     */
    public Node getDest() {
        return dest;
    }

    public void setDest(final Node dest) {
        this.dest = dest;
    }

    /**
     * Gets gain of the edge.
     *
     * @return gain.
     */
    public double getGain() {
        return gain;
    }

    public void setGain(final double gain) {
        this.gain = gain;
    }
}