package oriedita.editor.drawing;

import oriedita.editor.canvas.LineStyle;

import java.awt.Dimension;
import java.awt.Graphics;

public interface CreasePatternRenderer {
    void drawWithCamera(
            Graphics g,
            boolean displayComments,
            boolean displayCpLines,
            boolean displayAuxLines,
            boolean displayAuxLiveLines,
            float lineWidth,
            LineStyle lineStyle,
            float f_h_WireframeLineWidth,
            int p0x_max, int p0y_max,
            boolean i_mejirusi_display,
            boolean hideOperationFrame);
    void drawWithGraphics(Graphics g, Dimension dimension, boolean hideOperationFrame);
}
