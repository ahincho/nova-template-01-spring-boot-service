package pe.edu.nova.java.templates.springboot;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import pe.edu.nova.java.archunit.LayeredArchitectureTest;

/** Las reglas de capas de Nova, sobre el código del servicio. */
@AnalyzeClasses(
        packages = "pe.edu.nova.java.templates.springboot",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest extends LayeredArchitectureTest {

    @Override
    protected String basePackage() {
        return "pe.edu.nova.java.templates.springboot";
    }
}
