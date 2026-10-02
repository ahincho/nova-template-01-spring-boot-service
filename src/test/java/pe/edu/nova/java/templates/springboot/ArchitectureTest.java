package pe.edu.nova.java.templates.springboot;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import pe.edu.nova.java.archunit.LayeredArchitectureTest;

/** Las reglas de capas de Nova, sobre el código del servicio. */
@AnalyzeClasses(packages = ArchitectureTest.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest extends LayeredArchitectureTest {

    /**
     * El paquete del servicio, escrito una sola vez. Con el nombre dentro de la anotación, un paquete más corto
     * cambia cómo el formateador parte la línea y el chequeo de formato falla tras renombrarlo.
     */
    static final String BASE_PACKAGE = "pe.edu.nova.java.templates.springboot";

    @Override
    protected String basePackage() {
        return BASE_PACKAGE;
    }
}
