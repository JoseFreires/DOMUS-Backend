package com.domus.tcc.backend.controller;

import com.domus.tcc.backend.dto.request.DadosRegistrarLogConvidadoDTO;
import com.domus.tcc.backend.dto.response.DadosConvidadoDTO;
import com.domus.tcc.backend.dto.response.DadosLogConvidadoDTO;
import com.domus.tcc.backend.security.Usuario;
import com.domus.tcc.backend.services.LogConvidadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/log-convidado")
public class LogConvidadoController {

    private final LogConvidadoService logConvidadoService;

    public LogConvidadoController(LogConvidadoService logConvidadoService) {
        this.logConvidadoService = logConvidadoService;
    }

    @PostMapping
    public ResponseEntity<DadosLogConvidadoDTO> registrar(
        @Valid @RequestBody DadosRegistrarLogConvidadoDTO dados,
        @AuthenticationPrincipal Usuario logado,
        UriComponentsBuilder uriBuilder
    ) {
        DadosLogConvidadoDTO dto = logConvidadoService.registrar(dados, logado);
        var uri = uriBuilder.path("/log-convidado/{id}").buildAndExpand(dto.idLogConvidado()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<DadosLogConvidadoDTO>> listar() {
        return ResponseEntity.ok(logConvidadoService.listar());
    }

    @GetMapping("/convidados")
    public ResponseEntity<List<DadosConvidadoDTO>> listarConvidadosPorMorador(@RequestParam Long moradorId) {
        return ResponseEntity.ok(logConvidadoService.listarConvidadosPorMorador(moradorId));
    }
}
