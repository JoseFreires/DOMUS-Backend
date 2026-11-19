package com.domus.tcc.backend.controller;


import com.domus.tcc.backend.dto.response.DadosConvidadoDTO;
import com.domus.tcc.backend.dto.response.DadosLogConvidadoDTO;
import com.domus.tcc.backend.security.Usuario;
import com.domus.tcc.backend.services.LogConvidadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/log-convidado")
public class LogConvidadoController {

    private final LogConvidadoService logConvidadoService;

    public LogConvidadoController(LogConvidadoService logConvidadoService) {
        this.logConvidadoService = logConvidadoService;
    }

    @PostMapping("/morador/{moradorId}/convidados/{id}")
    public ResponseEntity<DadosLogConvidadoDTO> registrar(@PathVariable Long id,
        @AuthenticationPrincipal Usuario logado,
        UriComponentsBuilder uriBuilder
    ) {
        DadosLogConvidadoDTO dto = logConvidadoService.registrar(id, logado);
        var uri = uriBuilder.path("/log-convidado/{id}").buildAndExpand(dto.idLogConvidado()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<DadosLogConvidadoDTO>> listar() {
        return ResponseEntity.ok(logConvidadoService.listar());
    }

    @GetMapping("/morador/{moradorId}/convidados")
    public ResponseEntity<List<DadosConvidadoDTO>> listarConvidadosPorMorador(@PathVariable Long moradorId) {
        return ResponseEntity.ok(logConvidadoService.listarConvidadosPorMorador(moradorId));
    }
}
