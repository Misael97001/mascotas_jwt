package com.krakedev.mascotas_jwt.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity                        // Le dice a JPA que esta clase representa una tabla
@Table(name = "usuarios")      // El nombre exacto de la tabla en PostgreSQL
public class Usuario {

    @Id                                                    // Esta es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // La BD genera el ID (SERIAL)
    private Long id;

    @Column(nullable = false, unique = true)   // No puede ser null y no puede repetirse
    private String username;

    @Column(nullable = false)   // No puede ser null
    private String password;    // En el Hito 1 se guarda en texto plano (sin cifrar)

    private String rol;         // "ADMIN" o "USER"

    // Constructor vacío — JPA lo necesita obligatoriamente para crear objetos
    public Usuario() {}

    // Constructor con parámetros — útil para crear usuarios desde código
    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters — JPA los usa para leer y escribir los valores
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}