package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
	private Long id;
    private UsuarioDTO user;
    private String title;
    private String msg;
    private Integer currency;
    private CartaDTO card;
    private Integer cardCant;
    private Boolean claimed;
    
    public static NotificationDTO convertToDTO(Notification input) {
    	return NotificationDTO.builder()
    						  .id(input.getId())
    						  .user(UsuarioDTO.convertToDTO(input.getUser()))
    						  .title(input.getTitle())
    						  .msg(input.getMsg())
    						  .currency(input.getCurrency())
    						  .card(CartaDTO.convertToDTO(input.getCard()))
    						  .cardCant(input.getCardCant())
    						  .claimed(input.getClaimed())
    						  .build();
    }
    
    public static Notification convertToEntity(NotificationDTO input) {
    	Carta card = null;
    	if (input.getCard() != null) {
    		card = Carta.builder().id(input.getCard().getId()).build();
    	}
    	return Notification.builder()
    						  .id(input.getId())
    						  .user(UsuarioDTO.convertToEntity(input.getUser()))
    						  .title(input.getTitle())
    						  .msg(input.getMsg())
    						  .currency(input.getCurrency())
    						  .card(card)
    						  .cardCant(input.getCardCant())
    						  .claimed(input.getClaimed())
    						  .build();
    }
}
