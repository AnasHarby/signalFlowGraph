package test;

import org.junit.Assert;
import org.junit.Test;
import sfg.Sfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tester {

    @Test
    public void testPathEquality() {
        int testSize = 500;
        List<Sfg.Node> nodes = new ArrayList<>();
        List<Sfg.Edge> edges = new ArrayList<>();
        List<Sfg.Path> paths = new ArrayList<>();
        for (int i = 0; i < testSize; i++)
            nodes.add(new Sfg.Node("N" + i));
        for (int i = 1; i < testSize; i++)
            edges.add(new Sfg.Edge(nodes.get(i - 1), nodes.get(i), 5));
        for (int i = 0; i < testSize; i++) {
            Collections.shuffle(nodes);
            Collections.shuffle(edges);
            Sfg.Path path = new Sfg.Path();
            path.addNodes(nodes.toArray(new Sfg.Node[nodes.size()]));
            path.addEdges(edges.toArray(new Sfg.Edge[edges.size()]));
            paths.add(path);
        }
        for (int i = 0; i < testSize; i++)
            for (int j = i + 1; j < testSize; j++)
                Assert.assertTrue(paths.get(i).equals(paths.get(j)));
        edges.add(new Sfg.Edge(nodes.get(5), nodes.get(1), 5));
        Sfg.Path unequalPath = new Sfg.Path();
        unequalPath.addNodes(nodes.toArray(new Sfg.Node[nodes.size()]));
        unequalPath.addEdges(edges.toArray(new Sfg.Edge[edges.size()]));
        for (int i = 0; i < testSize; i++)
            Assert.assertFalse(paths.get(i).equals(unequalPath));
    }

    @Test
    public void testForwardPaths() {
        Sfg sfg = new Sfg();
        List<Sfg.Node> nodes = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            nodes.add(new Sfg.Node("n" + i));
        sfg.addNodes(nodes.toArray(new Sfg.Node[nodes.size()]));
        for (int i = 0; i < 7; i++)
            sfg.addEdges(new Sfg.Edge(nodes.get(i), nodes.get(i + 1), 5));
        sfg.addEdges(new Sfg.Edge(nodes.get(7), nodes.get(1), 5),
                new Sfg.Edge(nodes.get(2), nodes.get(1), 5),
                new Sfg.Edge(nodes.get(4), nodes.get(3), 5),
                new Sfg.Edge(nodes.get(7), nodes.get(6), 5),
                new Sfg.Edge(nodes.get(3), nodes.get(5), 5));
        Sfg.SfgMetadata metadata = sfg.solve(nodes.get(0), nodes.get(5));
        Assert.assertTrue(metadata.getForwardPaths().size() == 2);
    }

    @Test
    public void testLoops() {
        Sfg sfg = new Sfg();
        List<Sfg.Node> nodes = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            nodes.add(new Sfg.Node("n" + i));
        sfg.addNodes(nodes.toArray(new Sfg.Node[nodes.size()]));
        for (int i = 0; i < 7; i++)
            sfg.addEdges(new Sfg.Edge(nodes.get(i), nodes.get(i + 1), 5));
        sfg.addEdges(new Sfg.Edge(nodes.get(7), nodes.get(1), 5),
                new Sfg.Edge(nodes.get(2), nodes.get(1), 5),
                new Sfg.Edge(nodes.get(4), nodes.get(3), 5),
                new Sfg.Edge(nodes.get(7), nodes.get(6), 5));
        Sfg.SfgMetadata metadata = sfg.solve(nodes.get(0), nodes.get(5));
        Assert.assertTrue(metadata.getLoops().size() == 4);
    }

    @Test
    public void testAll() {
        Sfg sfg = new Sfg();
        List<Sfg.Node> nodes = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            nodes.add(new Sfg.Node("n" + i));
        sfg.addNodes(nodes.toArray(new Sfg.Node[nodes.size()]));
        for (int i = 0; i < 7; i++)
            sfg.addEdges(new Sfg.Edge(nodes.get(i), nodes.get(i + 1), i + 1));
        sfg.addEdges(new Sfg.Edge(nodes.get(7), nodes.get(1), 8),
                new Sfg.Edge(nodes.get(2), nodes.get(1), 9),
                new Sfg.Edge(nodes.get(4), nodes.get(3), 10),
                new Sfg.Edge(nodes.get(7), nodes.get(6), 11));
        Assert.assertEquals(0.100620049,
                sfg.solve(nodes.get(0), nodes.get(5)).getResult(), 0.0001);
    }
}
