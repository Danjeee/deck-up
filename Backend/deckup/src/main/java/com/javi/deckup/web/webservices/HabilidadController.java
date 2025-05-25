package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.HabilidadDTO;
import com.javi.deckup.service.HabilidadService;
import com.javi.deckup.utils.Response;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/habilidades")
public class HabilidadController {
	
	@Autowired
	HabilidadService hs;
	
	@GetMapping("/all")
	public List<HabilidadDTO> all() {
		return hs.getAll();
	}
	
	@PostMapping("/save")
	public Response save(@RequestParam("data") String dataJson) {
		try {
            ObjectMapper objectMapper = new ObjectMapper();
            HabilidadDTO hab = objectMapper.readValue(dataJson, HabilidadDTO.class);
            hs.save(hab);

            return Response.success("donete");
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().msg("Error al procesar los datos").build();
        }
	}
	
}
