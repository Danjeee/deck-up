package com.javi.deckup.model.dto;

import java.sql.Timestamp;

import com.javi.deckup.repository.entity.Mensaje;
import com.javi.deckup.repository.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MensajeDTO {
	private Long id;
	private Timestamp fechaEnvio;
    private String contenido;
    private Long usuarioId;
    private Long destinoId;
    private Boolean leido;
    
    public static MensajeDTO convertToDTO(Mensaje input) {
    	return MensajeDTO.builder()
    					 .id(input.getId())
    					 .fechaEnvio(input.getFechaEnvio())
    					 .contenido(input.getContenido())
    					 .usuarioId(input.getUsuario().getId())
    					 .destinoId(input.getDestino().getId())
    					 .leido(input.getLeido())
    					 .build();
    }
    
    public static Mensaje convertToEntity(MensajeDTO input) {
    	return Mensaje.builder()
    					 .id(input.getId())
    					 .fechaEnvio(input.getFechaEnvio())
    					 .contenido(input.getContenido())
    					 .usuario(Usuario.builder().id(input.getUsuarioId()).build())
    					 .destino(Usuario.builder().id(input.getDestinoId()).build())
    					 .leido(input.getLeido())
    					 .build();
    }
}
