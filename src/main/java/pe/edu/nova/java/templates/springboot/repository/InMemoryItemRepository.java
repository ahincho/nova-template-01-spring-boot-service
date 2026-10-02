package pe.edu.nova.java.templates.springboot.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import pe.edu.nova.java.templates.springboot.entity.Item;

/** Los ítems en memoria, para que la plantilla funcione sin una base de datos. Se pierden al reiniciar. */
@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<UUID, Item> items = new ConcurrentHashMap<>();

    /** Crea el repositorio vacío; lo instancia Spring. */
    public InMemoryItemRepository() {}

    @Override
    public Item save(Item item) {
        items.put(item.id(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(UUID id) {
        return Optional.ofNullable(items.get(id));
    }
}
