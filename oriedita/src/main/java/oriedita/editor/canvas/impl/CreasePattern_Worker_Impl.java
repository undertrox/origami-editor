package oriedita.editor.canvas.impl;

import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.OperationFrame;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.Grid;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.task.CheckCAMVTask;
import origami.Epsilon;
import origami.crease_pattern.CustomLineTypes;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Rectangle;
import origami.crease_pattern.element.StraightLine;
import origami.crease_pattern.worker.foldlineset.BranchTrim;
import origami.crease_pattern.worker.foldlineset.Check1;
import origami.crease_pattern.worker.foldlineset.Check2;
import origami.crease_pattern.worker.foldlineset.Check3;
import origami.crease_pattern.worker.foldlineset.Fix1;
import origami.crease_pattern.worker.foldlineset.Fix2;
import origami.crease_pattern.worker.foldlineset.InsideToAux;
import origami.crease_pattern.worker.foldlineset.OrganizeCircles;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for holding the current creasepattern and drawing it.
 */

public class CreasePattern_Worker_Impl implements CreasePattern_Worker {
    // ------------
    private final int check4ColorTransparencyIncrement = 10;
    private final LineSegmentSet lineSegmentSet = new LineSegmentSet();    //Instantiation of basic branch structure
    private final Camera creasePatternCamera;
    private final TaskExecutorService camvTaskExecutor;
    private final CanvasModel canvasModel;
    private final ApplicationModel applicationModel;
    private final GridModel gridModel;
    private final FoldedFigureModel foldedFigureModel;

    private final TextWorker textWorker;
    private final FileModel fileModel;
    private final FoldLineSet foldLineSet;    //Store polygonal lines
    private final Grid grid = new Grid();
    private final HistoryState historyState;
    private final HistoryState auxHistoryState;
    /**
     * Temporary line segments when drawing.
     */
    private final List<LineSegment> lineStep = new ArrayList<>();
    /**
     * Temporary circles when drawing.
     */
    private final List<Circle> circleStep = new ArrayList<>();
    /**
     * Candidate line segments.
     */
    private final List<LineSegment> lineCandidate = new ArrayList<>();
    private final Camera camera = new Camera();
    //mouseMode==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
    private final SelectedTextModel textModel;
    private double selectionDistance = 50.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Value for determining whether an input point is close to an existing point or line segment
    private int pointSize = 1;
    private LineColor lineColor;//Line segment color
    private LineColor auxLineColor = LineColor.ORANGE_4;//Auxiliary line color
    private boolean gridInputAssist = false;//1 if you use the input assist function for fine grid display, 0 if you do not use it
    private Color customCircleColor;//Stores custom colors for circles and auxiliary hot lines
    private FoldLineAdditionalInputMode i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;//= 0 is polygonal line input = 1 is auxiliary line input mode (when inputting a line segment, these two). When deleting a line segment, the value becomes as follows. = 0 is the deletion of the polygonal line, = 1 is the deletion of the auxiliary picture line, = 2 is the deletion of the black line, = 3 is the deletion of the auxiliary live line, = 4 is the folding line, the auxiliary live line and the auxiliary picture line.
    private final FoldLineSet auxLines;    //Store auxiliary lines
    private int foldLineDividingNumber = 1;
    private int numPolygonCorners = 5;
    private String s_title; //Used to hold the title that appears at the top of the frame
    private boolean check1 = false;//=0 check1を実施しない、1=実施する　　
    private boolean check2 = false;//=0 check2を実施しない、1=実施する　
    private boolean check3 = false;//=0 check3を実施しない、1=実施する　// TODO: intellij says this field is never written to, double check if check3 can be removed
    private boolean check4 = false;//=0 check4を実施しない、1=実施する　
    private boolean isSelectionEmpty;
    //---------------------------------
    private int check4ColorTransparency = 100;
    // ****************************************************************************************************************************************
    // **************　Variable definition so far　****************************************************************************************************
    // ****************************************************************************************************************************************
    // ------------------------------------------------------------------------------------------------------------
    // Sub-operation mode for MouseMode.FOLDABLE_LINE_DRAW_71, either DRAW_CREASE_FREE_1, or VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38
    //--------------------------------------------
    private CanvasModel.SelectionOperationMode i_select_mode = CanvasModel.SelectionOperationMode.NORMAL_0;//=0は通常のセレクト操作
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final OperationFrame operationFrame;

    public CreasePattern_Worker_Impl(Camera creasePatternCamera,
                                     HistoryState normalHistoryState,
                                     HistoryState auxHistoryState,
                                     FoldLineSet auxLines,
                                     FoldLineSet foldLineSet,
                                     TaskExecutorService camvTaskExecutor,
                                     CanvasModel canvasModel,
                                     ApplicationModel applicationModel,
                                     GridModel gridModel,
                                     FoldedFigureModel foldedFigureModel,
                                     FileModel fileModel,
                                     TextWorker textWorker,
                                     SelectedTextModel textModel) {
        this.creasePatternCamera = creasePatternCamera;  //コンストラクタ
        this.historyState = normalHistoryState;
        this.auxHistoryState = auxHistoryState;
        this.camvTaskExecutor = camvTaskExecutor;
        this.canvasModel = canvasModel;
        this.applicationModel = applicationModel;
        this.gridModel = gridModel;
        this.foldedFigureModel = foldedFigureModel;
        this.fileModel = fileModel;
        this.textWorker = textWorker;
        this.textModel = textModel;

        this.auxLines = auxLines;
        this.foldLineSet = foldLineSet;

        lineColor = LineColor.BLACK_0;

        this.operationFrame = new OperationFrame();

        s_title = "no title";
    }

    @Override
    public void lineStepAdd(LineSegment s) {
        LineSegment s0 = s.clone();
        s0.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
        lineStep.add(s0);
    }

    public void setGridConfigurationData(GridModel gridModel) {
        grid.setGridConfigurationData(gridModel);
        calculateDecisionWidth();
    }

    @Override
    public void clearCreasePattern() {
        foldLineSet.reset();
        auxLines.reset();
        initialize();

        camera.reset();
        lineStep.clear();
        circleStep.clear();
    }

    @Override
    public void reset() {
        foldLineSet.reset();
        auxLines.reset();

        historyState.reset();
        auxHistoryState.reset();

        camera.reset();
        lineStep.clear();
        circleStep.clear();
    }

    @Override
    public void initialize() {
        //Enter the paper square (start)
        foldLineSet.addLine(-200.0, -200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(-200.0, -200.0, 200.0, -200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, 200.0, -200.0, LineColor.BLACK_0);
        //Enter the paper square (end)
    }

    public void Memo_jyouhou_toridasi(Save memo1) {
        if (memo1.getCreasePatternCamera() != null) {
            creasePatternCamera.setCamera(memo1.getCreasePatternCamera());
        }

        if (memo1.getApplicationModel() != null) {
            applicationModel.set(memo1.getApplicationModel());
        }

        if (memo1.getCanvasModel() != null) {
            canvasModel.set(memo1.getCanvasModel());
        }

        if (memo1.getGridModel() != null) {
            gridModel.set(memo1.getGridModel());
        }

        if (memo1.getFoldedFigureModel() != null) {
            foldedFigureModel.setFrontColor(memo1.getFoldedFigureModel().getFrontColor());
            foldedFigureModel.setBackColor(memo1.getFoldedFigureModel().getBackColor());
            foldedFigureModel.setLineColor(memo1.getFoldedFigureModel().getLineColor());
        }

        textModel.reset();
    }

    public boolean isCAMVCalculationRunning() {
        return camvTaskExecutor.isTaskRunning();
    }

    public String setMemo_for_redo_undo(Save save) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        textWorker.setSave(save);
        textModel.setSelected(false);
        return foldLineSet.setSave(save);
    }

    public void setSave_for_reading(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<For reading data
        Memo_jyouhou_toridasi(memo1);
        foldLineSet.setSave(memo1);
        auxLines.setAuxSave(memo1);
        textWorker.setSave(memo1);
    }

    @Override
    public void setSave_for_reading_tuika(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<For reading data
        double addx, addy;

        FoldLineSet tempFoldLineSet = new FoldLineSet();    //追加された折線だけ取り出すために使う
        tempFoldLineSet.setSave(memo1);//追加された折線だけ取り出してori_s_tempを作る
        addx = foldLineSet.getMaxX() + 100.0 - tempFoldLineSet.getMinX();
        addy = foldLineSet.getMaxY() - tempFoldLineSet.getMaxY();

        tempFoldLineSet.move(addx, addy);//全体を移動する

        int total_old = foldLineSet.getTotal();
        Save save = SaveProvider.createInstance();
        tempFoldLineSet.getSave(save);
        foldLineSet.addSave(save);
        int total_new = foldLineSet.getTotal();
        foldLineSet.divideLineSegmentWithNewLines(total_old, total_new);

        foldLineSet.unselect_all();
        record();
    }

    @Override
    public void setSaveForPaste(Save save1) {
        int total_old = foldLineSet.getTotal();
        foldLineSet.addSave(save1);
        int total_new = foldLineSet.getTotal();
        foldLineSet.divideLineSegmentWithNewLines(total_old, total_new);

        foldLineSet.unselect_all();
        record();
    }

    @Override
    public void setAuxMemo(Save memo1) {
        auxLines.setAuxSave(memo1);
    }

    @Override
    public void allMountainValleyChange() {
        foldLineSet.allMountainValleyChange();
        checkIfNecessary();
    }

    @Override
    public void branch_trim() {
        BranchTrim.apply(foldLineSet);
    }

    @Override
    public LineSegmentSet get() {
        Save save = SaveProvider.createInstance();
        foldLineSet.getSave(save);
        lineSegmentSet.setSave(save);
        return lineSegmentSet;
    }

    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    @Override
    public int getFoldLineTotalForSelectFolding() {
        return foldLineSet.getFoldLineTotalForSelectFolding();
    }

    @Override
    public LineSegmentSet getForSelectFolding() {//selectした折線で折り畳み推定をする。
        Save save = SaveProvider.createInstance();
        foldLineSet.getSaveForSelectFolding(save);
        LineSegmentSet ls = new LineSegmentSet();
        ls.setSave(save);
        return ls;
    }

    public void calculateDecisionWidth() {
        selectionDistance = applicationModel.getMouseRadius();
        if (camera.getCameraZoomX() > 1) {
            selectionDistance = applicationModel.getMouseRadius() / camera.getCameraZoomX();
        }
    }

    @Override
    public int getTotal() {
        return foldLineSet.getTotal();
    }

    public Save getSave(String title) {
        Save save_temp = SaveProvider.createInstance();
        foldLineSet.getSave(save_temp, title);

        saveAdditionalInformation(save_temp);
        return save_temp;
    }

    public Save h_getSave() {
        Save save = SaveProvider.createInstance();
        auxLines.h_getSave(save);
        return save;
    }

    @Override
    public Save getSave_for_export() {
        Save save = SaveProvider.createInstance();
        foldLineSet.getSave(save);
        auxLines.h_getSave(save);
        saveAdditionalInformation(save);

        return save;
    }

    @Override
    public Save getSave_for_export_with_applicationModel() {
        Save save = getSave_for_export();

        save.setApplicationModel(applicationModel);

        return save;
    }

    @Override
    public void saveAdditionalInformation(Save memo1) {
        Camera camera = new Camera();
        camera.setCamera(creasePatternCamera);
        memo1.setCreasePatternCamera(camera);

        textWorker.getSave(memo1);

        memo1.setCanvasModel(canvasModel);
        memo1.setGridModel(gridModel);

        memo1.setFoldedFigureModel(foldedFigureModel);
    }

    public void setColor(LineColor i) {
        lineColor = i;
    }

    @Override
    public void point_removal() {
        foldLineSet.removePoints();
    }

    @Override
    public void overlapping_line_removal() {
        foldLineSet.removeOverlappingLines();
    }

    @Override
    public String undo() {
        s_title = setMemo_for_redo_undo(historyState.undo());
        checkIfNecessary();
        refreshIsSelectionEmpty();
        return s_title;
    }

    @Override
    public String redo() {
        s_title = setMemo_for_redo_undo(historyState.redo());
        checkIfNecessary();
        refreshIsSelectionEmpty();
        return s_title;
    }

    @Override
    public void setTitle(String s_title0) {
        s_title = s_title0;
    }

    @Override
    public void record() {
        checkIfNecessary();

        if (!historyState.isEmpty()) {
            fileModel.setSaved(false);
        }

        historyState.record(getSave(s_title));
    }

    @Override
    public void auxUndo() {
        setAuxMemo(auxHistoryState.undo());
    }

    @Override
    public void auxRedo() {
        setAuxMemo(auxHistoryState.redo());
    }

    @Override
    public void auxRecord() {
        auxHistoryState.record(h_getSave());
    }

    @Override
    public void resetCircleStep() {
        circleStep.clear();
    }

    //--------------------------------------------------------------------------------------
    //Mouse operation----------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------

    @Override
    public void addCircle(Circle e0) {
        addCircle(e0.getX(), e0.getY(), e0.getR(), e0.getColor());
    }


    //動作モデル00a--------------------------------------------------------------------------------------------------------
    //マウスクリック（マウスの近くの既成点を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（マウスの近くの既成点を選択）してから目的の処理をする雛形セット

    @Override
    public void addCircle(Point t0, double dr, LineColor ic) {
        addCircle(t0.getX(), t0.getY(), dr, ic);
    }

    @Override
    public void addCircle(double dx, double dy, double dr, LineColor ic) {
        foldLineSet.addCircle(dx, dy, dr, ic);

        int imin = 0;
        int imax = foldLineSet.numCircles() - 2;
        int jmin = foldLineSet.numCircles() - 1;
        int jmax = foldLineSet.numCircles() - 1;

        foldLineSet.applyCircleCircleIntersection(imin, imax, jmin, jmax);
        foldLineSet.applyLineSegmentCircleIntersection(1, foldLineSet.getTotal(), jmin, jmax);

    }

    @Override
    public FoldLineSet getAuxFoldLineSet() {
        return auxLines;
    }

    @Override
    public void addLineSegment_auxiliary(LineSegment s0) {
        auxLines.addLine(s0);
    }


    //動作モデル00b--------------------------------------------------------------------------------------------------------
    //マウスクリック（近くの既成点かマウス位置を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（近くの既成点かマウス位置を選択）してから目的の処理をする雛形セット

    @Override
    public void addLineSegment(LineSegment s0) {//0 = No change, 1 = Color change only, 2 = Line segment added
        foldLineSet.addLine(s0);//Just add the information of s0 to the end of senbun of foldLineSet
        int total_old = foldLineSet.getTotal();
        foldLineSet.applyLineSegmentCircleIntersection(foldLineSet.getTotal(), foldLineSet.getTotal(), 0, foldLineSet.numCircles() - 1);

        foldLineSet.divideLineSegmentWithNewLines(total_old - 1, total_old);
    }

    @Override
    public Point getClosestPoint(Point t0) {
        // When dividing paper 1/1 Only the end point of the folding line is the reference point. The grid point never becomes the reference point.
        // When dividing paper from 1/2 to 1/512 The end point of the polygonal line and the grid point in the paper frame (-200.0, -200.0 _ 200.0, 200.0) are the reference points.

        //End point of the polygonal line
        Point t1 = foldLineSet.closestPoint(t0); // foldLineSet.closestPoint returns (100000.0,100000.0) if there is no close point

        //Center of circle
        Point t3 = foldLineSet.closestCenter(t0); // foldLineSet.closestCenter returns (100000.0,100000.0) if there is no close point

        if (t0.distanceSquared(t1) > t0.distanceSquared(t3)) {
            t1 = t3;
        }

        if (grid.getBaseState() == GridModel.State.HIDDEN) {
            return t1;
        }

        if (t0.distanceSquared(t1) > t0.distanceSquared(grid.closestGridPoint(t0))) {
            return grid.closestGridPoint(t0);
        }

        return t1;
    }

    //------------------------------
    @Override
    public LineSegment getClosestLineSegment(Point t0) {
        return foldLineSet.getClosestLineSegment(t0);
    }

    //------------------------------------------------------
    @Override
    public LineSegment getClosestLineStepSegment(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;
        for (int i = imin; i <= imax; i++) {
            double sk = OritaCalc.determineLineSegmentDistance(t0, lineStep.get(i - 1));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }

        return lineStep.get(minrid - 1);
    }

    //------------------------------
    @Override
    public Circle getClosestCircleMidpoint(Point t0) {
        return foldLineSet.closestCircleMidpoint(t0);
    }

    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える
    @Override
    public Point getGridPosition(Point p0) {
        Point p = camera.TV2object(p0);
        Point closestPoint = getClosestPoint(p);
        return grid.getPosition(closestPoint);
    }

    /**
     * Used when OperationFrame is temporarily hidden.
     *
     * @param i New number of steps.
     */
    @Override
    public void resetLineStep(int i) {
        lineStep.clear();

        for (int j = 0; j < i; j++) {
            lineStepAdd(new LineSegment());
        }
    }


    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    @Override
    public int getCandidateSize() {
        return lineCandidate.size();
    }

    @Override
    public void setIsSelectionEmpty(boolean isSelectionEmpty){
        boolean oldIsSelectionEmpty = this.isSelectionEmpty;
        this.isSelectionEmpty = isSelectionEmpty;
        this.pcs.firePropertyChange("isSelectionEmpty", oldIsSelectionEmpty, isSelectionEmpty);
    }

    @Override
    public boolean getIsSelectionEmpty(){ 
        return isSelectionEmpty;
    }

    @Override
    public void refreshIsSelectionEmpty(){
        boolean latestState = foldLineSet.isSelectionEmpty();
        if(latestState != this.isSelectionEmpty){
            setIsSelectionEmpty(latestState);
        }
    }

    @Override
    public void select_all() {
        foldLineSet.select_all();
        refreshIsSelectionEmpty();
    }

    @Override
    public void unselect_all(boolean ignorePersistent) {
        if (!applicationModel.getSelectPersistent() || ignorePersistent) {
            foldLineSet.unselect_all();
            setIsSelectionEmpty(true);
        }
    }

    public void unselect_all() {
        unselect_all(true);
    }

    @Override
    public void select(Point p0a, Point p0b) {
        boolean anyLinesSelected = foldLineSet.select(createBox(p0a, p0b));
        if(anyLinesSelected) {
        	setIsSelectionEmpty(false);
        }
    }

    @Override
    public void unselect(Point p0a, Point p0b) {
        foldLineSet.unselect(createBox(p0a, p0b));
        refreshIsSelectionEmpty();
    }

    @Override
    public boolean deleteInside_foldingLine(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_foldingLine(createBox(p0a, p0b));
    }

    @Override
    public boolean deleteInside_edge(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_edge(createBox(p0a, p0b));
    }

    @Override
    public boolean deleteInside_aux(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_aux(createBox(p0a, p0b));
    }

    @Override
    public boolean insideToDeleteType(Point p0a, Point p0b, CustomLineTypes del){
        return foldLineSet.insideToDeleteType(createBox(p0a, p0b), del);
    }

    @Override
    public boolean deleteInside_text(Point p1, Point p2) {
        if (textWorker.deleteInsideRectangle(p1, p2, camera)) {
            textModel.markDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean change_property_in_4kakukei(Point p0a, Point p0b) {
        return foldLineSet.change_property_in_4kakukei(createBox(p0a, p0b), customCircleColor);
    }

    @Override
    public boolean deleteInside(Point p0a, Point p0b) {
        return auxLines.deleteInside(createBox(p0a, p0b));
    }

    @Override
    public int MV_change(Point p0a, Point p0b) {
        return foldLineSet.MV_change(createBox(p0a, p0b));
    }

    @Override
    public LineSegment extendToIntersectionPoint(LineSegment s0) {//Extend s0 from point a to b, until it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.distance(add_sen.getA());


        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_kousa_flg;
        for (var s : foldLineSet.getLineSegmentsIterable()) {
            i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(s);//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            if (i_kousa_flg.isIntersecting()) {
                kousa_point = OritaCalc.findIntersection(tyoku1, s);
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                            add_sen = new LineSegment(add_sen.getA(), kousa_point);
                        }
                    }
                }
            }
        }
        return add_sen;
    }

    //-------------------------
    @Override
    public void del_selected_senbun() {
        foldLineSet.delSelectedLineSegmentFast();
        setIsSelectionEmpty(true);
    }

    @Override
    public void v_del_all() {
        try {
            int sousuu_old = foldLineSet.getTotal();
            foldLineSet.del_V_all();
            if (sousuu_old != foldLineSet.getTotal()) {
                record();
            }
        } catch (InterruptedException e) {
            Logger.info("v_del_all aborted");
        }
    }

    @Override
    public void v_del_all_cc() {
        try {
            int sousuu_old = foldLineSet.getTotal();
            foldLineSet.del_V_all_cc();
            if (sousuu_old != foldLineSet.getTotal()) {
                record();
            }
        } catch (InterruptedException e) {
            Logger.info("v_del_all_cc aborted");
        }
    }

    @Override
    public void addPreviewLinesToCp() {//20181014
        for (LineSegment s : lineStep) {
            if (Epsilon.high.gt0(s.determineLength())) {
                LineSegment add_sen = new LineSegment(s);
                add_sen.setColor(lineColor);
                addLineSegment(add_sen);
            } else {
                addCircle(s.determineAX(), s.determineAY(), 5.0, LineColor.CYAN_3);
            }
        }
        record();
    }

    @Override
    public boolean insideToMountain(Point p0a, Point p0b) {
        return foldLineSet.insideToMountain(createBox(p0a, p0b));
    }

//20201024高密度入力がオンならばapのrepaint（画面更新）のたびにTen kus_sisuu=new Ten(mainDrawingWorker.get_moyori_ten_sisuu(p_mouse_TV_iti));で最寄り点を求めているので、この描き職人内で別途最寄り点を求めていることは二度手間になっている。

    @Override
    public boolean insideToValley(Point p0a, Point p0b) {
        return foldLineSet.insideToValley(createBox(p0a, p0b));
    }

    @Override
    public boolean insideToEdge(Point p0a, Point p0b) {
        return foldLineSet.insideToEdge(createBox(p0a, p0b));
    }

    @Override
    public boolean insideToAux(Point p0a, Point p0b) {
        return InsideToAux.apply(foldLineSet, createBox(p0a, p0b));
    }

    @Override
    public boolean insideToReplaceType(Point p0a, Point p0b, CustomLineTypes from, CustomLineTypes to){
        return foldLineSet.insideToReplaceType(createBox(p0a, p0b), from, to);
    }

    @Override
    public void setFoldLineAdditional(FoldLineAdditionalInputMode i) {
        i_foldLine_additional = i;
    }

    @Override
    public void check1() {
        Check1.apply(foldLineSet);
    }//In foldLineSet, check and set the funny fold line to the selected state.

    @Override
    public void fix1() {
        while (true) {
            if (!Fix1.apply(foldLineSet)) break;
        }
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        checkIfNecessary();
    }

    @Override
    public void set_i_check1(boolean i) {
        check1 = i;
    }

    @Override
    public void check2() {
        Check2.apply(foldLineSet);
    }

    @Override
    public void fix2() {
        Fix2.apply(foldLineSet);
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        checkIfNecessary();
    }

    private void checkIfNecessary() {
        if (check1) check1();
        if (check2) check2();
        if (check3) check3();
        if (check4) check4();
    }

    @Override
    public void check3() {
        Check3.apply(foldLineSet);
    }

    @Override
    public void check4() {
        camvTaskExecutor.executeTask(new CheckCAMVTask(foldLineSet, canvasModel));
    }

    @Override
    public void lightenCheck4Color() {
        check4ColorTransparency = check4ColorTransparency - check4ColorTransparencyIncrement;
        if (check4ColorTransparency < 50) {
            check4ColorTransparency = check4ColorTransparency + check4ColorTransparencyIncrement;
        }
        applicationModel.setCheck4ColorTransparency(check4ColorTransparency);
    }

    @Override
    public void darkenCheck4Color() {
        check4ColorTransparency = check4ColorTransparency + check4ColorTransparencyIncrement;
        if (check4ColorTransparency > 250) {
            check4ColorTransparency = check4ColorTransparency - check4ColorTransparencyIncrement;
        }
        applicationModel.setCheck4ColorTransparency(check4ColorTransparency);
    }

    @Override
    public void organizeCircles() {//Organize all circles.
        OrganizeCircles.apply(foldLineSet);
    }

    @Override
    public double getSelectionDistance() {
        return selectionDistance;
    }

    @Override
    public void setData(PropertyChangeEvent e, ApplicationModel data) {
        setGridInputAssist(data.getDisplayGridInputAssist());
        pointSize = data.getPointSize();

        setFoldLineDividingNumber(data.getFoldLineDividingNumber());
        setNumPolygonCorners(data.getNumPolygonCorners());
        setCheck4(data.getCheck4Enabled());
        setCustomCircleColor(data.getCircleCustomizedColor());

        if (e.getPropertyName() == null || e.getPropertyName().equals("check4Enabled")) {
            if (data.getCheck4Enabled()) {
                check4();
            } else if (camvTaskExecutor.isTaskRunning()) {
                camvTaskExecutor.stopTask();
            }
        }

        grid.setData(data);
    }

    @Override
    public void setData(CanvasModel data) {
        setColor(data.calculateLineColor());
        setAuxLineColor(data.calculateAuxColor());
        setFoldLineAdditional(data.getFoldLineAdditionalInputMode());
        i_select_mode = data.getSelectionOperationMode();
    }

    @Override
    public void setData(AngleSystemModel angleSystemModel) {
        unselect_all();
    }

    @Override
    public Point getCameraPosition() {
        return this.camera.getCameraPosition();
    }

    @Override
    public void selectConnected(Point p) {
        this.foldLineSet.selectProbablyConnected(p);
    }

    @Override
    public List<LineSegment> getLineStep() {
        return lineStep;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);

        calculateDecisionWidth();
    }

    @Override
    public boolean getGridInputAssist() {
        return gridInputAssist;
    }

    // ------------------------------------
    @Override
    public void setGridInputAssist(boolean i) {
        gridInputAssist = i;

        if (!gridInputAssist) {
            for (LineSegment candidate : lineCandidate) {
                candidate.deactivate();
            }
        }
    }

    private origami.crease_pattern.element.Polygon createBox(Point p0a, Point p0b) {
        Point p_a = camera.TV2object(new Point(p0a.getX(), p0a.getY()));
        Point p_b = camera.TV2object(new Point(p0a.getX(), p0b.getY()));
        Point p_c = camera.TV2object(new Point(p0b.getX(), p0b.getY()));
        Point p_d = camera.TV2object(new Point(p0b.getX(), p0a.getY()));

        return new Rectangle(p_a, p_b, p_c, p_d);
    }

    @Override
    public LineColor getLineColor() {
        return lineColor;
    }

    @Override
    public List<LineSegment> getLineCandidate() {
        return lineCandidate;
    }

    @Override
    public FoldLineSet getFoldLineSet() {
        return foldLineSet;
    }

    @Override
    public int getPointSize() {
        return pointSize;
    }

    @Override
    public List<Circle> getCircleStep() {
        return circleStep;
    }

    @Override
    public Grid getGrid() {
        return grid;
    }

    @Override
    public FoldLineAdditionalInputMode getI_foldLine_additional() {
        return i_foldLine_additional;
    }

    @Override
    public LineColor getAuxLineColor() {
        return auxLineColor;
    }

    @Override
    public void setAuxLineColor(LineColor i) {
        auxLineColor = i;
    }

    @Override
    public FoldLineSet getAuxLines() {
        return auxLines;
    }

    @Override
    public boolean isCheck1() {
        return check1;
    }

    @Override
    public boolean isCheck2() {
        return check2;
    }

    @Override
    public void setCheck2(boolean i) {
        check2 = i;
    }

    @Override
    public boolean isCheck3() {
        return check3;
    }

    @Override
    public boolean isCheck4() {
        return check4;
    }

    @Override
    public void setCheck4(boolean i) {
        check4 = i;
    }

    @Override
    public OperationFrame getOperationFrame() {
        return this.operationFrame;
    }

    @Override
    public int getNumPolygonCorners() {
        return numPolygonCorners;
    }

    @Override
    public void setNumPolygonCorners(int i) {
        numPolygonCorners = i;
        if (numPolygonCorners < 3) {
            foldLineDividingNumber = 3;
        }
    }

    @Override
    public Color getCustomCircleColor() {
        return customCircleColor;
    }

    public void setCustomCircleColor(Color c0) {
        customCircleColor = c0;
    }

    @Override
    public CanvasModel.SelectionOperationMode getI_select_mode() {
        return i_select_mode;
    }

    @Override
    public int getFoldLineDividingNumber() {
        return foldLineDividingNumber;
    }

    @Override
    public void setFoldLineDividingNumber(int i) {
        foldLineDividingNumber = i;
        if (foldLineDividingNumber < 1) {
            foldLineDividingNumber = 1;
        }
    }

    @Override
    public TextWorker getTextWorker() {
        return textWorker;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
}
