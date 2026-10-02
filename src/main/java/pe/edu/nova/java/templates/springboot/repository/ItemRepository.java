package pe.edu.nova.java.templates.springboot.repository;

import java.util.Optional;
import java.util.UUID;
import pe.edu.nova.java.templates.springboot.entity.Item;

/**
 * Los ítems guardados. Es una interfaz para que el servicio no sepa dónde viven: la plantilla no trae base
 * de datos, así que la implementación de ejemplo es en memoria, y un servicio real la reemplaza por la
 * suya, como un repositorio de Spring Data.
 */
public interface ItemRepository {

    /**
     * Guarda un ítem. Si ya existe uno con el mismo identificador, lo reemplaza.
     *
     * @param item el ítem
     * @return el ítem guardado
     */
    Item save(Item item);

    /**
     * Busca un ítem por su identificador.
     *
     * @param id el identificador
     * @return el ítem, o vacío si no existe
     */
    Optional<Item> findById(UUID id);
}
