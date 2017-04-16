package sfg;

import java.util.*;

public class Sfg {
    /**
     * A representation of a *node* inside a SFG.
     */
    public static class Node {
        private String label = null;

        public Node(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * A representation of an *edge* between two nodes inside a SFG.
     */
    public static class Edge {
        private Node src = null;
        private Node dest = null;
        private double gain = 0.0;

        public Edge(final Node src, final Node dest, final double gain) {
            this.src = src;
            this.dest = dest;
            this.gain = gain;
        }

        public Node getSrc() {
            return src;
        }

        public Node getDest() {
            return dest;
        }

        public double getGain() {
            return gain;
        }
    }

    public static class Path {
        private List<Edge> edgeSet = null;
        private List<Node> nodeSet = null;

        public Path() {
            edgeSet = new ArrayList<>();
            nodeSet = new ArrayList<>();
        }

        public void addEdges(final Edge... edges) {
            for (Edge edge : edges) {
                edgeSet.add(edge);
            }
        }

        public void addNodes(final Node... nodes) {
            for (Node node : nodes) {
                nodeSet.add(node);
            }
        }

        @Override
        public int hashCode() {
            int hash = 0;
            for (Edge edge : edgeSet)
                hash = (hash + edge.hashCode()) % 100000007;
            hash ^= (hash >>> 20) ^ (hash >>> 12);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return hashCode() == obj.hashCode();
        }
    }

    private Map<Node, List<Edge>> adj = null;

    public Sfg() {
        adj = new HashMap<>();
    }
}
