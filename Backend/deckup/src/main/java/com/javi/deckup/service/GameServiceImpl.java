package com.javi.deckup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.GameRepository;
import com.javi.deckup.repository.dao.LineaRepository;
import com.javi.deckup.repository.dao.PlayerStatusRepository;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.Game;
import com.javi.deckup.repository.entity.PlayerStatus;
import com.javi.deckup.repository.entity.Usuario;
import com.javi.deckup.web.websockets.NotificacionWSC;

@Service
public class GameServiceImpl implements GameService {
	
	@Autowired
	GameRepository gr;
	
	@Autowired
	LineaRepository lr;
	
	@Autowired
	PlayerStatusRepository ps;
	
	@Autowired
	UsuarioRepository ur;
	
	@Autowired
	NotificacionWSC ws;

	@Override
	public void save(GameDTO game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameDTO join(UsuarioDTO user) {
		Game game = gr.getAbleToJoin().orElse(null);
		Usuario usuario = ur.findById(user.getId()).get();
		if (game == null) {
			
			game = gr.save(Game.builder().build());
			PlayerStatus status = ps.save(PlayerStatus.builder()
					.usuario(usuario)
					.vida(40)
					.mana(1)
					.game(game)
					.build());
			game.setPlayer1(status);
		} else {
			PlayerStatus status = ps.save(PlayerStatus.builder()
					.usuario(usuario)
					.vida(40)
					.mana(1)
					.game(game)
					.build());
			game.setPlayer2(status);
			ws.norificarMatch(MensajeDTO.builder().destinoId(game.getPlayer1().getUsuario().getId()).contenido("Partida encontrada").build());
			ws.norificarMatch(MensajeDTO.builder().destinoId(usuario.getId()).contenido("Partida encontrada").build());
		}
		gr.save(game);
		return GameDTO.convertToDTO(game);
	}

	@Override
	public List<GameDTO> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameDTO findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
