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
        this.forwardPaths = new ArrayList<>();
        this.loops = new ArrayList<>();
        this.adj = new HashMap<>();
        this.nodeMap = new HashMap<>();
        this.delta = new Delta();
        this.forwardPathsDeltas = new HashMap<>();
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
     * Clears the SFG.
     */
    public void clear() {
        this.forwardPaths.clear();
        this.loops.clear();
        this.adj.clear();
        this.nodeMap.clear();
        this.delta.clear();
        this.forwardPathsDeltas.clear();
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
            if (!visited.containsKey(edge.getDest()) || !visited.get(edge.getDest())) {
                visited.put(edge.getDest(), true);
                nodeStack.push(curr);
                edgeStack.push(edge);
                getLoopsUtil(edge.getDest(), dest, nodeStack, edgeStack, visited,
                        ret, checkedLoops, adj);
                visited.put(edge.getDest(), false);
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
            for (LoopGroupContainer container : delta.getContainerList()) {
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
