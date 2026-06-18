package com.krakedev.mascotas_jwt.repositories;

import com.krakedev.mascotas_jwt.entidades.Usuario;   // importamos la clase que creamos arriba
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository   // Marca esta interfaz como componente de acceso a datos de Spring
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // JpaRepository<Usuario, Long> significa:
    //   - Usuario  → la entidad que maneja
    //   - Long     → el tipo del ID

    // JpaRepository ya nos da gratis: save(), findById(), findAll(), delete()...

    // Este método lo declara Spring automáticamente a partir del nombre:
    // "findBy" + "Username" → genera: SELECT * FROM usuarios WHERE username = ?
    Optional<Usuario> findByUsername(String username);
    // Optional evita NullPointerException si el usuario no existe
}