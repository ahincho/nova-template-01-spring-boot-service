package pe.edu.nova.java.templates.springboot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * El contrato de la API de ítems: el sobre de Nova en el éxito y los errores por capas de ADR-031 en el
 * fallo. Corre el servicio completo, con los starters de la plataforma, y no necesita ninguna base ni
 * servicio externo.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ItemApiTest {

    private static final String ITEMS = "/v1/items";

    @Autowired
    private MockMvc mvc;

    @Test
    void anItemIsCreatedAndReadBackInsideTheEnvelope() throws Exception {
        MvcResult created = mvc.perform(
                        post(ITEMS).contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": "Taza", "quantity": 3}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.title").value("Taza"))
                .andExpect(jsonPath("$.data.quantity").value(3))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andReturn();
        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.data.id");
        assertThat(created.getResponse().getHeader(HttpHeaders.LOCATION)).endsWith(ITEMS + "/" + id);

        mvc.perform(get(ITEMS + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.title").value("Taza"))
                .andExpect(jsonPath("$.data.quantity").value(3));
    }

    @Test
    void aMissingItemIs404WithItsOwnCodeAndTheTraceId() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(get(ITEMS + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors[0].code").value("ITEM_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0].message").value("El ítem " + id + " no existe"))
                .andExpect(jsonPath("$.metadata.traceId").isNotEmpty());
    }

    @Test
    void anInvalidItemIs400WithAnErrorPerField() throws Exception {
        mvc.perform(post(ITEMS).contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": " ", "quantity": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[*].code", everyItem(is("BAD_REQUEST"))))
                .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("title", "quantity")))
                .andExpect(jsonPath("$.errors[?(@.field == 'title')].message", contains("El título es obligatorio")))
                .andExpect(jsonPath(
                        "$.errors[?(@.field == 'quantity')].message", contains("La cantidad debe ser mayor que cero")))
                .andExpect(jsonPath("$.metadata.traceId").isNotEmpty());
    }

    @Test
    void anItemWithoutAQuantityIs400() throws Exception {
        mvc.perform(post(ITEMS).contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": "Taza"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("quantity"))
                .andExpect(jsonPath("$.errors[0].message").value("La cantidad es obligatoria"));
    }
}
