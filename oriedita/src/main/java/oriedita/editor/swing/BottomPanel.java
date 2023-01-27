package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.tinylog.Logger;
import oriedita.editor.Canvas;
import oriedita.editor.action.FoldedFigureOperationMode;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.FoldingService;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.swing.component.*;
import oriedita.editor.task.FoldingEstimateSave100Task;
import oriedita.editor.task.FoldingEstimateSpecificTask;
import oriedita.editor.tools.StringOp;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.folding.FoldedFigure;
import origami.folding.element.Face;
import origami.folding.element.SubFace;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class BottomPanel {
    private final ButtonService buttonService;
    private final MeasuresModel measuresModel;
    private final FoldedFigureModel foldedFigureModel;
    private JPanel panel1;
    private JTextField goToFoldedFigureTextField;
    private FoldedFigureRotate foldedFigureRotate;
    private FoldedFigureResize foldedFigureResize;
    private JButton foldButton;
    private JButton anotherSolutionButton;
    private JButton flipButton;
    private JButton foldedFigureAntiAliasButton;
    private JButton shadowButton;
    private JButton frontColorButton;
    private JButton backColorButton;
    private JButton lineColorButton;
    private JButton haltButton;
    private JButton trashButton;
    private JButton resetButton;
    private JButton oriagari_sousaButton;
    private JButton oriagari_sousa_2Button;
    private JButton As100Button;
    private JButton goToFoldedFigureButton;
    private JButton foldedFigureMoveButton;
    private UndoRedo undoRedo;
    private JComboBox<FoldedFigure_Drawer> foldedFigureBox;
    private JButton constraintButton;
    private JButton distortButton;

    @Inject
    public BottomPanel(@Named("mainFrame") JFrame frame,
                       @Named("camvExecutor") TaskExecutorService camvTaskExecutor,
                       @Named("foldingExecutor") TaskExecutorService foldingTaskExecutor,
                       ButtonService buttonService,
                       MeasuresModel measuresModel,
                       CanvasModel canvasModel,
                       FoldedFigureModel foldedFigureModel,
                       CameraModel creasePatternCameraModel,
                       CreasePattern_Worker mainCreasePatternWorker,
                       FoldingService foldingService,
                       ApplicationModel applicationModel,
                       FoldedFiguresList foldedFiguresList,
                       FileModel fileModel,
                       FileSaveService fileSaveService,
                       Canvas canvas,
                       BulletinBoard bulletinBoard) {
        this.buttonService = buttonService;
        this.measuresModel = measuresModel;
        this.foldedFigureModel = foldedFigureModel;

        foldedFigureModel.addPropertyChangeListener(e -> setData(foldedFigureModel));
        canvasModel.addPropertyChangeListener(e -> setData(e, canvasModel));

        $$$setupUI$$$();

        buttonService.registerButton(foldButton, "foldAction");
        buttonService.registerButton(anotherSolutionButton, "anotherSolutionAction");
        buttonService.registerButton(flipButton, "foldedFigureFlipAction");
        buttonService.registerButton(foldedFigureAntiAliasButton, "foldedFigureToggleAntiAliasAction");
        buttonService.registerButton(shadowButton, "foldedFigureToggleShadowAction");
        buttonService.registerButton(frontColorButton, "foldedFigureFrontColorAction");
        buttonService.registerButton(backColorButton, "foldedFigureBackColorAction");
        buttonService.registerButton(lineColorButton, "foldedFigureLineColorAction");
        buttonService.registerButton(haltButton, "haltAction");
        buttonService.registerButton(trashButton, "foldedFigureTrashAction");
        buttonService.registerButton(resetButton, "resetAction");
        buttonService.registerButton(oriagari_sousaButton, "oriagari_sousaAction");
        buttonService.registerButton(oriagari_sousa_2Button, "oriagari_sousa_2Action");
        buttonService.registerButton(As100Button, "As100Action");
        buttonService.registerButton(goToFoldedFigureButton, "goToFoldedFigureAction");
        buttonService.registerButton(foldedFigureMoveButton, "foldedFigureMoveAction");
        buttonService.registerButton(constraintButton, "addColorConstraintAction");

        buttonService.registerButton(undoRedo.getUndoButton(), "foldedFigureUndoAction");
        buttonService.registerButton(undoRedo.getRedoButton(), "foldedFigureRedoAction");


        foldButton.addActionListener(e -> {
            Logger.info("20180220 get_i_fold_type() = " + foldingService.getFoldType());

            if (!applicationModel.getFoldWarning()) {
                try {
                    mainCreasePatternWorker.getFoldLineSet().check4();
                } catch (InterruptedException bruh) {
                    Logger.info("Warning window broke");
                }
                if (!mainCreasePatternWorker.getFoldLineSet().getViolations().isEmpty()) {
                    JCheckBox checkbox = new JCheckBox("Don't show this again");
                    Object[] params = {"Detected errors in flat foldability. Continue to fold?", checkbox};
                    int warningResult = JOptionPane.showConfirmDialog(null, params, "Warning", JOptionPane.YES_NO_OPTION);
                    if (warningResult == JOptionPane.YES_OPTION || checkbox.isSelected()) {
                        foldCreasePattern(mainCreasePatternWorker, foldingService, applicationModel);
                    }
                    applicationModel.setFoldWarning(checkbox.isSelected());
                } else {
                    foldCreasePattern(mainCreasePatternWorker, foldingService, applicationModel);
                }
            } else {
                foldCreasePattern(mainCreasePatternWorker, foldingService, applicationModel);
            }
        });
        anotherSolutionButton.addActionListener(e -> {
            FoldedFigure_Drawer selectedItem = foldedFiguresList.getActiveItem();
            if (selectedItem != null) {
                foldingService.foldAnother(selectedItem);
            }
        });
        flipButton.addActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
            if (selectedFigure != null) {
                foldedFigureModel.advanceState();

                if ((canvasModel.getMouseMode() == MouseMode.MODIFY_CALCULATED_SHAPE_101) && (selectedFigure.foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) {
                    foldedFigureModel.setState(FoldedFigure.State.FRONT_0);
                }//Fold-up forecast map Added to avoid the mode that can not be moved when moving
            }
        });
        As100Button.addActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
            if (selectedFigure != null && selectedFigure.foldedFigure.findAnotherOverlapValid) {
                selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

                foldingTaskExecutor.executeTask(new FoldingEstimateSave100Task(canvas, foldingService, fileSaveService, foldedFiguresList));
            }
        });
        goToFoldedFigureButton.addActionListener(e -> {
            int foldedCases_old = foldedFigureModel.getFoldedCases();
            int newFoldedCases = StringOp.String2int(goToFoldedFigureTextField.getText(), foldedCases_old);
            if (newFoldedCases < 1) {
                newFoldedCases = 1;
            }

            foldedFigureModel.setFoldedCases(newFoldedCases);

            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure == null) {
                return;
            }

            selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            if (foldedFigureModel.getFoldedCases() < selectedFigure.foldedFigure.discovered_fold_cases) {
                selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_51;    //i_suitei_meirei=51はoritatami_suiteiの最初の推定図用カメラの設定は素通りするための設定。推定図用カメラの設定を素通りしたら、i_suitei_meirei=5に変更される。
                //1例目の折り上がり予想はi_suitei_meirei=5を指定、2例目以降の折り上がり予想はi_suitei_meirei=6で実施される
            }

            foldingTaskExecutor.executeTask(new FoldingEstimateSpecificTask(foldedFigureModel, foldingService, canvasModel, foldedFiguresList));
        });
        goToFoldedFigureTextField.addActionListener(e -> goToFoldedFigureButton.doClick());
        constraintButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.ADD_FOLDING_CONSTRAINT);
        });
        distortButton.addActionListener(e -> {
            FoldedFigure_Drawer selectedDrawer = foldedFiguresList.getActiveItem();
            FoldedFigure selected = selectedDrawer.foldedFigure;
            PointSet pointSet = selected.cp_worker2.get();
            SubFace[] subFaces = selected.ct_worker.s;
            int[] faceHeights = determineFaceHeights(pointSet, subFaces);
            double[] vertexHeights = determineVertexHeights(pointSet, faceHeights);

            // adjust heights to fix self intersections
            adjustVertexHeights(pointSet, subFaces, vertexHeights);

            Point p0 = new Point(-8, 8);
            double max = Arrays.stream(vertexHeights).max().orElse(0);

            for (int pointId = 0; pointId < vertexHeights.length; pointId++) {
                Point p = new Point(selectedDrawer.wireFrame_worker_drawer2.get().getPoint(pointId + 1));
                double fac = vertexHeights[pointId] / max;
                p.setX(p.getX() + p0.getX() * fac);
                p.setY(p.getY() + p0.getY() * fac);
                selectedDrawer.wireFrame_worker_drawer2.get().set(pointId + 1, p);

            }
            if (foldingTaskExecutor.isTaskRunning() && Objects.equals(foldingTaskExecutor.getTaskName(), "Distortion")) {
                foldingTaskExecutor.stopTask();
            }
            //foldingTaskExecutor.executeTask(new DistortionTask(selected, selectedDrawer.foldedFigure_worker_drawer, canvasModel));
        });

        undoRedo.addUndoActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null) {
                selectedFigure.undo();
            }
        });
        undoRedo.addRedoActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null) {
                selectedFigure.redo();
            }
        });
        oriagari_sousaButton.addActionListener(e -> {
            canvasModel.setFoldedFigureOperationMode(FoldedFigureOperationMode.MODE_1);
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null) {
                selectedFigure.foldedFigure.setAllPointStateFalse();
                selectedFigure.record();
            }

            canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);
        });
        oriagari_sousa_2Button.addActionListener(e -> {
            canvasModel.setFoldedFigureOperationMode(FoldedFigureOperationMode.MODE_2);
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null) {
                selectedFigure.foldedFigure.setAllPointStateFalse();
                selectedFigure.record();
            }

            canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);
        });
        foldedFigureMoveButton.addActionListener(e -> canvasModel.setMouseMode(MouseMode.MOVE_CALCULATED_SHAPE_102));
        foldedFigureAntiAliasButton.addActionListener(e -> foldedFigureModel.toggleAntiAlias());
        shadowButton.addActionListener(e -> foldedFigureModel.toggleDisplayShadows());
        frontColorButton.addActionListener(e -> {
            //以下にやりたいことを書く

            Color frontColor = JColorChooser.showDialog(frame, "F_col", Color.white);

            if (frontColor != null) {
                foldedFigureModel.setFrontColor(frontColor);
            }
        });
        backColorButton.addActionListener(e -> {
            //以下にやりたいことを書く
            Color backColor = JColorChooser.showDialog(frame, "B_col", Color.white);

            if (backColor != null) {
                foldedFigureModel.setBackColor(backColor);
            }
        });
        lineColorButton.addActionListener(e -> {
            //以下にやりたいことを書く

            Color lineColor = JColorChooser.showDialog(frame, "L_col", Color.white);
            if (lineColor != null) {
                foldedFigureModel.setLineColor(lineColor);
            }
        });
        haltButton.addActionListener(e -> {
            camvTaskExecutor.stopTask();
            foldingTaskExecutor.stopTask();
        });
        trashButton.addActionListener(e -> {
            if (foldedFiguresList.getSize() == 0) {
                return;
            }

            Object selectedItem = foldedFiguresList.getSelectedItem();

            if (selectedItem == null) {
                selectedItem = foldedFiguresList.getElementAt(0);
            }

            foldedFiguresList.removeElement(selectedItem);
        });
        resetButton.addActionListener(e -> {

            mainCreasePatternWorker.clearCreasePattern();
            creasePatternCameraModel.reset();
            foldedFiguresList.removeAllElements();

            canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);

            mainCreasePatternWorker.record();
            mainCreasePatternWorker.auxRecord();
        });
        foldedFigureBox.setModel(foldedFiguresList);
        foldedFigureBox.setRenderer(new IndexCellRenderer());
        foldedFigureBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!applicationModel.getDisplayNumbers()) {
                    applicationModel.setDisplayNumbers(true);
                }
            }
        });
        foldedFigureBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (applicationModel.getDisplayNumbers()) {
                    applicationModel.setDisplayNumbers(false);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
    }

    private void adjustVertexHeights(PointSet pointSet, SubFace[] subFaces, double[] vertexHeights) {
        for (int lineId = 1; lineId <= pointSet.getNumLines(); lineId++) {
            LineSegment l = pointSet.getLineSegmentFromLineId(lineId);
            int faceA = pointSet.lineInFaceBorder_max_lookup(lineId);
            int faceB = pointSet.lineInFaceBorder_min_lookup(lineId);
            if (faceA == faceB) {
                continue;
            }
            if (isBelow(subFaces, faceA, faceB)) {
                int tmp = faceA;
                faceA = faceB;
                faceB = tmp;
            }
            Face a = pointSet.getFace(faceA);
            Face b = pointSet.getFace(faceB);
            ArrayList<Integer> pointsA = getSharedPoints(a, b);
            List<Double> heights = pointsA.stream().map(i -> vertexHeights[i - 1]).collect(Collectors.toList());
            double max = heights.stream().max(Double::compare).orElse(0d);
            double min = heights.stream().min(Double::compare).orElse(1000000d);
            boolean betweenAAndB = false;
            for (int subFaceId = 0; subFaceId < subFaces.length; subFaceId++) {
                SubFace sf = subFaces[subFaceId];

                if (sf.getFaceIdCount() <= 0 || !(sf.contains(faceA) && sf.contains(faceB))) {
                    continue;
                }
                for (int i = 1; i <= sf.getFaceIdCount(); i++) {
                    int faceId = sf.fromTop_count_FaceId(i);
                    if (faceId == faceA) {
                        betweenAAndB = true;
                    } else if (betweenAAndB) {
                        if (faceId == faceB) {
                            betweenAAndB = false;
                            break;
                        }
                        limitHeight(pointSet, vertexHeights, l, faceA, faceB, max, min, faceId);
                    }
                }
            }
        }
    }

    private void limitHeight(PointSet pointSet, double[] vertexHeights, LineSegment l, int faceA, int faceB, double max, double min, int faceId) {
        Face f = pointSet.getFace(faceId);
        for (int j = 1; j <= f.getNumPoints(); j++) {
            int p = f.getPointId(j);
            if (OritaCalc.determineLineSegmentDistance(pointSet.getPoint(p), l) < 1) {
                if (pointSet.inside(pointSet.getPoint(p), faceA) != Polygon.Intersection.OUTSIDE && vertexHeights[p - 1] > max) {
                    vertexHeights[p - 1] = max;
                }
                if (pointSet.inside(pointSet.getPoint(p), faceB) != Polygon.Intersection.OUTSIDE && vertexHeights[p - 1] < min) {
                    vertexHeights[p - 1] = min;
                }
            }
        }
    }

    private ArrayList<Integer> getSharedPoints(Face a, Face b) {
        ArrayList<Integer> pointsA = new ArrayList<>();
        ArrayList<Integer> pointsB = new ArrayList<>();
        for (int i = 1; i <= a.getNumPoints(); i++) {
            pointsA.add(a.getPointId(i));
        }
        for (int i = 1; i <= b.getNumPoints(); i++) {
            pointsB.add(b.getPointId(i));
        }
        pointsA.retainAll(pointsB);
        return pointsA;
    }

    private boolean isBelow(SubFace[] subFaces, int faceA, int faceB) {
        for (SubFace subFace : subFaces) {
            if (subFace.getFaceIdCount() > 0 && subFace.contains(faceA) && subFace.contains(faceB)) {
                for (int i = 1; i <= subFace.getFaceIdCount(); i++) {
                    int faceId = subFace.fromTop_count_FaceId(i);
                    if (faceId == faceA) {
                        return false;
                    }
                    if (faceId == faceB) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private double[] determineVertexHeights(PointSet pointSet, int[] faceHeights) {
        double[] vertexHeights = new double[pointSet.getNumPoints()];
        for (int pointId = 1; pointId <= vertexHeights.length; pointId++) {
            double sum = 0;
            int count = 0;
            for (int faceId = 1; faceId <= faceHeights.length; faceId++) {
                if (pointSet.pointInFaceBorder(faceId, pointId)) {
                    sum += faceHeights[faceId - 1];
                    count++;
                }
            }
            vertexHeights[pointId - 1] = sum / count;
        }
        return vertexHeights;
    }

    private int[] determineFaceHeights(PointSet pointSet, SubFace[] subFaces) {
        int[] faceHeights = new int[pointSet.getNumFaces()];
        for (int faceId = 1; faceId <= faceHeights.length; faceId++) {
            int maxHeight = 0;
            for (int subFaceId = 0; subFaceId < subFaces.length; subFaceId++) {
                SubFace sf = subFaces[subFaceId];
                int index = -1;
                for (int i = 1; i <= sf.getFaceIdCount(); i++) {
                    if (sf.fromTop_count_FaceId(i) == faceId) {
                        index = i;
                        break;
                    }
                }
                if (index > maxHeight) {
                    maxHeight = index;
                }
            }
            faceHeights[faceId - 1] = maxHeight - 1;
        }
        return faceHeights;
    }

    private void foldCreasePattern(CreasePattern_Worker mainCreasePatternWorker, FoldingService foldingService, ApplicationModel applicationModel) {
        foldingService.fold(FoldedFigure.EstimationOrder.ORDER_5);//引数の意味は(i_fold_type , i_suitei_meirei);

        if (!applicationModel.getSelectPersistent()) {
            mainCreasePatternWorker.unselect_all();
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1.setLayout(new GridLayoutManager(1, 30, new Insets(1, 1, 1, 1), 1, 1));
        foldButton = new JButton();
        foldButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_04.png")));
        foldButton.setText("Fold");
        panel1.add(foldButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        undoRedo = new UndoRedo();
        panel1.add(undoRedo.$$$getRootComponent$$$(), new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        oriagari_sousaButton = new JButton();
        oriagari_sousaButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_sousa.png")));
        panel1.add(oriagari_sousaButton, new GridConstraints(0, 11, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        foldedFigureMoveButton = new JButton();
        foldedFigureMoveButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_idiu.png")));
        panel1.add(foldedFigureMoveButton, new GridConstraints(0, 14, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(foldedFigureRotate.$$$getRootComponent$$$(), new GridConstraints(0, 16, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(foldedFigureResize.$$$getRootComponent$$$(), new GridConstraints(0, 18, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        foldedFigureAntiAliasButton = new JButton();
        foldedFigureAntiAliasButton.setText("a_a");
        panel1.add(foldedFigureAntiAliasButton, new GridConstraints(0, 20, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        shadowButton = new JButton();
        shadowButton.setText("S");
        panel1.add(shadowButton, new GridConstraints(0, 21, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        frontColorButton = new JButton();
        frontColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/F_color.png")));
        frontColorButton.setText("FC");
        panel1.add(frontColorButton, new GridConstraints(0, 22, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backColorButton = new JButton();
        backColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/B_color.png")));
        backColorButton.setText("BC");
        panel1.add(backColorButton, new GridConstraints(0, 23, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineColorButton = new JButton();
        lineColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/L_color.png")));
        lineColorButton.setText("LC");
        panel1.add(lineColorButton, new GridConstraints(0, 24, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        haltButton = new JButton();
        haltButton.setIcon(new ImageIcon(getClass().getResource("/ppp/keisan_tyuusi.png")));
        haltButton.setMargin(new Insets(0, 10, 0, 10));
        panel1.add(haltButton, new GridConstraints(0, 26, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        trashButton = new JButton();
        trashButton.setIcon(new ImageIcon(getClass().getResource("/ppp/settei_syokika.png")));
        trashButton.setMargin(new Insets(0, 10, 0, 10));
        panel1.add(trashButton, new GridConstraints(0, 27, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        resetButton = new JButton();
        resetButton.setIcon(new ImageIcon(getClass().getResource("/ppp/zen_syokika.png")));
        resetButton.setMargin(new Insets(0, 10, 0, 10));
        panel1.add(resetButton, new GridConstraints(0, 28, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(foldedFigureBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        oriagari_sousa_2Button = new JButton();
        oriagari_sousa_2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_sousa_2.png")));
        panel1.add(oriagari_sousa_2Button, new GridConstraints(0, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        anotherSolutionButton = new JButton();
        anotherSolutionButton.setText("a_s");
        panel1.add(anotherSolutionButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        As100Button = new JButton();
        As100Button.setText("AS100");
        panel1.add(As100Button, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        goToFoldedFigureTextField = new JTextField();
        goToFoldedFigureTextField.setColumns(2);
        panel1.add(goToFoldedFigureTextField, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        goToFoldedFigureButton = new JButton();
        goToFoldedFigureButton.setText("Go");
        panel1.add(goToFoldedFigureButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        flipButton = new JButton();
        flipButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Button0b.png")));
        panel1.add(flipButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 29, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 17, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 15, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel1.add(spacer6, new GridConstraints(0, 19, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel1.add(spacer7, new GridConstraints(0, 25, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        constraintButton = new JButton();
        constraintButton.setText("C");
        constraintButton.setToolTipText("Add Constraints");
        panel1.add(constraintButton, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        distortButton = new JButton();
        distortButton.setText("D");
        panel1.add(distortButton, new GridConstraints(0, 13, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private void createUIComponents() {
        panel1 = new JPanel();
        foldedFigureResize = new FoldedFigureResize(buttonService, foldedFigureModel, measuresModel);
        foldedFigureRotate = new FoldedFigureRotate(buttonService, foldedFigureModel, measuresModel);
        foldedFigureBox = new JComboBox<>();
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode") || e.getPropertyName().equals("i_foldedFigure_operation_mode")) {
            MouseMode m = data.getMouseMode();

            foldedFigureMoveButton.setSelected(m == MouseMode.MOVE_CALCULATED_SHAPE_102);
            oriagari_sousaButton.setSelected(data.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
            oriagari_sousa_2Button.setSelected(data.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
        }
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        foldedFigureResize.setText(String.valueOf(foldedFigureModel.getScale()));
        foldedFigureRotate.setText(String.valueOf(foldedFigureModel.getRotation()));

        frontColorButton.setIcon(new ColorIcon(foldedFigureModel.getFrontColor()));
        backColorButton.setIcon(new ColorIcon(foldedFigureModel.getBackColor()));
        lineColorButton.setIcon(new ColorIcon(foldedFigureModel.getLineColor()));

        goToFoldedFigureTextField.setText(String.valueOf(foldedFigureModel.getFoldedCases()));

        boolean findAnotherOverlapValid = foldedFigureModel.isFindAnotherOverlapValid();
        anotherSolutionButton.setEnabled(findAnotherOverlapValid);
        As100Button.setEnabled(findAnotherOverlapValid);
        goToFoldedFigureButton.setEnabled(findAnotherOverlapValid);
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigureModel.setScale(measuresModel.string2double(foldedFigureResize.getText(), foldedFigureModel.getScale()));
        foldedFigureModel.setRotation(measuresModel.string2double(foldedFigureRotate.getText(), foldedFigureModel.getRotation()));
        foldedFigureModel.setFoldedCases(StringOp.String2int(goToFoldedFigureTextField.getText(), foldedFigureModel.getFoldedCases()));
    }

    private static class IndexCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (index == -1) {
                if (list.getSelectedIndex() == -1) {
                    setText("");
                } else {
                    setText(Integer.toString(list.getSelectedIndex() + 1));
                }
            } else {
                setText(Integer.toString(index + 1));
            }

            return this;
        }
    }
}
