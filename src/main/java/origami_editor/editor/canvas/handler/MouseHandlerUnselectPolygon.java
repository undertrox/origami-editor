package origami_editor.editor.canvas.handler;

import origami_editor.editor.MouseMode;

public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNSELECT_POLYGON_67;
    }
}
