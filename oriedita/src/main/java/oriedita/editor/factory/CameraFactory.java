package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.drawing.tools.Camera;

public class CameraFactory {

    @Produces
    @ApplicationScoped
    @Named("mainCameraModel")
    public static CameraModel mainCameraModel() {
        return new CameraModel();
    }

    @Produces
    @ApplicationScoped
    @Named("exportCameraModel")
    public static CameraModel exportCameraModel() {
        return new CameraModel();
    }

    @Produces
    @ApplicationScoped
    @Named("creasePatternCamera")
    public static Camera creasePatternCamera(
            @Named("mainCameraModel") CameraModel cameraModel
    ) {
        Camera creasePatternCamera = new Camera();

        cameraModel.addPropertyChangeListener(e -> {
            creasePatternCamera.setCameraAngle(cameraModel.getRotation());
            creasePatternCamera.setCameraZoomX(cameraModel.getScale());
            creasePatternCamera.setCameraZoomY(cameraModel.getScale());
        });


        return creasePatternCamera;
    }

    @Produces
    @ApplicationScoped
    @Named("exportCreasePatternCamera")
    public static Camera exportCreasePatternCamera(
            @Named("exportCameraModel") CameraModel cameraModel
    ) {
        Camera creasePatternCamera = new Camera();

        cameraModel.addPropertyChangeListener(e -> {
            creasePatternCamera.setCameraAngle(cameraModel.getRotation());
            creasePatternCamera.setCameraZoomX(cameraModel.getScale());
            creasePatternCamera.setCameraZoomY(cameraModel.getScale());
        });


        return creasePatternCamera;
    }

}
