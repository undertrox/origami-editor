package oriedita.editor.drawing;

import java.awt.Dimension;
import java.awt.Graphics;

public interface CreasePatternRenderer {
    void drawWithGraphics(Graphics g, Dimension dimension, boolean hideOperationFrame);
}
