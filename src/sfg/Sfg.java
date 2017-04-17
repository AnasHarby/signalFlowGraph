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
        private double gain = 1;

        public Path() {
            this.edgeList = new ArrayList<>();
            this.nodeList = new ArrayList<>();
        }

        public void addEdges(final Edge... edges) {
            this.edgeList.addAll(Arrays.asList(edges));
            for (Edge edge : edges)
                this.gain *= edge.getGain();
        }

        public void addNodes(final Node... nodes) {
            this.nodeList.addAll(Arrays.asList(nodes));
        }

        public boolean touches(final Path path) {
            Set<Node> nodeSet = new HashSet<>(this.nodeList);
            for (Node node : path.nodeList)
                if (nodeSet.contains(node))
                    return true;
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 0;
            for (Edge edge : this.edgeList)
                hash = (hash + edge.hashCode()) % 10000007;
            hash ^= (hash >>> 20) ^ (hash >>> 12);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return getClass() == obj.getClass() && hashCode() == obj.hashCode();
        }

        public double getGain() {
            return this.gain;
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
    }

    private Map<Node, List<Edge>> adj = null;
    private List<Node> nodeList = null;
    public List<Path> forwardPaths = null; //made public for testing.
    public List<Path> loops = null;
    private Delta delta = null;
    private Map<Path, Delta> forwardPathsDeltas = null;

    public Sfg() {
        adj = new HashMap<>();
        nodeList = new ArrayList<>();
    }

    public void addNodes(final Node... nodes) {
        this.nodeList.addAll(Arrays.asList(nodes));
    }

    public void addEdges(final Edge... edges) {
        for (Edge edge : edges) {
            if (!adj.containsKey(edge.src))
                adj.put(edge.getSrc(), new ArrayList<>());
            adj.get(edge.getSrc()).add(edge);
        }
    }

    public double solve(final Node start, final Node end) {
        this.forwardPaths = getForwardPaths(start, end, this.adj);
        this.loops = getLoops(this.nodeList, this.adj);
        this.delta = getDelta(this.loops);
        this.forwardPathsDeltas = getForwardPathsDeltas(this.forwardPaths, this.delta);
        return getResult(this.delta, this.forwardPathsDeltas, this.forwardPaths);
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
            if (edge.dest.equals(end)) {
                Path path = new Path();
                //Creates a new path, adds the last node and edge to it.
                path.addNodes(nodeStack.toArray(new Node[nodeStack.size()]));
                path.addNodes(end);
                path.addEdges(edgeStack.toArray(new Edge[edgeStack.size()]));
                path.addEdges(edge);
                //Adds the new path to the forward paths.
                ret.add(path);
            } else if (!visited.containsKey(edge.dest) ||
                    !visited.get(edge.dest)) {
                edgeStack.push(edge);
                getForwardPathsUtil(edge.dest, end, nodeStack, edgeStack,
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
        List<Path> loops = new ArrayList<>();
        for (Node node : nodeList)
            loops.addAll(getLoopsUtil(node, node, new Stack<>(), new Stack<>(),
                    new HashMap<>(), new ArrayList<>(), loops, adj));
        return loops;
    }

    private List<Path> getLoopsUtil(final Node curr, final Node dest,
                                    final Stack<Node> nodeStack,
                                    final Stack<Edge> edgeStack,
                                    final Map<Node, Boolean> visited,
                                    final List<Path> ret,
                                    final List<Path> checkedLoops,
                                    final Map<Node, List<Edge>> adj) {
        if (visited.containsKey(curr) && curr.equals(dest)) {
            Path loop = new Path();
            loop.addNodes(nodeStack.toArray(new Node[nodeStack.size()]));
            loop.addEdges(edgeStack.toArray(new Edge[edgeStack.size()]));
            if (!isDuplicateLoop(loop, checkedLoops))
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

    private boolean isDuplicateLoop(final Path loop, List<Path> loops) {
        for (Path checkedLoop : loops)
            if (loop.equals(checkedLoop))
                return true;
        return false;
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
}
