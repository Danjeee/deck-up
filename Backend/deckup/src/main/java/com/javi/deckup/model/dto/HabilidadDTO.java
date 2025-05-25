package com.javi.deckup.model.dto;

import java.io.Serializable;
import java.util.List;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Habilidad;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HabilidadDTO implements Serializable {

	private static final long serialVersionUID = 2L;

	private Integer id;
	
	private String nombre;
	
	private String descripcion;
	
	private Integer dmg;
	
	private String especial;
    
	private Integer heal;
    
	private Integer freeze;
    
	private String freezeName;
    
	private Integer burn;
    
	private Integer poisn;
    
	private Integer bleed;
    
	private Integer prcnt;
    
	//private EntornoDTO entorno;
    
	private Integer loadAtq;
    
	private Integer crit;
	
	private Integer critMult;
    
	private Integer leth;
    
	private Integer esq;
    
	private Integer prcntUp;
    
	private Integer prcntDwn;
	
	private String color;
	
	// No incluyo la lista de cartas porque es innecesaria
	
	public static HabilidadDTO convertToDTO(Habilidad input) {
		if (input == null) {
			return null;
		}
		return HabilidadDTO.builder()
		        .id(input.getId())
		        .nombre(input.getNombre())
		        .descripcion(input.getDescripcion())
		        .dmg(input.getDmg())
		        .especial(input.getEspecial())
		        .freeze(input.getFreeze())
		        .freezeName(input.getFreezeName())
		        .burn(input.getBurn())
		        .poisn(input.getPoisn())
		        .bleed(input.getBleed())
		        .prcnt(input.getPrcnt())
		        .color(input.getColor())
		        //.entorno(EntornoDTO.convertToDTO(input.getEntorno()))
		        .loadAtq(input.getLoadAtq())
		        .crit(input.getCrit())
		        .critMult(input.getCritMult())
		        .leth(input.getLeth())
		        .esq(input.getEsq())
		        .prcntUp(input.getPrcntUp())
		        .heal(input.getHeal())
		        .prcntDwn(input.getPrcntDwn())
		        .build();
	}
	
	public static Habilidad convertToEntity(HabilidadDTO input) {
	    return Habilidad.builder()
	        .id(input.getId())
	        .nombre(input.getNombre())
	        .descripcion(input.getDescripcion())
	        .dmg(input.getDmg())
	        //.cartas(cartas)
	        .especial(input.getEspecial())
	        .freeze(input.getFreeze())
	        .freezeName(input.getFreezeName())
	        .burn(input.getBurn())
	        .poisn(input.getPoisn())
	        .bleed(input.getBleed())
	        .color(input.getColor())
	        .prcnt(input.getPrcnt())
	        //.entorno(EntornoDTO.convertToEntity(input.getEntorno()))
	        .loadAtq(input.getLoadAtq())
	        .crit(input.getCrit())
	        .critMult(input.getCritMult())
	        .leth(input.getLeth())
	        .heal(input.getHeal())
	        .esq(input.getEsq())
	        .prcntUp(input.getPrcntUp())
	        .prcntDwn(input.getPrcntDwn())
	        .build();
	}
	
	public HabilidadDTO(Integer id) {
        this.id = id;
    }
	
}
