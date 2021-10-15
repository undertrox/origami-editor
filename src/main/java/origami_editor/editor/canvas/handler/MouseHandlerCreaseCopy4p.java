package origami_editor.editor.canvas.handler;

import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;

public class MouseHandlerCreaseCopy4p extends BaseMouseHandlerInputRestricted {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_COPY_4P_32;
    }

    //マウス操作(mouseMode==32copy2p2p2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.lineStep.size() == 0) {    //第1段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
            }
            return;
        }

        if (d.lineStep.size() == 1) {    //第2段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) >= d.selectionDistance) {
                d.lineStep.clear();
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.BLUE_2));
            }
            if (OritaCalc.distance(d.lineStep.get(0).getA(), d.lineStep.get(0).getA()) < 0.00000001) {
                d.lineStep.clear();
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


        if (d.lineStep.size() == 2) {    //第3段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            closestPoint.set(d.getClosestPoint(p));
            if (p.distance(closestPoint) >= d.selectionDistance) {
                d.lineStep.clear();
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.CYAN_3));
            }
            return;
        }

        if (d.lineStep.size() == 3) {    //第4段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);
            closestPoint.set(d.getClosestPoint(p));
            if (p.distance(closestPoint) >= d.selectionDistance) {
                d.lineStep.clear();
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.ORANGE_4));
            }
            if (OritaCalc.distance(d.lineStep.get(2).getA(), d.lineStep.get(3).getA()) < 0.00000001) {
                d.lineStep.clear();
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }
    }

    //マウス操作(mouseMode==32copy2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

//  ********************************************

    //マウス操作(mouseMode==32copy2p2pp　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 4) {
            d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setSave(d.foldLineSet.getMemoSelectOption(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s_temp.move(d.lineStep.get(0).getA(), d.lineStep.get(1).getA(), d.lineStep.get(2).getA(), d.lineStep.get(3).getA());//全体を移動する

            int sousuu_old = d.foldLineSet.getTotal();
            d.foldLineSet.addSave(ori_s_temp.getSave());
            int sousuu_new = d.foldLineSet.getTotal();
            d.foldLineSet.divideLineSegmentIntersections(1, sousuu_old, sousuu_old + 1, sousuu_new);

            d.record();
            d.app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);

            d.lineStep.clear();
        }
    }
}
