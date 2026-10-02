package pe.edu.nova.java.templates.springboot.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.nova.java.templates.springboot.dto.CreateItemRequest;
import pe.edu.nova.java.templates.springboot.dto.ItemResponse;
import pe.edu.nova.java.templates.springboot.entity.Item;
import pe.edu.nova.java.templates.springboot.service.ItemService;

/**
 * La API de ítems. El controlador solo traduce HTTP: valida la entrada, llama al servicio y arma la
 * respuesta. No decide ningún status de error, porque el starter de Nova responde lo que lanza el servicio.
 */
@RestController
@RequestMapping("/v1/items")
public class ItemController {

    private final ItemService service;

    /**
     * Crea el controlador.
     *
     * @param service el servicio de ítems
     */
    public ItemController(ItemService service) {
        this.service = service;
    }

    /**
     * Crea un ítem.
     *
     * @param request el ítem; si no es válido, el starter responde un 400 con un error por campo
     * @return el ítem creado, con 201 y su dirección en {@code Location}
     */
    @PostMapping
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody CreateItemRequest request) {
        Item item = service.create(request.title(), request.quantity());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(item.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(item));
    }

    /**
     * Devuelve un ítem.
     *
     * @param id el identificador
     * @return el ítem; si no existe, el error de dominio del servicio responde un 404
     */
    @GetMapping("/{id}")
    public ItemResponse find(@PathVariable UUID id) {
        return toResponse(service.find(id));
    }

    private static ItemResponse toResponse(Item item) {
        return new ItemResponse(item.id(), item.title(), item.quantity());
    }
}
