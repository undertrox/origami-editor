package origami_editor.editor.canvas.handler;

import origami.crease_pattern.element.Point;
import origami_editor.editor.App;
import origami_editor.editor.MouseMode;
import origami_editor.editor.canvas.MouseModeHandler;
import origami_editor.editor.folded_figure.FoldedFigure;

public class MouseHandlerChangeStandardFace implements MouseModeHandler {
    private final App app;

    public MouseHandlerChangeStandardFace(App app) {
        this.app = app;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CHANGE_STANDARD_FACE_103;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {

    }

    @Override
    public void mouseDragged(Point p0) {

    }

    @Override
    public void mouseReleased(Point p0) {
        int new_referencePlane_id;
        int old_referencePlane_id;
        old_referencePlane_id = app.OZ.cp_worker1.getReferencePlaneId();

        new_referencePlane_id = app.OZ.cp_worker1.setReferencePlaneId(p0);
        System.out.println("kijyunmen_id = " + new_referencePlane_id);
        if (app.OZ.ct_worker.face_rating != null) {//20180227追加
            System.out.println(
                    "OZ.js.nbox.get_jyunjyo = " + app.OZ.ct_worker.nbox.getSequence(new_referencePlane_id) + " , rating = " +
                            app.OZ.ct_worker.nbox.getWeight(app.OZ.ct_worker.nbox.getSequence(new_referencePlane_id))

            );

        }
        if ((new_referencePlane_id != old_referencePlane_id) && (app.OZ.estimationStep != FoldedFigure.EstimationStep.STEP_0)) {
            app.OZ.estimationStep = FoldedFigure.EstimationStep.STEP_1;
        }
    }
}
