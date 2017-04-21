package sfg;

import java.util.*;

/**
 * A representation for the structure of SFG and its services, allowing to solve
 * signal flow representations of physical systems using Mason's algorithm.
 */
public class Sfg {
    private List<Path> forwardPaths = null;
    private List<Path> loops = null;
    private Map<Node, List<Edge>> adj = null;
    private Map<String, Node> nodeMap = null;
    private Delta delta = null;
    private Map<Path, Delta> forwardPathsDeltas = null;

    /**
     * Creates a new empty Sfg.
     */
    public Sfg() {
        this.adj = new HashMap<>();
        this.nodeMap = new HashMap<>();
    }

    /**
     * Adds nodes to the SFG.
     * @param nodes nodes to be added.
     */
    public void addNodes(final Node... nodes) {
        for (Node node : nodes) {
            this.nodeMap.put(node.getLabel(), node);
            this.adj.put(node, new ArrayList<>());
        }
    }

    /**
     * Adds edges to the SFG.
     * @param edges edges to be added.
     */
    public void addEdges(final Edge... edges) {
        for (Edge edge : edges)
            this.adj.get(edge.getSrc()).add(edge);
    }

    /**
     * Gets a node from the SFG by its label, changing a node's label is not
     * allowed.
     * @param label label of the node to be returned.
     * @return {@link Node} with the equivalent label.
     */
    public Node getNode(final String label) {
        return this.nodeMap.get(label);
    }

    /**
     * Solves the SFG and returns the result enclosed in {@link SfgMetadata}.
     * @param start Starting node for the signal.
     * @param end Ending node for the signal.
     * @return {@link SfgMetadata} object which has the gain result and a copy
     * of forward paths and loops for this signal.
     */
    public SfgMetadata solve(final Node start, final Node end) {
        this.forwardPaths = getForwardPaths(start, end, this.adj);
        this.loops = getLoops(new ArrayList<>(this.nodeMap.values()), this.adj);
        this.delta = getDelta(this.loops);
        this.forwardPathsDeltas = getForwardPathsDeltas(this.forwardPaths, this.delta);
        double res = getResult(this.delta, this.forwardPathsDeltas,
                this.forwardPaths);
        return new SfgMetadata(res, this.forwardPaths, this.loops, this.delta,
                this.forwardPathsDeltas);
    }

    private List<Path> getForwardPaths(final Node start, final Node end,
                                       final Map<Node, List<Edge>> adj) {
        return getForwardPathsUtil(start, end, new Stack<>(), new Stack<>(),
                new HashMap<>(), new ArrayList<>(), adj);
    }

    private List<Path> getForwardPathsUtil(final Node curr, final Node end,
                                           final Stack<Node> nodeStack,
                                           final Stack<Edge> edgeStack,
                                           final Map<Node, Boolean> visited,
                                           final List<Path> ret,
                                           final Map<Node, List<Edge>> adj) {
        visited.put(curr, true);
        nodeStack.push(curr);
        for (Edge edge : adj.get(curr)) {
            if (edge.getDest().equals(end)) {
                Path path = new Path();
                //Creates a new path, adds the last node and edge to it.
                path.addNodes(nodeStack.toArray(new Node[nodeStack.size()]));
                path.addNodes(end);
                path.addEdges(edgeStack.toArray(new Edge[edgeStack.size()]));
                path.addEdges(edge);
                //Adds the new path to the forward paths.
                ret.add(path);
            } else if (!visited.containsKey(edge.getDest()) ||
                    !visited.get(edge.getDest())) {
                edgeStack.push(edge);
                getForwardPathsUtil(edge.getDest(), end, nodeStack, edgeStack,
                        visited, ret, adj);
            }
        }
        visited.put(curr, false);
        nodeStack.pop();
        if (!nodeStack.empty())
            edgeStack.pop();
        return ret;
    }

    private List<Path> getLoops(final List<Node> nodeList,
                                final Map<Node, List<Edge>> adj) {
        Set<Path> loopSet = new HashSet<>();
        for (Node node : nodeList)
            loopSet.addAll(getLoopsUtil(node, node, new Stack<>(), new Stack<>(),
                    new HashMap<>(), new ArrayList<>(), loopSet, adj));
        return new ArrayList<>(loopSet);
    }

    private List<Path> getLoopsUtil(final Node curr, final Node dest,
                                    final Stack<Node> nodeStack,
                                    final Stack<Edge> edgeStack,
                                    final Map<Node, Boolean> visited,
                                    final List<Path> ret,
                                    final Set<Path> checkedLoops,
                                    final Map<Node, List<Edge>> adj) {
        if (visited.containsKey(curr) && curr.equals(dest)) {
            Path loop = new Path();
            loop.addNodes(nodeStack.toArray(new Node[nodeStack.size()]));
            loop.addEdges(edgeStack.toArray(new Edge[edgeStack.size()]));
            if (!checkedLoops.contains(loop))
                ret.add(loop);
        }
        for (Edge edge : adj.get(curr))
            if (!visited.containsKey(edge.dest) || !visited.get(edge.dest)) {
                visited.put(edge.dest, true);
                nodeStack.push(curr);
                edgeStack.push(edge);
                getLoopsUtil(edge.dest, dest, nodeStack, edgeStack, visited,
                        ret, checkedLoops, adj);
                visited.put(edge.dest, false);
                nodeStack.pop();
                edgeStack.pop();
            }
        return ret;
    }

    private Delta getDelta(final List<Path> loops) {
        Delta ret = new Delta();
        for (int i = 1; i <= loops.size(); i++) {
            LoopGroupContainer nextContainer = getNextNonTouchingContainer(i, 0,
                    new LoopGroupContainer(i), new LoopGroup(), loops);
            if (nextContainer.empty())
                break;
            ret.addContainers(nextContainer);
        }
        return ret;
    }

    private LoopGroupContainer getNextNonTouchingContainer(final int rem,
                                                           final int i, final
                                                           LoopGroupContainer ret,
                                                           final LoopGroup loopGroup,
                                                           final List<Path> loops) {
        if (rem == 0) {
            ret.addLoopGroups((LoopGroup) loopGroup.clone());
            return ret;
        } else if (i == loops.size())
            return ret;
        if (!loopGroup.touches(loops.get(i))) {
            loopGroup.addLoops(loops.get(i));
            getNextNonTouchingContainer(rem - 1, i + 1, ret, loopGroup, loops);
            loopGroup.removeLoops(loops.get(i));
        }
        getNextNonTouchingContainer(rem, i + 1, ret, loopGroup, loops);
        return ret;
    }

    private Map<Path, Delta> getForwardPathsDeltas(final List<Path> forwardPaths,
                                                   final Delta delta) {
        Map<Path, Delta> ret = new HashMap<>();
        for (Path path : forwardPaths) {
            Delta pathDelta = new Delta();
            for (LoopGroupContainer container : delta.containerList) {
                LoopGroupContainer pathGroupContainer = new LoopGroupContainer(
                        container.getDegree());
                for (LoopGroup group : container.getGroupList())
                    if (!group.touches(path))
                        pathGroupContainer.addLoopGroups(group);
                if (!pathGroupContainer.empty())
                    pathDelta.addContainers(pathGroupContainer);
            }
            ret.put(path, pathDelta);
        }
        return ret;
    }

    private double getResult(final Delta delta, final Map<Path, Delta>
            forwardPathsDeltas, final List<Path> forwardPaths) {
        double res = 0;
        for (Path path : forwardPaths) {
            double pathGain = path.getGain() * forwardPathsDeltas.get(path).getGain();
            res += pathGain;
        }
        res /= delta.getGain();
        return res;
    }

    /**
     * A representation of a *node* inside a SFG.
     */
    public static class Node {
        private String label = null;

        /**
         * Creates a new node.
         * @param label label for this node, this is used later in SFG as an
         * identifier.
         */
        public Node(final String label) {
            this.label = label;
        }

        /**
         * Gets label for this node.
         * @return node label.
         */
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

        /**
         * Creates a new edge.
         * @param src Source node.
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
         * @return source node.
         */
        public Node getSrc() {
            return src;
        }

        /**
         * Gets the edge's destination node.
         * @return destination node.
         */
        public Node getDest() {
            return dest;
        }

        /**
         * Gets gain of the edge.
         * @return gain.
         */
        public double getGain() {
            return gain;
        }
    }

    /**
     * A representation of a path of nodes/edges inside a SFG.
     */

    public static class Path {
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
         * @param nodes nodes to be added.
         */
        public void addNodes(final Node... nodes) {
            this.nodeList.addAll(Arrays.asList(nodes));
        }

        /**
         * Checks if this path touches another path (intersect in a
         * node/edge or more).
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
        protected Object clone() {
            Path clone = new Path();
            clone.addNodes(this.nodeList.toArray(new Node[this.nodeList.size()]));
            clone.addEdges(this.edgeList.toArray(new Edge[this.edgeList.size()]));
            return clone;
        }
    }

    public static class LoopGroup {
        private List<Path> loopList = null;
        private double gain = 1;

        public LoopGroup() {
            this.loopList = new ArrayList<>();
        }

        public double getGain() {
            return this.gain;
        }

        public List<Path> getLoopList() {
            return this.loopList;
        }

        public void addLoops(final Path... loops) {
            this.loopList.addAll(Arrays.asList(loops));
            for (Path loop : loops)
                gain *= loop.getGain();
        }

        public void removeLoops(final Path... loops) {
            this.loopList.removeAll(Arrays.asList(loops));
            for (Path loop : loops)
                gain /= loop.getGain();
        }

        public boolean touches(final Path path) {
            for (Path loopInGroup : this.loopList)
                if (path.touches(loopInGroup))
                    return true;
            return false;
        }

        @Override
        public Object clone() {
            LoopGroup clone = new LoopGroup();
            clone.addLoops(this.loopList.toArray(new Path[this.loopList.size()]));
            return clone;
        }
    }

    public static class LoopGroupContainer {
        private List<LoopGroup> groupList = null;
        private double gain = 0;
        private int degree = 0;

        public LoopGroupContainer(final int degree) {
            this.groupList = new ArrayList<>();
            this.degree = degree;
        }

        public void addLoopGroups(final LoopGroup... loopGroups) {
            this.groupList.addAll(Arrays.asList(loopGroups));
            for (LoopGroup group : loopGroups)
                gain += group.getGain();
        }

        public int size() {
            return this.groupList.size();
        }

        public boolean empty() {
            return size() == 0;
        }

        public List<LoopGroup> getGroupList() {
            return groupList;
        }

        public double getGain() {
            return this.gain;
        }

        public int getDegree() {
            return degree;
        }

        @Override
        protected Object clone() {
            LoopGroupContainer clone = new LoopGroupContainer(this.degree);
            for (LoopGroup loopGroup : this.groupList)
                clone.addLoopGroups((LoopGroup) loopGroup.clone());
            return clone;
        }
    }

    public static class Delta {
        private List<LoopGroupContainer> containerList = null;
        private double gain = 1;

        public Delta() {
            containerList = new ArrayList<>();
        }

        public void addContainers(final LoopGroupContainer... containers) {
            this.containerList.addAll(Arrays.asList(containers));
            for (LoopGroupContainer container : containers) {
                this.gain += container.getDegree() % 2 == 0 ? container.getGain()
                        : -1 * container.getGain();
            }
        }

        public List<LoopGroupContainer> getContainerList() {
            return this.containerList;
        }

        public double getGain() {
            return this.gain;
        }

        @Override
        protected Object clone() {
            Delta clone = new Delta();
            for (LoopGroupContainer container : this.containerList)
                clone.addContainers((LoopGroupContainer) container.clone());
            return clone;
        }
    }

    /**
     * A metadata compilation for the SFG for a signal flow inside of it,
     * contains the overall gain result, a list of forward paths copies and a
     * list of loops copies.
     * It's safe to change any of the values of the metadata without affecting
     * the actual SFG.
     */
    public static class SfgMetadata {
        private double result = 0;
        private List<Path> forwardPaths = null;
        private List<Path> loops = null;
        private Delta delta = null;
        private Map<Path, Delta> forwardPathsDeltas = null;

        public SfgMetadata(final double result, final List<Path> forwardPaths
                , final List<Path> loops, final Delta delta, final Map<Path
                , Delta> forwardPathsDeltas) {
            this.result = result;
            this.forwardPaths = clonePaths(forwardPaths);
            this.loops = clonePaths(loops);
            this.delta = (Delta) delta.clone();
            this.forwardPathsDeltas = cloneDeltas(forwardPathsDeltas);
        }

        public double getResult() {
            return this.result;
        }

        public List<Path> getForwardPaths() {
            return this.forwardPaths;
        }

        public List<Path> getLoops() {
            return this.loops;
        }

        public Delta getDelta() {
            return delta;
        }

        public Map<Path, Delta> getForwardPathsDeltas() {
            return forwardPathsDeltas;
        }

        private List<Path> clonePaths(final List<Path> pathList) {
            List<Path> clone = new ArrayList<>();
            for (Path path : pathList)
                clone.add((Path) path.clone());
            return clone;
        }

        private Map<Path, Delta> cloneDeltas(final Map<Path, Delta> deltaMap) {
            Map<Path, Delta> clone = new HashMap<>();
            for (Map.Entry<Path, Delta> entry : deltaMap.entrySet())
                clone.put((Path) entry.getKey().clone(),
                        (Delta) entry.getValue().clone());
            return clone;
        }
    }
}
