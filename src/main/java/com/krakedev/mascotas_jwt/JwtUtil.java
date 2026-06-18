package com.krakedev.mascotas_jwt;   // paquete raíz del proyecto

import java.util.Date;   // ← usa java.util.Date, NO java.sql.Date
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JwtUtil {

    // Clave secreta para firmar el token. En producción va en variables de entorno.
    private static final String CLAVE_SECRETA = "EstaEsUnaClaveSecretaMuyLarga123456";

    // Identifica quién generó el token (se verifica al validar)
    private static final String EMISOR = "krakeDevBackend";

    // 1 800 000 milisegundos = 30 minutos (como pide el PDF)
    private static final long TIEMPO_EXPIRACION = 1800000L;

    public static String generarToken(String username, String rol) {

        // HMAC256 usa la clave secreta para firmar digitalmente el token
        Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

        long tiempoActual = System.currentTimeMillis();
        Date fechaExpiracion = new Date(tiempoActual + TIEMPO_EXPIRACION);

        String tokenGenerado = JWT.create()
            .withIssuer(EMISOR)                      // claim "iss" → quién emite
            .withSubject(username)                   // claim "sub" → el username
            .withIssuedAt(new Date(tiempoActual))    // claim "iat" → cuándo se emitió
            .withExpiresAt(fechaExpiracion)          // claim "exp" → cuándo expira
            .withClaim("rol", rol)                   // claim personalizado con el rol
            .sign(algoritmo);                        // firma el token con HMAC256

        // El token resultante tiene 3 partes separadas por puntos:
        // eyJhbGciOiJIUzI1NiJ9  ← header (algoritmo)
        // eyJzdWIiOiJjYXJsb3MifQ ← payload (los claims)
        // xK9mD3...              ← firma digital
        return tokenGenerado;
    }

    public static DecodedJWT validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

            // Construimos el verificador: comprueba firma + emisor + expiración
            JWTVerifier verificador = JWT.require(algoritmo)
                    .withIssuer(EMISOR)
                    .build();

            // verify() lanza excepción si el token fue modificado, expiró o es falso
            DecodedJWT tokenDecodificado = verificador.verify(token);
            return tokenDecodificado;   // retorna los datos del token si es válido

        } catch (Exception e) {
            return null;   // cualquier problema → retornamos null (inválido)
        }
    }
}