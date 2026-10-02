package pe.edu.nova.java.templates.springboot.dto;

import java.util.UUID;

/**
 * Un ítem, como lo ve el cliente. El starter de Nova lo envuelve en {@code data}.
 *
 * @param id el identificador
 * @param title el título
 * @param quantity las unidades disponibles
 */
public record ItemResponse(UUID id, String title, int quantity) {}
