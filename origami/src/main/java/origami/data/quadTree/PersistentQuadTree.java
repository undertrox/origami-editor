package origami.data.quadTree;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Rectangle;
import origami.crease_pattern.element.StraightLine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * QuadTree containing lines that is intended to be kept for a long time.
 * Every node keeps references to every line going through the node, as well as every point
 * contained in the node. The splitting is done based on the number of points
 */
public class PersistentQuadTree {
    public static final int CAPACITY = 32;

    public static class Node {
        private final double l, r, b, t;
        private Node[] children;
        private final ArrayList<LineSegment> lines;
        private final HashSet<Point> points;
        private final Point p1, p2, p3, p4;
        private final int capacity;

        public Node(double l, double r, double b, double t, int capacity) {
            this.l = l;
            this.r = r;
            this.b = b;
            this.t = t;
            this.capacity = capacity;
            children = null;
            lines = new ArrayList<>();
            points = new HashSet<>();
            p1 = new Point(l, t);
            p2 = new Point(r, t);
            p3 = new Point(r, b);
            p4 = new Point(l, b);
        }

        public void add(LineSegment l) {
            if (!contains(l)) {
                return;
            }
            if (isLeaf()){
                    lines.add(l);
                    if (contains(l.getA())) {
                        points.add(l.getA());
                    }
                    if (contains(l.getB())) {
                        points.add(l.getB());
                    }

                if (points.size() > capacity) {
                    split();
                }
            } else {
                assert lines.isEmpty();
                assert points.isEmpty();
                assert children.length == 4;

                for (Node child : children) {
                    child.add(l);
                }
            }
        }

        public boolean isLeaf() {
            return children == null;
        }

        public void split() {
            children = new Node[4];
            double w = (r - l) / 2, h = (t - b) / 2;
            children[0] = new Node(l, l + w, b, b + h, capacity);
            children[1] = new Node(l + w, r, b, b + h, capacity);
            children[2] = new Node(l, l + w, b + h, t, capacity);
            children[3] = new Node(l + w, r, b + h, t, capacity);
            for (Node child : children) {
                for (LineSegment line : lines) {
                    if (child.contains(line)) {
                        child.add(line);
                    }
                }
            }
            lines.clear();
            points.clear();
        }

        public List<LineSegment> getLines() {
            return lines;
        }

        public boolean contains(LineSegment line) {
            if (contains(line.getA())) {return true;}
            if (contains(line.getB())) {return true;}
            if (line.determineMaxX() < l || line.determineMinX() > r) {
                if (line.determineMaxY() < b || line.determineMinY() > t) {
                    return false;
                }
            }
            var straightLine = new StraightLine(line);
            var side = straightLine.side(p1);
            if (straightLine.side(p2) != side) {
                return true;
            }
            if (straightLine.side(p3) != side) {
                return true;
            }
            return straightLine.side(p4) != side;
        }

        public boolean contains(Point p) {
            var x = p.getX();
            var y = p.getY();
            return x >= l && x <= r && y <= t && y >= b;
        }

        public List<LineSegment> getBounds() {
            return new Rectangle(p1, p2, p3, p4).getLineSegments();
        }

        public Node[] getChildren() {
            return children;
        }
    }

    private Node root;
    private final int capacity;

    public PersistentQuadTree(double initialL, double initialR, double initialB, double initialT, int capacity) {
        root = new Node(initialL, initialR, initialB, initialT, capacity);
        this.capacity = capacity;
    }

    public PersistentQuadTree(double initialL, double initialR, double initialB, double initialT) {
        this(initialL, initialR, initialB, initialT, CAPACITY);
    }

    public Node getRoot() {return root;}
    public void add(LineSegment l) {
        root.add(l);
    }

    public List<LineSegment> getAllLines() {
        var nodes = new ArrayDeque<Node>();
        var allLines = new HashSet<LineSegment>();
        nodes.add(root);
        while (!nodes.isEmpty()) {
            var node = nodes.poll();
            if (node.isLeaf()) {
                allLines.addAll(node.getLines());
            } else {
                nodes.addAll(Arrays.asList(node.children));
            }
        }
        return allLines.stream().toList();
    }

    public void clear() {
        root = new Node(root.l, root.r, root.b, root.t, capacity);
    }
}
