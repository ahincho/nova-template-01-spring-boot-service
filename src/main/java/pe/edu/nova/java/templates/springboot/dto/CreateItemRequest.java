package pe.edu.nova.java.templates.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Lo que el cliente manda para crear un ítem. Los mensajes de las restricciones los lee una persona, así
 * que van en español; el starter de Nova los responde en un error por campo.
 *
 * @param title el título, de hasta 100 caracteres
 * @param quantity las unidades disponibles, mayores que cero
 */
public record CreateItemRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede pasar de {max} caracteres")
        String title,

        @NotNull(message = "La cantidad es obligatoria") @Positive(message = "La cantidad debe ser mayor que cero")
        Integer quantity) {}
