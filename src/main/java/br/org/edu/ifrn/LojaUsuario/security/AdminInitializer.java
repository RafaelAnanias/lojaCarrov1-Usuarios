package br.org.edu.ifrn.LojaUsuario.security;

import br.org.edu.ifrn.LojaUsuario.model.Cargo;
import br.org.edu.ifrn.LojaUsuario.model.Usuario;
import br.org.edu.ifrn.LojaUsuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@lojacarro.com";
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario(
                    "Administrador",
                    adminEmail,
                    passwordEncoder.encode("admin123"),
                    Cargo.ADMIN
            );
            usuarioRepository.save(admin);
        }
    }
}
