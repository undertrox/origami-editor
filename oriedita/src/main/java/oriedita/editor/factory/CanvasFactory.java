package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.Canvas;
import oriedita.editor.CanvasUI;
import oriedita.editor.FrameProvider;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.NamedApplicationModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.CreasePatternRenderer;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.MouseModeHandler;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FoldedFigureCanvasSelectService;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.swing.component.BulletinBoard;


@ApplicationScoped
public class CanvasFactory {
    @Produces
    @Dependent
    @Named("main_CanvasUI")
    public static CanvasUI mainCanvasUI(
            @Named("creasePatternCamera") Camera creasePatternCamera,
            @Named("foldingExecutor") TaskExecutorService foldingExecutor,
            BackgroundModel backgroundModel,
            CanvasModel canvasModel,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            @Named("mainCreasePattern_Renderer") CreasePatternRenderer mainCreasePatternRenderer,
            AnimationService animationService,
            ApplicationModel applicationModel,
            BulletinBoard bulletinBoard,
            FoldedFigureModel foldedFigureModel,
            FoldedFiguresList foldedFiguresList
    ) {
        return new CanvasUI(creasePatternCamera, foldingExecutor,
                backgroundModel, canvasModel,
                mainCreasePatternWorker, mainCreasePatternRenderer, animationService,
                applicationModel, bulletinBoard, foldedFigureModel, foldedFiguresList);
    }

    @Produces
    @Dependent
    @Named("export_canvasUI")
    public static CanvasUI exportCanvasUI(
            @Named("exportCreasePatternCamera") Camera creasePatternCamera,
            @Named("foldingExecutor") TaskExecutorService foldingExecutor,
            BackgroundModel backgroundModel,
            CanvasModel canvasModel,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            @Named("export_mainCreasePattern_Renderer") CreasePatternRenderer mainCreasePatternRenderer,
            AnimationService animationService,
            @Named("export_ApplicationModel") NamedApplicationModel applicationModel,
            BulletinBoard bulletinBoard,
            FoldedFigureModel foldedFigureModel,
            FoldedFiguresList foldedFiguresList
    ) {
        return new CanvasUI(creasePatternCamera, foldingExecutor,
                backgroundModel, canvasModel,
                mainCreasePatternWorker, mainCreasePatternRenderer, animationService,
                applicationModel, bulletinBoard, foldedFigureModel, foldedFiguresList);
    }

    @Produces
    @ApplicationScoped
    @Named("main_Canvas")
    public static Canvas mainCanvas(
            @Named("creasePatternCamera") Camera creasePatternCamera,
            FrameProvider frameProvider,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            @Named("main_CanvasUI") Instance<CanvasUI> canvasUIProvider,
            FoldedFiguresList foldedFiguresList,
            BackgroundModel backgroundModel,
            BulletinBoard bulletinBoard,
            ApplicationModel applicationModel,
            @Named("mainCameraModel") CameraModel creasePatternCameraModel,
            FoldedFigureModel foldedFigureModel,
            GridModel gridModel,
            @Any Instance<MouseModeHandler> handlerList,
            AngleSystemModel angleSystemModel,
            FoldedFigureCanvasSelectService foldedFigureCanvasSelectService,
            @Any CanvasModel canvasModel,
            TextWorker textWorker,
            SelectedTextModel textModel,
            AnimationService animationService,
            ButtonService buttonService
    ) {
        return new Canvas(
                creasePatternCamera, frameProvider, mainCreasePatternWorker,
                canvasUIProvider, foldedFiguresList, backgroundModel,
                bulletinBoard, applicationModel, creasePatternCameraModel,
                foldedFigureModel, gridModel, handlerList, angleSystemModel,
                foldedFigureCanvasSelectService, canvasModel, textWorker,
                textModel, animationService, buttonService);
    }

    @Produces
    @ApplicationScoped
    @Named("export_Canvas")
    public static Canvas exportCanvas(
            @Named("exportCreasePatternCamera") Camera creasePatternCamera,
            FrameProvider frameProvider,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            @Named("export_canvasUI") Instance<CanvasUI> canvasUIProvider,
            FoldedFiguresList foldedFiguresList,
            BackgroundModel backgroundModel,
            BulletinBoard bulletinBoard,
            @Named("export_ApplicationModel") NamedApplicationModel applicationModel,
            @Named("exportCameraModel") CameraModel creasePatternCameraModel,
            FoldedFigureModel foldedFigureModel,
            GridModel gridModel,
            @Any Instance<MouseModeHandler> handlerList,
            AngleSystemModel angleSystemModel,
            FoldedFigureCanvasSelectService foldedFigureCanvasSelectService,
            @Any CanvasModel canvasModel,
            TextWorker textWorker,
            SelectedTextModel textModel,
            AnimationService animationService,
            ButtonService buttonService
    ) {
        return new Canvas(
                creasePatternCamera, frameProvider, mainCreasePatternWorker,
                canvasUIProvider, foldedFiguresList, backgroundModel,
                bulletinBoard, applicationModel, creasePatternCameraModel,
                foldedFigureModel, gridModel, handlerList, angleSystemModel,
                foldedFigureCanvasSelectService, canvasModel, textWorker,
                textModel, animationService, buttonService);
    }
}
