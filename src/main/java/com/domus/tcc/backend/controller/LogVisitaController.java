

package com.domus.tcc.backend.controller;

import com.domus.tcc.backend.dto.request.DadosRegistrarLogVisitaDTO;
import com.domus.tcc.backend.dto.response.DadosLogVisitaDTO;
import com.domus.tcc.backend.security.Usuario;
import com.domus.tcc.backend.services.LogVisitaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/log-visita")
public class LogVisitaController {

    private final LogVisitaService logVisitaService;

    public LogVisitaController(LogVisitaService logVisitaService) {
        this.logVisitaService = logVisitaService;
    }

    @PostMapping
    public ResponseEntity<DadosLogVisitaDTO> registrar(
        @Valid @RequestBody DadosRegistrarLogVisitaDTO dados,
        @AuthenticationPrincipal Usuario logado,
        UriComponentsBuilder uriBuilder
    ) {
        DadosLogVisitaDTO dto = logVisitaService.registrar(dados, logado);
        var uri = uriBuilder.path("/log-visita/{id}").buildAndExpand(dto.idLogVisita()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<DadosLogVisitaDTO>> listar() {
        return ResponseEntity.ok(logVisitaService.listar());
    }
}
