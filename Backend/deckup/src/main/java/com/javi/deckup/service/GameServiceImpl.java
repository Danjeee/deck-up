package com.javi.deckup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.LineaDTO;
import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.model.dto.PlayerStatusDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.CartaRepository;
import com.javi.deckup.repository.dao.GameRepository;
import com.javi.deckup.repository.dao.LineaRepository;
import com.javi.deckup.repository.dao.PlayerStatusRepository;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.Game;
import com.javi.deckup.repository.entity.PlayerStatus;
import com.javi.deckup.repository.entity.Usuario;
import com.javi.deckup.utils.GameEvents;
import com.javi.deckup.web.websockets.NotificacionWSC;

@Service
public class GameServiceImpl implements GameService {
	
	@Autowired
	GameRepository gr;

	@Autowired
	CartaRepository cr;
	
	@Autowired
	LineaRepository lr;
	
	@Autowired
	PlayerStatusRepository ps;
	
	@Autowired
	UsuarioRepository ur;
	
	@Autowired
	NotificacionWSC ws;
	
	@Autowired
	GameEvents ge;

	@Override
	public void save(GameDTO game) {
		gr.save(GameDTO.convertToEntity(game));
	}

	@Override
	public GameDTO join(UsuarioDTO user) {
		Game game = gr.getAbleToJoin().orElse(null);
		Usuario usuario = ur.findById(user.getId()).get();
		if (game == null) {
			game = gr.save(Game.builder().build());
			game.setStatus("pendiente");
			PlayerStatus status = ps.save(PlayerStatus.builder()
					.usuario(usuario)
					.vida(40)
					.mana(1)
					.game(game)
					.build());
			game.setPlayer1(status);
		} else {
			game.setStatus("activo");
			PlayerStatus status = ps.save(PlayerStatus.builder()
					.usuario(usuario)
					.vida(40)
					.mana(1)
					.game(game)
					.carta1(cr.findById(ge.drawCard(usuario.getId(), "Id")).get())
					.carta2(cr.findById(ge.drawCard(usuario.getId(), "Id")).get())
					.carta3(cr.findById(ge.drawCard(usuario.getId(), "Id")).get())
					.carta4(cr.findById(ge.drawCard(usuario.getId(), "Id")).get())
					.carta5(cr.findById(ge.drawCard(usuario.getId(), "Id")).get())
					.build());
			game.setPlayer2(status);
			game.getPlayer1().setCarta1(cr.findById(ge.drawCard(game.getPlayer1().getUsuario().getId(), "Id")).get());
			game.getPlayer1().setCarta2(cr.findById(ge.drawCard(game.getPlayer1().getUsuario().getId(), "Id")).get());
			game.getPlayer1().setCarta3(cr.findById(ge.drawCard(game.getPlayer1().getUsuario().getId(), "Id")).get());
			game.getPlayer1().setCarta4(cr.findById(ge.drawCard(game.getPlayer1().getUsuario().getId(), "Id")).get());
			game.getPlayer1().setCarta5(cr.findById(ge.drawCard(game.getPlayer1().getUsuario().getId(), "Id")).get());
			game.setTurno(1);
			ws.norificarMatch(MensajeDTO.builder().destinoId(game.getPlayer1().getUsuario().getId()).contenido(game.getId().toString()).build());
			ws.norificarMatch(MensajeDTO.builder().destinoId(usuario.getId()).contenido(game.getId().toString()).build());
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
		Game game = gr.findById(id).orElse(null);
		return game == null ? null : GameDTO.convertToDTO(game);
	}

	@Override
	public GameDTO findByPlayer1(Long id, Boolean unstarted) {
		Game game = null;
		if (unstarted) {
			game = gr.findUnstartedByPlayer1(id).orElse(null);
		} else {
			game = gr.findByPlayer1(id).orElse(null);
		}
		return game == null ? null : GameDTO.convertToDTO(game);
	}

	@Override
	public void deleteById(Long id) {
		gr.deleteById(id);
	}

	@Override
	public void delete(GameDTO game) {
		Game game_aux = gr.findById(game.getId()).get();
		ps.delete(game_aux.getPlayer1());
		gr.delete(game_aux);
	}

	@Override
	public void save(GameDTO game, boolean notify) {
		if (notify) {
			ws.gameStatusChange(MensajeDTO.builder().contenido(gr.save(GameDTO.convertToEntity(game)).getTurno().toString()).destinoId(game.getId()).build());
		} else {
			gr.save(GameDTO.convertToEntity(game));
		}
	}

	@Override
	public LineaDTO save(LineaDTO nuevaLinea) {
		return LineaDTO.convertToDTO(lr.save(LineaDTO.convertToEntity(nuevaLinea)));
	}

	@Override
	public PlayerStatusDTO save(PlayerStatusDTO player1) {
		return PlayerStatusDTO.convertToDTO(ps.save(PlayerStatusDTO.convertToEntity(player1)));
	}
	

}
