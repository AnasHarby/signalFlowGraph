package test;

import org.junit.Assert;
import org.junit.Test;
import sfg.*;

import java.util.*;

public class Tester {

    @Test
    public void testPathEquality() {
        int testSize = 1000;
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Set<Path> paths = new HashSet<>();
        for (int i = 0; i < testSize; i++)
            nodes.add(new Node("N" + i));
        for (int i = 1; i < testSize; i++)
            edges.add(new Edge(nodes.get(i - 1), nodes.get(i), 5));
        for (int i = 0; i < testSize; i++) {
            Collections.shuffle(nodes);
            Collections.shuffle(edges);
            Path path = new Path();
            path.addNodes(nodes.toArray(new Node[nodes.size()]));
            path.addEdges(edges.toArray(new Edge[edges.size()]));
            paths.add(path);
        }
        Assert.assertTrue(paths.size() == 1);
        edges.add(new Edge(nodes.get(5), nodes.get(1), 5));
        Path unequalPath = new Path();
        unequalPath.addNodes(nodes.toArray(new Node[nodes.size()]));
        unequalPath.addEdges(edges.toArray(new Edge[edges.size()]));
        paths.add(unequalPath);
        Assert.assertTrue(paths.size() == 2);
    }

    @Test
    public void testForwardPaths() {
        Sfg sfg = new Sfg();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            nodes.add(new Node("n" + i));
        sfg.addNodes(nodes.toArray(new Node[nodes.size()]));
        for (int i = 0; i < 7; i++)
            sfg.addEdges(new Edge(nodes.get(i), nodes.get(i + 1), 5));
        sfg.addEdges(new Edge(nodes.get(7), nodes.get(1), 5),
                new Edge(nodes.get(2), nodes.get(1), 5),
                new Edge(nodes.get(4), nodes.get(3), 5),
                new Edge(nodes.get(7), nodes.get(6), 5),
                new Edge(nodes.get(3), nodes.get(5), 5));
        SfgMetadata metadata = sfg.solve(nodes.get(0), nodes.get(5));
        Assert.assertTrue(metadata.getForwardPaths().size() == 2);
    }

    @Test
    public void testLoops() {
        Sfg sfg = new Sfg();
        int testSize = 100;
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < testSize; i++)
            nodes.add(new Node("N" + i));
        sfg.addNodes(nodes.toArray(new Node[nodes.size()]));
        for (int i = 0; i < testSize - 1; i++)
            sfg.addEdges(new Edge(nodes.get(i), nodes.get(i + 1), 5));
        for (int i = 1; i < testSize; i++)
            sfg.addEdges(new Edge(nodes.get(i), nodes.get(0), 5));
        SfgMetadata metadata = sfg.solve(nodes.get(0), nodes.get(testSize - 1));
        Assert.assertTrue(metadata.getLoops().size() == testSize - 1);
    }

    @Test
    public void testAll() {
        Sfg sfg = new Sfg();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            nodes.add(new Node("n" + i));
        sfg.addNodes(nodes.toArray(new Node[nodes.size()]));
        for (int i = 0; i < 7; i++)
            sfg.addEdges(new Edge(nodes.get(i), nodes.get(i + 1), i + 1));
        sfg.addEdges(new Edge(nodes.get(7), nodes.get(1), 8),
                new Edge(nodes.get(2), nodes.get(1), 9),
                new Edge(nodes.get(4), nodes.get(3), 10),
                new Edge(nodes.get(7), nodes.get(6), 11));
        Assert.assertEquals(0.100620049,
                sfg.solve(nodes.get(0), nodes.get(5)).getResult(), 0.0001);
    }
}
