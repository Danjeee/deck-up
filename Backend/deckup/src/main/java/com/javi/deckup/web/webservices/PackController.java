package com.javi.deckup.web.webservices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.PaqueteDTO;
import com.javi.deckup.model.dto.RarezaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.PackService;
import com.javi.deckup.service.RarezaService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/packs")
public class PackController {

	@Autowired
	PackService ps;
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	RarezaService rs;
	
	@PostMapping("/buy")
	public Response buy(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		PaqueteDTO pack = ps.findByPacksInStore(data.getArtifact_id());
		if (user == null) {
			return Response.error("Ha habido un error con tu sesión, intentalo de nuevo");
		}
		if (pack == null) {
			return Response.error("El pack que quieres comprar no se encuentra disponible en estos momentos");
		}
		if (pack.getPrecio() > user.getCurrency()) {
			return Response.error("No tienes suficientes gemas para comprar este paquete");
		}
		Random rand = new Random();
		HashMap<Integer, List<CartaDTO>> cartas_sorted = newSortedHashMap();
		for (CartaDTO i : pack.getCartas()) {
			List<CartaDTO> lista = cartas_sorted.get(i.getRarezaDTO().getId());
			lista.add(i);
			cartas_sorted.put(i.getRarezaDTO().getId(), lista);
		}
		List<CartaDTO> cartas_finales = new ArrayList<>();
		for (Integer i = 0; i < pack.getCant();) {
			Integer chances = rand.nextInt(10000);
			if (chances < 1) { // 0.01%
				if (cartas_sorted.get(6).size() != 0) {
					cartas_finales.add(getRandom(cartas_sorted.get(6)));
					i++;
				}
	        } else if (chances < 51) { // 0.5%
	        	if (cartas_sorted.get(5).size() != 0) {
	        		cartas_finales.add(getRandom(cartas_sorted.get(5)));
					i++;
				}
	        } else if (chances < 351) { // 3%
	        	if (cartas_sorted.get(4).size() != 0) {
	        		cartas_finales.add(getRandom(cartas_sorted.get(4)));
					i++;
				}
	        } else if (chances < 1051) { // 10%
	        	if (cartas_sorted.get(3).size() != 0) {
	        		cartas_finales.add(getRandom(cartas_sorted.get(3)));
					i++;
				}
	        } else if (chances < 3051) { // 20%
	        	if (cartas_sorted.get(2).size() != 0) {
	        		cartas_finales.add(getRandom(cartas_sorted.get(2)));
					i++;
				}
	        } else { // 40%
	        	if (cartas_sorted.get(1).size() != 0) {
	        		cartas_finales.add(getRandom(cartas_sorted.get(1)));
					i++;
				}
	        }
			
		}
		
		// cartas_finales = cartas_finales.stream().map(c -> CartaDTO.builder().nombre(c.getNombre()).build()).collect(Collectors.toList());
		// return new Response(cartas_finales);
		// Descomentar para probar cambios en el codigo
		
		us.buy(user, pack.getPrecio());
		for (CartaDTO i : cartas_finales) {
			us.recive(user, i);
		}
		
		CartaDTO[] cartstosend = new CartaDTO[6];
		
		for (Integer i = 0; i<6; i++) {
			cartstosend[i] = cartas_finales.get(i);
			
		}
		
		Arrays.sort(cartstosend, Comparator.comparingInt(c -> ((CartaDTO) c).getRarezaDTO().getId()));
		
		cartas_finales = Arrays.asList(cartstosend);
		
		pack.setCartas(null);
		
		return new Response(cartas_finales, pack);
	}
	
	private CartaDTO getRandom(List<CartaDTO> cartas) {
		Random rand = new Random();
		return cartas.get(rand.nextInt(cartas.size()));
	}
	
	private HashMap<Integer, List<CartaDTO>> newSortedHashMap() {
		List<RarezaDTO> rarezas = rs.findAll();
		HashMap<Integer, List<CartaDTO>> data = new HashMap<>();
		for (RarezaDTO i : rarezas) {
			data.put(i.getId(), new ArrayList<>());
		}
		return data;
	}
	
	
}
