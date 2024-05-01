package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.impl.CreasePatternRendererImpl;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.NamedApplicationModel;
import oriedita.editor.drawing.CreasePatternRenderer;
import oriedita.editor.drawing.tools.Camera;

public class CreasePatternRendererFactory {
    @Produces
    @ApplicationScoped
    @Named("mainCreasePattern_Renderer")
    public CreasePatternRenderer mainCpRenderer(
            @Named("mainCreasePattern_Worker")CreasePattern_Worker cpWorker,
            ApplicationModel applicationModel,
            CanvasModel canvasModel,
            @Named("creasePatternCamera") Camera creasePatternCamera
            ) {
        return new CreasePatternRendererImpl(cpWorker, applicationModel, canvasModel, creasePatternCamera);
    }

    @Produces
    @ApplicationScoped
    @Named("export_mainCreasePattern_Renderer")
    public CreasePatternRenderer exportMainCpRenderer(
            @Named("mainCreasePattern_Worker")CreasePattern_Worker cpWorker,
            @Named("export_ApplicationModel") NamedApplicationModel applicationModel,
            CanvasModel canvasModel,
            @Named("exportCreasePatternCamera") Camera creasePatternCamera
    ) {
        return new CreasePatternRendererImpl(cpWorker, applicationModel, canvasModel, creasePatternCamera);
    }
}
