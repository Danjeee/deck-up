package com.javi.deckup.web.webservices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.OfertaGemaDTO;
import com.javi.deckup.model.dto.TiendaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.OfertaGemaService;
import com.javi.deckup.service.PayPalService;
import com.javi.deckup.service.TiendaService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.Session;
import com.javi.deckup.utils.UserAction;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/tienda")
public class TiendaController {
	
	@Autowired
	TiendaService ts;
	
	@Autowired
	CartaService cs;
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	OfertaGemaService gs;
	
	@Autowired
	PayPalService pps;

	@GetMapping("/get")
	public TiendaDTO get() {
		return ts.findById(1);
	}
	
	@GetMapping("/getGems")
	public List<OfertaGemaDTO> getGems() {
		return gs.findAll();
	}
	@PostMapping("/buy")
	public Response buy(@ModelAttribute UserAction data) {
		CartaDTO card = cs.findById(Math.toIntExact(data.getArtifact_id()));
		if (card == null) {
			return Response.error("No se ha encontrado la carta que quieres comprar", 404);
		}
		List<CartaDTO> cartasEnTienda = new ArrayList<>();
		TiendaDTO tienda = ts.findById(1);
		cartasEnTienda.add(tienda.getCarta1());
		cartasEnTienda.add(tienda.getCarta2());
		cartasEnTienda.add(tienda.getCarta3());
		cartasEnTienda.add(tienda.getCarta4());
		cartasEnTienda.add(tienda.getCarta5());
		if (!cartasEnTienda.contains(card)) {
			return Response.error("La carta que quieres comprar no se encuentra en la tienda");
		}
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un error con la sesión, intentalo de nuevo");
		}
		if (user.getCurrency()<card.getPrecio()) {
			return Response.error("No tienes suficientes gemas");
		} else {
			us.buy(user, card);
		}
		return Response.success("Carta añadida a tu colección");
	}
	
    @PostMapping("/pay")
    public String createPayment(@RequestParam String amount,
                                @RequestParam String currency,
                                @RequestParam String description,
                                @RequestParam String returnUrl,
                                @RequestParam String cancelUrl,
                                @RequestParam String auth,
                                @RequestParam Integer cant) {
    	UsuarioDTO user = us.findByToken(auth);
    	if (user == null) {
    		throw new RuntimeException("Error al procesar el pago: Error con el token");
    	}
        try {
            return pps.createPayment(amount, currency, description, returnUrl, cancelUrl, user, cant);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }
    
    @PostMapping("/verify")
    public Response postMethodName(@ModelAttribute UsuarioDTO aux) {
        UsuarioDTO user = us.findByToken(aux.getAuth(), true);
        if (user == null) {
        	return Response.error("Ha habido un error con tu sesión, intentalo de nuveo (No te preocupes, tu pago se ha guardado)");
        }
        Integer total = pps.getAllVerifiedPayments(user);
        
        return total == 0 ? Response.error("No tienes pagos pendientes") : Response.success(total, "Pago recibido correctamente");
    }
    

    @PostMapping("/change")
    public Response change(@ModelAttribute UserAction data) {
        UsuarioDTO user = us.findByToken(data.getUser_auth(), true);
        if (user == null) {
        	return Response.error("Ha habido un error con tu sesión");
        }
        if (!user.getRolesDTO().get(0).getNombre().equals("ADMIN")) {
        	return Response.error("Solo un administrador puede cambiar la tienda");
        }
        ts.change();
        return Response.success("Tienda cambiada con exito");
    }
	
	
	
}
