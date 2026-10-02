package pe.edu.nova.java.templates.springboot;

import pe.edu.nova.java.starters.boot.NovaApplication;
import pe.edu.nova.java.starters.boot.NovaSpringBootApplication;

/** El servicio de ejemplo de la plantilla: un recurso, {@code items}, organizado en las capas de Nova. */
@NovaSpringBootApplication
public class ServiceApplication {

    /** Crea la clase de arranque; la instancia Spring. */
    public ServiceApplication() {}

    /**
     * Arranca el servicio.
     *
     * @param args los argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        NovaApplication.run(ServiceApplication.class, args);
    }
}
