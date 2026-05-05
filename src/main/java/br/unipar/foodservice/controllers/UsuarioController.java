package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.UsuarioCreateRequest;
import br.unipar.foodservice.dtos.UsuarioResponse;
import br.unipar.foodservice.entities.Usuario;
import br.unipar.foodservice.enums.Perfil;
import br.unipar.foodservice.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gestão de usuários do sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cadastra um novo usuário (apenas ADMINISTRADOR).")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioCreateRequest request) {
        Usuario novo = usuarioService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novo.getId())
                .toUri();
        return ResponseEntity.created(location).body(UsuarioResponse.from(novo));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Lista usuários, opcionalmente filtrando por perfil.")
    public ResponseEntity<List<UsuarioResponse>> listar(@RequestParam(required = false) Perfil perfil) {
        List<Usuario> usuarios = perfil == null
                ? usuarioService.listar()
                : usuarioService.listarPorPerfil(perfil);
        return ResponseEntity.ok(usuarios.stream().map(UsuarioResponse::from).toList());
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna os dados do usuário autenticado.")
    public ResponseEntity<UsuarioResponse> me(Principal principal) {
        Usuario usuario = usuarioService.buscarPorLogin(principal.getName());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }
}
