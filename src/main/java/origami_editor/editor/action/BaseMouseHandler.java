package origami_editor.editor.action;


import origami_editor.editor.canvas.CreasePattern_Worker;

import javax.inject.Inject;

public abstract class BaseMouseHandler implements MouseModeHandler {
    @Inject
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }
}