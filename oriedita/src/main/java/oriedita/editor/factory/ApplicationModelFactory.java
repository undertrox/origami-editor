package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.NamedApplicationModel;

public class ApplicationModelFactory {

    @Produces
    @ApplicationScoped
    public static ApplicationModel appModel() {
        return new ApplicationModel();
    }

    @Produces
    @ApplicationScoped
    @Named("export_ApplicationModel")
    public static NamedApplicationModel exportApplicationModel() {
        return new NamedApplicationModel();
    }

}
