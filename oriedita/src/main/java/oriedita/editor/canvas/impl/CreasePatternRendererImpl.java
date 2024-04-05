package oriedita.editor.canvas.impl;

import oriedita.editor.Colors;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.CreasePatternRenderer;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.FlatFoldabilityViolation;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class CreasePatternRendererImpl implements CreasePatternRenderer {
    private final CreasePattern_Worker cpWorker;
    private final ApplicationModel applicationModel;
    private final CanvasModel canvasModel;
    private final Camera cpCamera;

    public CreasePatternRendererImpl(CreasePattern_Worker cpWorker, ApplicationModel applicationModel, CanvasModel canvasModel, Camera cpCamera) {
        this.cpWorker = cpWorker;
        this.applicationModel = applicationModel;
        this.canvasModel = canvasModel;
        this.cpCamera = cpCamera;
    }

    public void drawWithCamera(Graphics g, boolean displayComments, boolean displayCpLines, boolean displayAuxLines, boolean displayAuxLiveLines, float lineWidth, LineStyle lineStyle, float f_h_WireframeLineWidth, int p0x_max, int p0y_max, boolean i_mejirusi_display, boolean hideOperationFrame) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        Graphics2D g2 = (Graphics2D) g;
        var grid = cpWorker.getGrid();
        var gridInputAssist = applicationModel.getDisplayGridInputAssist();

        //Drawing grid lines
        grid.draw(g, cpCamera, p0x_max, p0y_max, applicationModel.getDisplayGridInputAssist(), applicationModel.getMinGridUnitSize());

        BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//Line thickness and shape of the end of the line

        //Drawing auxiliary strokes (non-interfering with polygonal lines)
        if (displayAuxLiveLines) {
            g2.setStroke(new BasicStroke(f_h_WireframeLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//Line thickness and shape of the end of the line
            for (var as : cpWorker.getAuxLines().getLineSegmentsIterable()) {
                DrawingUtil.drawAuxLiveLine(g, as, cpCamera, lineWidth, applicationModel.getPointSize(), f_h_WireframeLineWidth);
            }
        }

        //check結果の表示

        g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
        var foldLineSet = cpWorker.getFoldLineSet();
        //Check1Senbには0番目からsize()-1番目までデータが入っている
        if (cpWorker.isCheck1()) {
            for (LineSegment s_temp : foldLineSet.getCheck1LineSegments()) {
                DrawingUtil.pointingAt1(g, cpCamera.object2TV(s_temp));
            }
        }

        if (cpWorker.isCheck2()) {
            for (LineSegment s_temp : foldLineSet.getCheck2LineSegments()) {
                DrawingUtil.pointingAt2(g, cpCamera.object2TV(s_temp));
            }
        }

        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定


        //Check4Senbには0番目からsize()-1番目までデータが入っている
        //Logger.info("foldLineSet.check4_size() = "+foldLineSet.check4_size());
        if (cpWorker.isCheck4()) {
            for (FlatFoldabilityViolation violation : foldLineSet.getViolations()) {
                DrawingUtil.drawViolation(g2, cpCamera.object2TV(violation.getPoint()), violation,
                        applicationModel.getCheck4ColorTransparency(), applicationModel.getAdvancedCheck4Display());
            }

            if (displayComments) {

                if (cpWorker.isCAMVCalculationRunning()) {
                    g.setColor(Colors.get(Color.orange));
                    g.drawString("... cAMV Errors", p0x_max - 100, 10);
                } else {
                    int numErrors = foldLineSet.getViolations().size();
                    if (numErrors == 0) {
                        g.setColor(Colors.get(Color.green));
                    } else {
                        g.setColor(Colors.get(Color.red));
                    }

                    g.drawString(numErrors + " cAMV Errors", p0x_max - 100, 10);
                }
            }
        }


        //Check3Senbには0番目からsize()-1番目までデータが入っている
        if (cpWorker.isCheck3()) {
            for (LineSegment s_temp : foldLineSet.getCheck3LineSegments()) {
                DrawingUtil.pointingAt3(g, cpCamera.object2TV(s_temp));
            }
        }

        //Draw the center of the camera with a cross
        if (i_mejirusi_display) {
            DrawingUtil.cross(g, cpCamera.object2TV(cpCamera.getCameraPosition()), 5.0, 2.0, LineColor.CYAN_3);
        }

        //円を描く　
        if (displayAuxLines) {
            for (Circle circle : foldLineSet.getCircles()) {
                DrawingUtil.drawCircle(g, circle, cpCamera, lineWidth, applicationModel.getPointSize());
            }
        }

        var lines = foldLineSet.getLineSegmentsCollection();
        //selectの描画
        g2.setStroke(new BasicStroke(lineWidth * 2.0f + 2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        for (var s : lines) {
            if (s.getSelected() == 2) {
                DrawingUtil.drawSelectLine(g, s, cpCamera);
            }
        }

        boolean useRounded = applicationModel.getRoundedEnds();
        var pointSize = applicationModel.getPointSize();
        //展開図の描画 補助活線のみ
        if (displayAuxLines) {
            for (var s : lines) {
                if (s.getColor() == LineColor.CYAN_3) {
                    DrawingUtil.drawAuxLine(g, s, cpCamera, lineWidth, pointSize, useRounded);
                }
            }
        }

        //展開図の描画  補助活線以外の折線
        if (displayCpLines) {
            g.setColor(Colors.get(Color.black));
            for (var s : lines) {
                if (s.getColor() != LineColor.CYAN_3 && s.getColor() != LineColor.RED_1 && s.getColor() != LineColor.BLACK_0) {
                    DrawingUtil.drawCpLine(g, s, cpCamera, lineStyle, lineWidth, pointSize, p0x_max, p0y_max, useRounded);
                }
            }
            for (var s : lines) {
                if (s.getColor() == LineColor.RED_1) {
                    DrawingUtil.drawCpLine(g, s, cpCamera, lineStyle, lineWidth, pointSize, p0x_max, p0y_max, useRounded);
                }
            }
            for (var s : lines) {
                if (s.getColor() == LineColor.BLACK_0) {
                    DrawingUtil.drawCpLine(g, s, cpCamera, lineStyle, lineWidth, pointSize, p0x_max, p0y_max, useRounded);
                }
            }
        }

        var operationFrame = cpWorker.getOperationFrame();
        var lineStep = cpWorker.getLineStep();
        //mouseMode==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
        if (!hideOperationFrame && canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61 && lineStep.size() == 4) {
            Point p1 = cpCamera.TV2object(operationFrame.getP1());
            Point p2 = cpCamera.TV2object(operationFrame.getP2());
            Point p3 = cpCamera.TV2object(operationFrame.getP3());
            Point p4 = cpCamera.TV2object(operationFrame.getP4());

            lineStep.set(0, new LineSegment(p1, p2, LineColor.GREEN_6));
            lineStep.set(1, new LineSegment(p2, p3, LineColor.GREEN_6));
            lineStep.set(2, new LineSegment(p3, p4, LineColor.GREEN_6));
            lineStep.set(3, new LineSegment(p4, p1, LineColor.GREEN_6));
        }

        //線分入力時の一時的なs_step線分を描く　

        if (!hideOperationFrame && ((canvasModel.getMouseMode() != MouseMode.OPERATION_FRAME_CREATE_61) || (lineStep.size() == 4))) {
            for (LineSegment s : lineStep) {
                DrawingUtil.drawLineStep(g, s, cpCamera, lineWidth, gridInputAssist);
            }
        }
        //候補入力時の候補を描く//Logger.info("_");
        g2.setStroke(new BasicStroke(lineWidth + 0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A

        for (LineSegment s : cpWorker.getLineCandidate()) {
            DrawingUtil.drawLineCandidate(g, s, cpCamera, pointSize);
        }

        g.setColor(Colors.get(Color.black));

        for (Circle c : cpWorker.getCircleStep()) {
            DrawingUtil.drawCircleStep(g, c, cpCamera);
        }

        g.setColor(Colors.get(Color.black));
        if (displayComments) {
            g.drawString("1/" + grid.getGridSize(), 10, 55);
            cpWorker.getTextWorker().draw(g2, cpCamera);
        }
    }
}
