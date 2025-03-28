package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Entorno;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntornoDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer dmg;
    private Integer heal;
    private Double prnctUp;
    private Double prcntDwn;
    private Double crit;
    private Double critDmg;
    private String especial;

    public static EntornoDTO convertToDTO(Entorno entorno) {
    	if (entorno == null) {return null;}
        return EntornoDTO.builder()
            .id(entorno.getId())
            .nombre(entorno.getNombre())
            .descripcion(entorno.getDescripcion())
            .dmg(entorno.getDmg())
            .heal(entorno.getHeal())
            .prnctUp(entorno.getPrnctUp())
            .prcntDwn(entorno.getPrcntDwn())
            .crit(entorno.getCrit())
            .critDmg(entorno.getCritDmg())
            .especial(entorno.getEspecial())
            .build();
    }

    public static Entorno convertToEntity(EntornoDTO entornoDTO) {
    	if (entornoDTO == null) {return null;}
        return Entorno.builder()
            .id(entornoDTO.getId())
            .nombre(entornoDTO.getNombre())
            .descripcion(entornoDTO.getDescripcion())
            .dmg(entornoDTO.getDmg())
            .heal(entornoDTO.getHeal())
            .prnctUp(entornoDTO.getPrnctUp())
            .prcntDwn(entornoDTO.getPrcntDwn())
            .crit(entornoDTO.getCrit())
            .critDmg(entornoDTO.getCritDmg())
            .especial(entornoDTO.getEspecial())
            .build();
    }
}