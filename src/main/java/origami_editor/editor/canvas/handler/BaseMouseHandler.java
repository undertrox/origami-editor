package origami_editor.editor.canvas.handler;

import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.canvas.MouseModeHandler;

public abstract class BaseMouseHandler implements MouseModeHandler {
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }

    public void setDrawingWorker(CreasePattern_Worker d) {
        this.d = d;
    }
}
