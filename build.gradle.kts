plugins {
    // El toolchain de Java de Nova (ADR-044): Java 25, Spring Boot con su BOM, formato, Checkstyle,
    // cobertura, validación de commits, OWASP, el SBOM y la imagen del contenedor. Trae el estándar de
    // API y el enmascarado, y las reglas de arquitectura para las pruebas.
    id("pe.edu.nova.java.spring-boot-service") version "2.0.0"
}

dependencies {
    // El meta-starter: Spring Boot web, Jackson y Actuator, más el estándar de API con los errores por
    // capas (ADR-031), el enmascarado, la observabilidad y los secretos.
    implementation("pe.edu.nova.java.starters:nova-spring-boot-starter:2.0.0")
    // Bean Validation. El meta-starter no lo declara: solo llega de rebote, por la observabilidad, y un
    // servicio no debe apoyarse en una dependencia que otro starter trae sin decirlo.
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
