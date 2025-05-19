package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.RarezaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.PlayerCardsService;
import com.javi.deckup.service.RarezaService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/cards")
public class CartaController {

	@Autowired
	CartaService cs;
	
	@Autowired
	PlayerCardsService ps;
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	RarezaService rs;
	
	@GetMapping("/all")
	public List<CartaDTO> getAll() {
		return cs.findAll();
	}
	
	@GetMapping("/rarezas")
	public List<RarezaDTO> getRarezas() {
		return rs.findAll();
	}
	
	@PostMapping("/getByPlayer")
	public List<PlayerCardsDTO> getByPlayer(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		return ps.getAllByUser(user.getId());
	}
	
	@GetMapping("/getOf/{id}")
	public List<PlayerCardsDTO> getOf(@PathVariable("id") Long id) {
		UsuarioDTO user = us.findById(id);
		return ps.getAllByUser(user.getId());
	}
	
	@PostMapping("/save")
	public Response save(@RequestParam("data") String dataJson) {
		try {
            ObjectMapper objectMapper = new ObjectMapper();
            CartaDTO carta = objectMapper.readValue(dataJson, CartaDTO.class);
            if (carta.getPaqueteDTO().getId() == -1) {
            	carta.setPaqueteDTO(null);
            }
            cs.save(carta);

            // Devuelve la respuesta con la lista de cartas
            return Response.builder().cartas(List.of(carta)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().msg("Error al procesar los datos").build();
        }
	}
	
	
}
