package origami_editor.editor.databinding;

import origami.crease_pattern.element.LineColor;
import origami_editor.editor.MouseMode;
import origami_editor.editor.canvas.FoldLineAdditionalInputMode;
import origami_editor.editor.canvas.handler.MouseHandlerModifyCalculatedShape;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class CanvasModel implements Serializable {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private LineColor lineColor;
    private LineColor auxLiveLineColor;
    private MouseMode mouseMode;
    private MouseMode mouseModeAfterColorSelection;
    private FoldLineAdditionalInputMode foldLineAdditionalInputMode;
    private FoldLineAdditionalInputMode foldLineAdditionalInputMode_old;



    /**
     * Specify which operation to perform when selecting and operating the mouse. It is used to select a selected point after selection and automatically switch to the mouse operation that is premised on selection.
     */
    private SelectionOperationMode selectionOperationMode;

    private MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode foldedFigureOperationMode;

    public MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode getFoldedFigureOperationMode() {
        return foldedFigureOperationMode;
    }

    public void setFoldedFigureOperationMode(MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode foldedFigureOperationMode) {
        MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode oldI_foldedFigure_operation_mode = this.foldedFigureOperationMode;
        this.foldedFigureOperationMode = foldedFigureOperationMode;
        this.pcs.firePropertyChange("foldedFigureOperationMode", oldI_foldedFigure_operation_mode, foldedFigureOperationMode);
    }

    public boolean isCkbox_add_frame_SelectAnd3click_isSelected() {
        return ckbox_add_frame_SelectAnd3click_isSelected;
    }

    public void setCkbox_add_frame_SelectAnd3click_isSelected(boolean ckbox_add_frame_SelectAnd3click_isSelected) {
        boolean oldCkbox_add_frame_SelectAnd3click_isSelected = this.ckbox_add_frame_SelectAnd3click_isSelected;
        this.ckbox_add_frame_SelectAnd3click_isSelected = ckbox_add_frame_SelectAnd3click_isSelected;
        this.pcs.firePropertyChange("ckbox_add_frame_SelectAnd3click_isSelected", oldCkbox_add_frame_SelectAnd3click_isSelected, ckbox_add_frame_SelectAnd3click_isSelected);
    }

    private boolean ckbox_add_frame_SelectAnd3click_isSelected;

    public CanvasModel() {
        reset();
    }

    public SelectionOperationMode getSelectionOperationMode() {
        return selectionOperationMode;
    }

    public void setSelectionOperationMode(SelectionOperationMode selectionOperationMode) {
        SelectionOperationMode oldSelectionOperationMode = this.selectionOperationMode;
        this.selectionOperationMode = selectionOperationMode;
        this.pcs.firePropertyChange("selectionOperationMode", oldSelectionOperationMode, selectionOperationMode);
    }

    public void restoreFoldLineAdditionalInputMode() {
        setFoldLineAdditionalInputMode(foldLineAdditionalInputMode_old);
    }

    public FoldLineAdditionalInputMode getFoldLineAdditionalInputMode() {
        return foldLineAdditionalInputMode;
    }

    public void setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode foldLineAdditionalInputMode) {
        FoldLineAdditionalInputMode oldFoldLineAdditionalInputMode = this.foldLineAdditionalInputMode;
        this.foldLineAdditionalInputMode_old = this.foldLineAdditionalInputMode;
        this.foldLineAdditionalInputMode = foldLineAdditionalInputMode;
        this.pcs.firePropertyChange("foldLineAdditionalInputMode", oldFoldLineAdditionalInputMode, foldLineAdditionalInputMode);
    }

    public MouseMode getMouseModeAfterColorSelection() {
        return mouseModeAfterColorSelection;
    }

    public void setMouseModeAfterColorSelection(MouseMode mouseModeAfterColorSelection) {
        MouseMode oldMouseModeAfterColorSelection = this.mouseModeAfterColorSelection;
        this.mouseModeAfterColorSelection = mouseModeAfterColorSelection;
        this.pcs.firePropertyChange("mouseModeAfterColorSelection", oldMouseModeAfterColorSelection, mouseModeAfterColorSelection);
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public void setMouseMode(MouseMode mouseMode) {
        MouseMode oldMouseMode = this.mouseMode;
        this.mouseMode = mouseMode;
        this.pcs.firePropertyChange("mouseMode", oldMouseMode, mouseMode);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }


    public LineColor getLineColor() {
        return lineColor;
    }

    public void setLineColor(LineColor lineColor) {
        LineColor oldLineColor = this.lineColor;
        this.lineColor = lineColor;
        this.pcs.firePropertyChange("lineColor", oldLineColor, lineColor);
    }

    public LineColor getAuxLiveLineColor() {
        return auxLiveLineColor;
    }

    public void setAuxLiveLineColor(LineColor auxLiveLineColor) {
        LineColor oldAuxLiveLineColor = this.auxLiveLineColor;
        this.auxLiveLineColor = auxLiveLineColor;
        this.pcs.firePropertyChange("auxLiveLineColor", oldAuxLiveLineColor, auxLiveLineColor);
    }


    public void reset() {
        lineColor = LineColor.RED_1;
        auxLiveLineColor = LineColor.ORANGE_4;

        mouseMode = MouseMode.FOLDABLE_LINE_DRAW_71;
        mouseModeAfterColorSelection = MouseMode.FOLDABLE_LINE_DRAW_71;

        foldLineAdditionalInputMode = FoldLineAdditionalInputMode.POLY_LINE_0;
        foldLineAdditionalInputMode_old = FoldLineAdditionalInputMode.POLY_LINE_0;

        selectionOperationMode = SelectionOperationMode.NORMAL_0;

        ckbox_add_frame_SelectAnd3click_isSelected = false;

        this.pcs.firePropertyChange(null, null, null);
    }


    public void set(CanvasModel canvasModel) {
        lineColor = canvasModel.getLineColor();
        auxLiveLineColor = canvasModel.getAuxLiveLineColor();
        mouseMode = canvasModel.getMouseMode();
        mouseModeAfterColorSelection = canvasModel.getMouseModeAfterColorSelection();
        foldLineAdditionalInputMode = canvasModel.getFoldLineAdditionalInputMode();

        selectionOperationMode = canvasModel.getSelectionOperationMode();


        this.pcs.firePropertyChange(null, null, null);
    }

    public enum SelectionOperationMode {
        NORMAL_0,
        MOVE_1,
        MOVE4P_2,
        COPY_3,
        COPY4P_4,
        MIRROR_5,
    }
}
