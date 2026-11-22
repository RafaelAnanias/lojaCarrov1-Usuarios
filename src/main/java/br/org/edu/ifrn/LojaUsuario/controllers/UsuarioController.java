package br.org.edu.ifrn.LojaUsuario.controllers;

import br.org.edu.ifrn.LojaUsuario.model.Usuario;
import br.org.edu.ifrn.LojaUsuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/salvar")
    public ResponseEntity<Usuario> salvar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.salvar(usuario));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication auth) {
        if (!temPermissao(id, auth)) {
            return ResponseEntity.status(403).body(Map.of("erro", "Acesso negado: você só pode consultar seus próprios dados."));
        }
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Usuario usuario, Authentication auth) {
        if (!temPermissao(id, auth)) {
            return ResponseEntity.status(403).body(Map.of("erro", "Acesso negado: você só pode atualizar seus próprios dados."));
        }
        return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private boolean temPermissao(Long idAlvo, Authentication auth) {
        Long idLogado = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin || idLogado.equals(idAlvo);
    }
}