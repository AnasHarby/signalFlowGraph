package test;

import org.junit.Assert;
import org.junit.Test;
import sfg.Sfg;

import java.util.ArrayList;
import java.util.List;


public class Tester {

    @Test
    public void testPathEquality() {
        Sfg.Path a = new Sfg.Path();
        Sfg.Path b = new Sfg.Path();
        Sfg.Node n1 = new Sfg.Node("n1");
        Sfg.Node n2 = new Sfg.Node("n2");
        Sfg.Node n3 = new Sfg.Node("n3");
        Sfg.Node n4 = new Sfg.Node("n4");
        Sfg.Node n5 = new Sfg.Node("n5");
        Sfg.Edge e1 = new Sfg.Edge(n1, n2, 1);
        Sfg.Edge e2 = new Sfg.Edge(n2, n3, 2);
        Sfg.Edge e3 = new Sfg.Edge(n3, n4, 3);
        Sfg.Edge e4 = new Sfg.Edge(n4, n5, 4);
        a.addEdges(e1, e2, e3, e4);
        b.addEdges(e2, e3, e4, e1);
        Assert.assertTrue(a.equals(b));
        b.addEdges(e4, e1);
        Assert.assertFalse(a.equals(b));
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
        sfg.solve(nodes.get(0), nodes.get(5));
        Assert.assertTrue(sfg.forwardPaths.size() == 2);
    }
}
