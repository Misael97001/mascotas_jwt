package com.krakedev.mascotas_jwt.services;


import com.krakedev.mascotas_jwt.entidades.Usuario;
import com.krakedev.mascotas_jwt.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;             // ← nueva importación para BCrypt
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario guardar(Usuario usuario) {
        // BCrypt.gensalt() genera una "sal" aleatoria cada vez
        // Dos usuarios con la misma clave producen hashes distintos gracias a la sal
        // BCrypt.hashpw() aplica el algoritmo → resultado: "$2a$10$xxxx..."
        // Es IRREVERSIBLE: no se puede obtener la clave original a partir del hash
        String contrasenaEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());

        usuario.setPassword(contrasenaEncriptada);   // reemplazamos la clave plana
        return usuarioRepository.save(usuario);
    }

    public boolean autenticar(String username, String password) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername(username);

        if (usuarioEncontrado.isPresent()) {
            String hashGuardado = usuarioEncontrado.get().getPassword();

            // checkpw() toma la clave recibida + el hash guardado,
            // aplica el mismo proceso y compara internamente — nunca "desencripta"
            if (BCrypt.checkpw(password, hashGuardado)) {
                return true;
            }
        }

        return false;
    }
}