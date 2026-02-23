package com.magic.lontrasarcanas.decktracker;

import com.magic.lontrasarcanas.decktracker.model.Usuario;
import com.magic.lontrasarcanas.decktracker.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Conta quantos usuários existem. Se for zero, ele cria a galera!
        if (usuarioRepository.count() == 0) {
            
            // 1. O SEU SUPERUSUÁRIO
            Usuario admin = new Usuario();
            admin.setLogin("tales_terezu"); 
            admin.setSenha(passwordEncoder.encode("Terezu240")); // O passwordEncoder faz a mágica da criptografia aqui!
            admin.setRole("ROLE_ADMIN");

            // 2. USUÁRIO 1
            Usuario user1 = new Usuario();
            user1.setLogin("luiz_lima");
            user1.setSenha(passwordEncoder.encode("jeskai_control_quase_bom"));
            user1.setRole("ROLE_USER");

            // 3. USUÁRIO 2
            Usuario user2 = new Usuario();
            user2.setLogin("zeh_gustavo");
            user2.setSenha(passwordEncoder.encode("rei_do_midrange"));
            user2.setRole("ROLE_USER");

            // 4. USUÁRIO 3
            Usuario user3 = new Usuario();
            user3.setLogin("haly_guerra");
            user3.setSenha(passwordEncoder.encode("profissional_do_combo"));
            user3.setRole("ROLE_USER");

            // 5. USUÁRIO 4
            Usuario user4 = new Usuario();
            user4.setLogin("leo_barros");
            user4.setSenha(passwordEncoder.encode("destruidor_das_safadinhas"));
            user4.setRole("ROLE_USER");

            // Salva todo mundo de uma vez no banco
            usuarioRepository.saveAll(List.of(admin, user1, user2, user3, user4));
            System.out.println("🧙‍♂️ Magic: Usuários iniciais criados com sucesso no banco de dados!");
        }
    }
}
