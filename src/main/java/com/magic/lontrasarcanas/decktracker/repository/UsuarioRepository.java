package com.magic.lontrasarcanas.decktracker.repository;

import com.magic.lontrasarcanas.decktracker.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // O Spring Security precisa buscar o usuário pelo login para validar a senha
    UserDetails findByLogin(String login);
}
