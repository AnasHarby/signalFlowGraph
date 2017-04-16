package test;

import sfg.Sfg;

public class Test {
    public static void main(String[] args) {
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
        System.out.println(a.equals(b));
        b.addEdges(e4, e1);
        System.out.println(a.equals(b));
    }
}
