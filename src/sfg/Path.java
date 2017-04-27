package sfg;

import java.util.*;

/**
 * A representation of a path of nodes/edges inside a SFG.
 */
public class Path {
    private List<Edge> edgeList = null;
    private List<Node> nodeList = null;
    private double gain = 1;
    private long totEdgesHashCodes = 0;

    /**
     * Creates a new empty path.
     */
    public Path() {
        this.edgeList = new ArrayList<>();
        this.nodeList = new ArrayList<>();
    }

    /**
     * Adds edges to the path.
     *
     * @param edges edges to be added.
     */
    public void addEdges(final Edge... edges) {
        this.edgeList.addAll(Arrays.asList(edges));
        for (Edge edge : edges) {
            this.gain *= edge.getGain();
            this.totEdgesHashCodes += edge.hashCode();
        }
    }

    /**
     * Adds nodes to the path.
     *
     * @param nodes nodes to be added.
     */
    public void addNodes(final Node... nodes) {
        this.nodeList.addAll(Arrays.asList(nodes));
    }

    /**
     * Checks if this path touches another path (intersect in a
     * node/edge or more).
     *
     * @param path path to be checked for touching.
     * @return true if they touch, false if not.
     */
    public boolean touches(final Path path) {
        Set<Node> nodeSet = new HashSet<>(this.nodeList);
        for (Node node : path.nodeList)
            if (nodeSet.contains(node))
                return true;
        return false;
    }

    /**
     * Gets overall gain of the path.
     *
     * @return gain.
     */
    public double getGain() {
        return this.gain;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    @Override
    public int hashCode() {
        long hash = this.totEdgesHashCodes;
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        return (int) hash % 10000007;
    }

    @Override
    public boolean equals(final Object obj) {
        return getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public Object clone() {
        Path clone = new Path();
        clone.addNodes(this.nodeList.toArray(new Node[this.nodeList.size()]));
        clone.addEdges(this.edgeList.toArray(new Edge[this.edgeList.size()]));
        return clone;
    }
}