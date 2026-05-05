package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.CatalogoCategoriaResponse;
import br.unipar.foodservice.services.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo público", description = "Listagem aberta de produtos ativos para consulta online (RF11)")
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Retorna o catálogo público de produtos ativos agrupado por categoria.")
    public ResponseEntity<List<CatalogoCategoriaResponse>> obter() {
        return ResponseEntity.ok(catalogoService.montar());
    }
}
