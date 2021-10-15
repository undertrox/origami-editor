package origami_editor.editor.canvas.handler;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;

public class MouseHandlerDrawCreaseSymmetric extends BaseMouseHandlerInputRestricted {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_SYMMETRIC_12;
    }

    //マウス操作(mouseMode==12鏡映モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.lineStep.size() == 0) {    //第1段階として、点を選択
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.MAGENTA_5));
            }
        } else if (d.lineStep.size() == 1) {    //第2段階として、点を選択
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.fromNumber(d.lineStep.size() + 1)));

                d.lineStep.get(0).setB(d.lineStep.get(1).getB());
            } else {
                d.lineStep.clear();
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }

            if (d.lineStep.get(0).determineLength() < 0.00000001) {
                d.lineStep.clear();
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }
    }

    //マウス操作(mouseMode==12鏡映モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==12鏡映モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        LineSegment adds = new LineSegment();
        if (d.lineStep.size() == 2) {
            d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            int old_sousuu = d.foldLineSet.getTotal();

            for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                LineSegment s = d.foldLineSet.get(i);
                if (s.getSelected() == 2) {
                    adds.set(OritaCalc.findLineSymmetryLineSegment(s, d.lineStep.get(0)));
                    adds.setColor(s.getColor());

                    d.foldLineSet.addLine(adds);
                }
            }

            int new_sousuu = d.foldLineSet.getTotal();

            d.foldLineSet.divideLineSegmentIntersections(1, old_sousuu, old_sousuu + 1, new_sousuu);

            d.foldLineSet.unselect_all();
            d.record();
            d.app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);

            d.lineStep.clear();
        }
    }
}
