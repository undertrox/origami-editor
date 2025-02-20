package origami.data.quadTree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import static org.junit.jupiter.api.Assertions.*;

class PersistentQuadTreeTest {
    PersistentQuadTree tree;

    @BeforeEach
    void setUp() {
        tree = new PersistentQuadTree(0, 100, 0, 100, 4);
    }

    @Test
    void testTreeContainsLineAfterAdding() {
        var line = new LineSegment(new Point(0, 0), new Point(100, 100));
        tree.add(line);
        assertTrue(tree.getAllLines().contains(line));
    }

    @Test
    void testTreeContainsLineAfterAdding_LineOutsideBounds() {
        var line = new LineSegment(new Point(200, 200), new Point(300, 300));
        tree.add(line);
        assertTrue(tree.getAllLines().contains(line));
    }

    @Test
    void testTreeNodeSplitsAfterAddingEnoughPoints() {
        var line1 = new LineSegment(new Point(0, 0), new Point(100, 100));
        var line2 = new LineSegment(new Point(50, 0), new Point(50, 100));
        var line3 = new LineSegment(new Point(0, 50), new Point(100, 50));
        tree.add(line1);
        tree.add(line2);
        tree.add(line3);
        assertFalse(tree.getRoot().isLeaf());
    }
}