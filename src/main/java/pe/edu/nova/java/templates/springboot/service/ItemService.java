package pe.edu.nova.java.templates.springboot.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import pe.edu.nova.java.libs.api.standard.error.DomainError;
import pe.edu.nova.java.templates.springboot.entity.Item;
import pe.edu.nova.java.templates.springboot.repository.ItemRepository;

/**
 * Crea y consulta ítems. Lanza lo que salió mal como un error de Nova (ADR-031), no como un status HTTP:
 * quién lo responde, y cómo, lo decide la plataforma.
 */
@Service
public class ItemService {

    /** El código propio del error de dominio de un ítem que no existe. */
    public static final String ITEM_NOT_FOUND = "ITEM_NOT_FOUND";

    private final ItemRepository items;

    /**
     * Crea el servicio.
     *
     * @param items los ítems guardados
     */
    public ItemService(ItemRepository items) {
        this.items = items;
    }

    /**
     * Crea un ítem con un identificador nuevo.
     *
     * @param title el título
     * @param quantity las unidades disponibles
     * @return el ítem guardado
     */
    public Item create(String title, int quantity) {
        return items.save(new Item(UUID.randomUUID(), title, quantity));
    }

    /**
     * Busca un ítem.
     *
     * @param id el identificador
     * @return el ítem
     * @throws DomainError {@code ITEM_NOT_FOUND}, si no existe; la plataforma lo responde como un 404
     */
    public Item find(UUID id) {
        return items.findById(id)
                .orElseThrow(() -> DomainError.notFound(ITEM_NOT_FOUND, "El ítem " + id + " no existe"));
    }
}
