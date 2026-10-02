package pe.edu.nova.java.templates.springboot;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.annotation.Masked;

/**
 * El enmascarado del starter de Nova solo toca lo que el código marca: un campo llamado {@code name} se
 * responde tal cual, y el que lleva {@code @Masked} sale enmascarado. El servicio de ejemplo no guarda datos
 * personales, así que la prueba responde un contacto propio, que existe solo aquí.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(MaskingTest.ContactController.class)
class MaskingTest {

    /**
     * Un contacto de prueba.
     *
     * @param name el nombre, sin anotación
     * @param email el correo, con la anotación
     */
    record Contact(
            String name, @Masked(type = MaskType.EMAIL) String email) {}

    /** Responde un contacto. Es una clase anidada de la prueba, así que el escaneo del servicio no la registra. */
    @RestController
    static class ContactController {

        @GetMapping("/test/contact")
        Contact contact() {
            return new Contact("Taza", "ana@acme.pe");
        }
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void aPlainNameIsAnsweredAsIsAndOnlyTheAnnotatedFieldIsMasked() throws Exception {
        mvc.perform(get("/test/contact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Taza"))
                .andExpect(jsonPath("$.data.email", containsString("*")));
    }
}
