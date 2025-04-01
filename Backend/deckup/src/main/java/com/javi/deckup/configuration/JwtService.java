package com.javi.deckup.configuration;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private static final Logger log = (Logger) LoggerFactory.getLogger(JwtService.class);
	
	@Value("${security.jwt.secret}")
	private String secretKey;
	
	@Value("${security.jwt.expiration}")
	private long jwtExpiration;

	/**
	 * Extrae el nombre de usuario del token
	 *
	 * @param token token de donde extaer el usuario
	 * @return nombre de usuario
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extrae una Claim específica del token usando un resolutor
	 *
	 * @param <T>            tipo de la Claim
	 * @param token          token de donde extraer la Claim
	 * @param claimsResolver resolutor de la Claim
	 * @return Claim extraida
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Genera un token para un usuario
	 *
	 * @param userDetails usuario para el que se genera el token
	 * @return token generado
	 */
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	/**
	 * Genera un token para un usuario con Claims adicionales
	 *
	 * @param extraClaims Claims adicionales
	 * @param userDetails usuario para el que se genera el token
	 * @return token generado
	 */
	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, jwtExpiration);
	}

	/**
	 * Devuelve el tiempo de expiracion del token
	 *
	 * @return tiempo de expiracion del token
	 */
	public long getExpirationTime() {
		return jwtExpiration;
	}

	/**
	 * Construye el token JWT
	 *
	 * @param extraClaims Claims adicionales
	 * @param userDetails usuario para el que se genera el token
	 * @param expiration  tiempo de expiracion del token
	 * @return token generado
	 */
	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Comprueba si un token es valido para un usuario
	 *
	 * @param token       token a validar
	 * @param userDetails usuario para el que se valida el token
	 * @return true si el token es valido, false en caso contrario
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
		log.info(this.getClass().getSimpleName() + " isTokenValid: token validado para el usuario {}: {}", username,
				isValid);
		return isValid;
	}

	/**
	 * Comprueba si un token a expirado
	 *
	 * @param token token a comprobar
	 * @return true si el token a expirado, false en caso contrario
	 */
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Extrae la fecha de expiracion del token
	 * 
	 * @param token token de donde extraer la fecha de expiracion
	 * @return fecha de expiracion
	 */
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extrae todas las Claims del token
	 *
	 * @param token token de donde extraer las Claims
	 * @return Claims extraidas
	 */
	private Claims extractAllClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
		} catch (Exception e) {
			log.error(" extractAllClaims: fallo al extraer todas las Claims del token: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Devuelve la clave secreta para firmar el token
	 *
	 * @return clave secreta
	 */
	private Key getSignInKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
}
