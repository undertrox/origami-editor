package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.impl.CreasePatternRendererImpl;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
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
}
