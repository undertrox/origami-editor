package origami.data.quadTree;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.crease_pattern.element.Rectangle;
import origami.crease_pattern.element.StraightLine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * QuadTree that is intended to be kept for a long time, with methods to add and remove items
 */
public class PersistentQuadTree {

    public static class Node {
        private double l, r, b, t;
        private Node[] children;
        private ArrayList<LineSegment> lines;
        private HashSet<Point> points;
        private Point p1, p2, p3, p4;

        public Node(double l, double r, double b, double t) {
            this.l = l;
            this.r = r;
            this.b = b;
            this.t = t;
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

                if (points.size() > 32) {
                    split();
                }
            } else {
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
            children[0] = new Node(l, l + w, b, b + h);
            children[1] = new Node(l + w, r, b, b + h);
            children[2] = new Node(l, l + w, b + h, t);
            children[3] = new Node(l + w, r, b + h, t);
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
            if (straightLine.side(p4) != side) {
                return true;
            }
            return false;
            //return bounds.totu_boundary_inside(line);
        }

        public boolean contains(Point p) {
            var x = p.getX();
            var y = p.getY();
            return x > l && x < r && y < t && y > b;
        }

        public List<LineSegment> getBounds() {
            return new Rectangle(p1, p2, p3, p4).getLineSegments();
        }

        public Node[] getChildren() {
            return children;
        }
    }

    private Node root;

    public PersistentQuadTree(double initialL, double initialR, double initialB, double initialT) {
        root = new Node(initialL, initialR, initialB, initialT);
    }

    public Node getRoot() {return root;}
    public void add(LineSegment l) {
        root.add(l);
    }

    public void clear() {
        root = new Node(root.l, root.r, root.b, root.t);
    }
}
