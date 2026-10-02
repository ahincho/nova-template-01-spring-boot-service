package pe.edu.nova.java.templates.springboot.entity;

import java.util.UUID;

/**
 * Un ítem de ejemplo: el dato de dominio que guarda el repositorio. No conoce HTTP ni JSON, así que el
 * mismo ítem sirve detrás de una API o de un consumidor de cola.
 *
 * @param id el identificador
 * @param title el título
 * @param quantity las unidades disponibles
 */
public record Item(UUID id, String title, int quantity) {}
