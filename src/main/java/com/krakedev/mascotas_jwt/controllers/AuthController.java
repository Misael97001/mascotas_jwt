package com.krakedev.mascotas_jwt.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.krakedev.mascotas_jwt.entidades.Usuario;
import com.krakedev.mascotas_jwt.repositories.UsuarioRepository;
import com.krakedev.mascotas_jwt.services.UsuarioService;
import com.krakedev.mascotas_jwt.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController // Combina @Controller + @ResponseBody (responde JSON automático)
@RequestMapping("/auth") // Todas las rutas de esta clase empiezan con /auth
public class AuthController {

	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepository;

	// Spring inyecta ambas dependencias por constructor
	public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
		this.usuarioService = usuarioService;
		this.usuarioRepository = usuarioRepository;
	}

	// Escucha: POST /auth/registrar
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		// @RequestBody convierte el JSON del body → objeto Usuario automáticamente
		try {
			Usuario usuarioNuevo = usuarioService.guardar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);
			// 201 Created + el usuario guardado en el body
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar usuario: " + e.getMessage());
			// 500 si algo falla (ej: username duplicado)
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
		String username = credenciales.get("username");
		String password = credenciales.get("password");

		boolean autenticado = usuarioService.autenticar(username, password);

		if (autenticado) {
			// Buscamos el usuario para leer su rol
			Usuario usuario = usuarioRepository.findByUsername(username).get();

			// Generamos el JWT con el username y el rol
			String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

			// Respondemos con JSON: {"token": "eyJ..."}
			return ResponseEntity.ok(Map.of("token", token));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrecta");
		}
	}

	@GetMapping("/perfil") // Escucha: GET /auth/perfil
	public ResponseEntity<?> verPerfil(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		// @RequestHeader extrae el header "Authorization" del request HTTP
		// required = false → si no viene el header, no lanza error automático (lo
		// manejamos nosotros)

		// Verificamos que el header exista y tenga el prefijo "Bearer "
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Acceso denegado: debes enviar el token en el header Authorization");
		}

		// "Bearer eyJ..." → quitamos los 7 primeros caracteres para quedarnos solo con
		// el token
		String token = authHeader.substring(7);

		// Validamos el token: firma, emisor y expiración
		DecodedJWT datosToken = JwtUtil.validarToken(token);

		if (datosToken == null) {
			// El token fue modificado, expiró o es inválido
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado: token inválido o expirado");
		}

		// Extraemos los datos que guardamos al generar el token
		String username = datosToken.getSubject(); // claim "sub" → username
		String rol = datosToken.getClaim("rol").asString(); // claim "rol" → ADMIN o USER

		return ResponseEntity.ok(Map.of("mensaje", "Bienvenido al sistema protegido por JWT", "usuario", username,
				"rol", rol, "estatus", "autenticado exitosamente"));
	}
}