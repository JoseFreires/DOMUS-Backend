package com.domus.tcc.backend.controller;

import com.domus.tcc.backend.dto.request.DadosAtualizarConvidadoDTO;
import com.domus.tcc.backend.dto.request.DadosRegistrarConvidadoDTO;
import com.domus.tcc.backend.dto.response.DadosConvidadoDTO;
import com.domus.tcc.backend.security.Usuario;
import com.domus.tcc.backend.services.ConvidadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/convidados")
public class ConvidadoController {

    private final ConvidadoService convidadoService;

    public ConvidadoController(ConvidadoService convidadoService) {
        this.convidadoService = convidadoService;
    }


    @PostMapping
    public ResponseEntity<DadosConvidadoDTO> cadastrar(
        @Valid @RequestBody DadosRegistrarConvidadoDTO dados,
        @AuthenticationPrincipal Usuario logado,
        UriComponentsBuilder uriBuilder
    ) {
        DadosConvidadoDTO dto = convidadoService.cadastrar(dados, logado);
        var uri = uriBuilder.path("/convidados/{id}").buildAndExpand(dto.idConvidado()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<DadosConvidadoDTO>> listar(@AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.ok(convidadoService.listar(logado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosConvidadoDTO> atualizar(
        @PathVariable Long id,
        @RequestBody DadosAtualizarConvidadoDTO dados,
        @AuthenticationPrincipal Usuario logado
    ) {
        return ResponseEntity.ok(convidadoService.atualizar(id, dados, logado));
    }

    @PatchMapping("/{id}/inativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id, @AuthenticationPrincipal Usuario logado) {
        convidadoService.inativar(id, logado);
    }
}
