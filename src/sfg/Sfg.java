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
        private List<Edge> edgeList = null;
        private List<Node> nodeList = null;

        public Path() {
            edgeList = new ArrayList<>();
            nodeList = new ArrayList<>();
        }

        public void addEdges(final Edge... edges) {
            for (Edge edge : edges) {
                edgeList.add(edge);
            }
        }

        public void addNodes(final Node... nodes) {
            for (Node node : nodes) {
                nodeList.add(node);
            }
        }

        @Override
        public int hashCode() {
            int hash = 0;
            for (Edge edge : edgeList)
                hash = (hash + edge.hashCode()) % 10000007;
            hash ^= (hash >>> 20) ^ (hash >>> 12);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return hashCode() == obj.hashCode();
        }
    }

    private Map<Node, List<Edge>> adj = null;
    private List<Node> nodeList = null;
    public List<Path> forwardPaths = null; //made public for testing.
    public List<Path> loops = null;

    public Sfg() {
        adj = new HashMap<>();
        nodeList = new ArrayList<>();
        forwardPaths = new ArrayList<>();
        loops = new ArrayList<>();
    }

    public void addNodes(final Node... nodes) {
        for (Node node : nodes)
            nodeList.add(node);
    }

    public void addEdges(final Edge... edges) {
        for (Edge edge : edges) {
            if (!adj.containsKey(edge.src))
                adj.put(edge.getSrc(), new ArrayList<>());
            adj.get(edge.getSrc()).add(edge);
        }
    }

    public double solve(final Node start, final Node end) {
        getForwardPaths(start, end);
        getLoops();
        return 0.0;
    }

    private void getForwardPaths(final Node start, final Node end) {
        getForwardPathsUtil(start, end, new Stack<>(), new Stack<>(),
                new HashMap<>());
    }

    private void getForwardPathsUtil(final Node curr, final Node end,
                                     final Stack<Node> nodeStack,
                                     final Stack<Edge> edgeStack,
                                     final Map<Node, Boolean> visited) {
        visited.put(curr, true);
        nodeStack.push(curr);
        for (Edge edge : adj.get(curr)) {
            if (edge.dest.equals(end)) {
                Path path = new Path();
                //Creates a new path, adds the last node and edge to it.
                path.addNodes(nodeStack.toArray(new Node[nodeStack.size()]));
                path.addNodes(end);
                path.addEdges(edgeStack.toArray(new Edge[edgeStack.size()]));
                path.addEdges(edge);
                //Adds the new path to the forward paths.
                this.forwardPaths.add(path);
            } else if (!visited.containsKey(edge.dest) ||
                    !visited.get(edge.dest)) {
                edgeStack.push(edge);
                getForwardPathsUtil(edge.dest, end, nodeStack, edgeStack, visited);
            }
        }
        visited.put(curr, false);
        nodeStack.pop();
        if (!nodeStack.empty())
            edgeStack.pop();
    }

    private void getLoops() {
        for (Node node : nodeList)
            getLoopsUtil(node, node, new Stack<>(), new Stack<>(), new HashMap<>());
    }

    private void getLoopsUtil(final Node curr, final Node dest,
                              final Stack<Node> nodeStack,
                              final Stack<Edge> edgeStack,
                              final Map<Node, Boolean> visited) {
        if (visited.containsKey(curr) && curr.equals(dest)) {
            Path loop = new Path();
            loop.addNodes(nodeStack.toArray(new Node[nodeStack.size()]));
            loop.addEdges(edgeStack.toArray(new Edge[edgeStack.size()]));
            if (!isDuplicateLoop(loop))
                this.loops.add(loop);
        }
        for (Edge edge : adj.get(curr))
            if (!visited.containsKey(edge.dest) || !visited.get(edge.dest)) {
                visited.put(edge.dest, true);
                nodeStack.push(curr);
                edgeStack.push(edge);
                getLoopsUtil(edge.dest, dest, nodeStack, edgeStack, visited);
                visited.put(edge.dest, false);
                nodeStack.pop();
                edgeStack.pop();
            }
    }

    private boolean isDuplicateLoop(final Path loop) {
        for (Path checkedLoop : loops)
            if (loop.equals(checkedLoop))
                return true;
        return false;
    }
}
