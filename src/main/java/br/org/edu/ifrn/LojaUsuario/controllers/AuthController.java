package br.org.edu.ifrn.LojaUsuario.controllers;

import br.org.edu.ifrn.LojaUsuario.exception.UsuarioException;
import br.org.edu.ifrn.LojaUsuario.model.Cargo;
import br.org.edu.ifrn.LojaUsuario.model.Usuario;
import br.org.edu.ifrn.LojaUsuario.repository.UsuarioRepository;
import br.org.edu.ifrn.LojaUsuario.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrarCliente(@RequestBody Usuario dados) {
        if (dados.getNome() == null || dados.getNome().isBlank()) {
            throw new UsuarioException("O nome é obrigatório.");
        }
        if (dados.getEmail() == null || dados.getEmail().isBlank()) {
            throw new UsuarioException("O e-mail é obrigatório.");
        }
        if (dados.getSenha() == null || dados.getSenha().length() < 6) {
            throw new UsuarioException("A senha deve ter no mínimo 6 caracteres.");
        }
        if (usuarioRepository.existsByEmail(dados.getEmail())) {
            throw new UsuarioException("Já existe uma conta cadastrada com este e-mail.");
        }


        Usuario novo = new Usuario();
        novo.setNome(dados.getNome().trim());
        novo.setEmail(dados.getEmail().trim().toLowerCase());
        novo.setSenha(passwordEncoder.encode(dados.getSenha()));
        novo.setCargo(Cargo.CLIENTE);

        return ResponseEntity.ok(usuarioRepository.save(novo));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> credenciais) {
        String email = credenciais.get("email");
        String senha = credenciais.get("senha");

        Usuario usuario = usuarioRepository.findByEmail(email != null ? email.trim().toLowerCase() : "")
                .orElseThrow(() -> new UsuarioException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new UsuarioException("E-mail ou senha inválidos.");
        }

        String token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "cargo", usuario.getCargo().name()
        ));
    }
}