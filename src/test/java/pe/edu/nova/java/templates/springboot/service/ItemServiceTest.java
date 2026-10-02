package pe.edu.nova.java.templates.springboot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import pe.edu.nova.java.libs.api.standard.error.DomainError;
import pe.edu.nova.java.templates.springboot.entity.Item;
import pe.edu.nova.java.templates.springboot.repository.InMemoryItemRepository;

/**
 * El caso de uso, sin Spring ni HTTP. El servicio lanza un error de dominio y no sabe qué status es: por eso
 * se prueba con un repositorio en memoria y sin levantar nada.
 */
class ItemServiceTest {

    private final ItemService service = new ItemService(new InMemoryItemRepository());

    @Test
    void anItemIsFoundAfterBeingCreated() {
        Item created = service.create("Taza", 3);

        assertThat(service.find(created.id())).isEqualTo(created);
    }

    @Test
    void aMissingItemThrowsTheDomainErrorWithItsOwnCode() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> service.find(id)).isInstanceOfSatisfying(DomainError.class, error -> {
            assertThat(error.type()).isEqualTo(DomainError.Type.NOT_FOUND);
            assertThat(error.code()).contains(ItemService.ITEM_NOT_FOUND);
            assertThat(error).hasMessage("El ítem " + id + " no existe");
        });
    }
}
