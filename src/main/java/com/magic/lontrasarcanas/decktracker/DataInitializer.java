package com.magic.lontrasarcanas.decktracker;

import com.magic.lontrasarcanas.decktracker.model.Usuario;
import com.magic.lontrasarcanas.decktracker.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        // Criamos a lista de usuários que desejamos ter no sistema
        List<Usuario> listaDesejada = new ArrayList<>();

        // 1. ADMIN
        Usuario admin = new Usuario();
        admin.setLogin("tales_terezu"); 
        admin.setSenha(passwordEncoder.encode("Terezu240"));
        admin.setRole("ROLE_ADMIN");
        listaDesejada.add(admin);

        // 2. USUÁRIO 1
        Usuario user1 = new Usuario();
        user1.setLogin("luiz_lima");
        user1.setSenha(passwordEncoder.encode("jeskai_control_quase_bom"));
        user1.setRole("ROLE_USER");
        listaDesejada.add(user1);

        // 3. USUÁRIO 2
        Usuario user2 = new Usuario();
        user2.setLogin("zeh_gustavo");
        user2.setSenha(passwordEncoder.encode("rei_do_midrange"));
        user2.setRole("ROLE_USER");
        listaDesejada.add(user2);

        // 4. USUÁRIO 3
        Usuario user3 = new Usuario();
        user3.setLogin("haly_guerra");
        user3.setSenha(passwordEncoder.encode("profissional_do_combo"));
        user3.setRole("ROLE_USER");
        listaDesejada.add(user3);

        // 5. USUÁRIO 4 (O novo integrante!)
        Usuario user4 = new Usuario();
        user4.setLogin("leonardo_barros");
        user4.setSenha(passwordEncoder.encode("destruidor_guiado_pelas_safadinhas"));
        user4.setRole("ROLE_USER");
        listaDesejada.add(user4);

        // 6. USUÁRIO 5 (O novo integrante!)
        Usuario user5 = new Usuario();
        user5.setLogin("matheus_weber");
        user5.setSenha(passwordEncoder.encode("amigo_dos_elfos"));
        user5.setRole("ROLE_USER");
        listaDesejada.add(user5);

        // Lógica: Para cada usuário na lista, só salva se ele NÃO existir no banco
        listaDesejada.forEach(u -> {
            if (!usuarioRepository.existsByLogin(u.getLogin())) {
                usuarioRepository.save(u);
                System.out.println("✅ Usuário criado: " + u.getLogin());
            }
        });
        
        System.out.println("🧙‍♂️ Magic: Verificação de usuários concluída!");
    }
}
