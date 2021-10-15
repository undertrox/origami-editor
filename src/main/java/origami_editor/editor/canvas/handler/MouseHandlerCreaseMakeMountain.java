package origami_editor.editor.canvas.handler;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseMakeMountain extends BaseMouseHandlerBoxSelect {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_MOUNTAIN_23;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==23 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        d.lineStep.clear();

        if (selectionStart.distance(p0) > 0.000001) {//現状では赤を赤に変えたときもUNDO用に記録されてしまう20161218
            if (d.insideToMountain(selectionStart, p0)) {
                d.fix2(0.001, 0.5);
                d.record();
            }
        } else {//現状では赤を赤に変えたときもUNDO用に記録されてしまう20161218
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double closestLineSegmentDistance(Ten p)
                d.foldLineSet.closestLineSegmentSearch(p).setColor(LineColor.RED_1);
                d.fix2(0.001, 0.5);
                d.record();
            }
        }

    }
}
