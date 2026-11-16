package com.domus.tcc.backend.controller;

import com.domus.tcc.backend.dto.request.DadosAtualizacaoReservaDTO;
import com.domus.tcc.backend.dto.request.DadosNegacaoReservaDTO;
import com.domus.tcc.backend.dto.request.DadosRegistrarReservaDTO;
import com.domus.tcc.backend.dto.response.DadosConsultaEspacoDTO;
import com.domus.tcc.backend.dto.response.DadosConsultaReservaDTO;
import com.domus.tcc.backend.security.Usuario;
import com.domus.tcc.backend.services.EspacoCondominialService;
import com.domus.tcc.backend.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @Autowired
    private EspacoCondominialService espacoCondominialService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/disponiveis/{id}")
    public ResponseEntity<DadosConsultaReservaDTO> criar(
            @Valid @RequestBody DadosRegistrarReservaDTO dados,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data,
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario autenticado,
            UriComponentsBuilder uriBuilder) {

        DadosConsultaReservaDTO reserva = reservaService.criar(dados, autenticado,data,id);
        URI uri = uriBuilder.path("/reservas/{id}").buildAndExpand(reserva.idReserva()).toUri();
        return ResponseEntity.created(uri).body(reserva);
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<DadosConsultaReservaDTO>> listarMinhas(
            @AuthenticationPrincipal Usuario autenticado) {
        return ResponseEntity.ok(reservaService.listarMinhas(autenticado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosConsultaReservaDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails autenticado) {
        return ResponseEntity.ok(reservaService.buscarPorId(id, autenticado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DadosConsultaReservaDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DadosAtualizacaoReservaDTO dados,
            @AuthenticationPrincipal Usuario autenticado) {
        return ResponseEntity.ok(reservaService.atualizarQuantidade(id, dados, autenticado));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario autenticado) {
        reservaService.cancelar(id, autenticado);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails autenticado) {
        exigirDecisaoEfetuada(reservaService.confirmar(id, autenticado));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/negar")
    public ResponseEntity<Void> negar(
            @PathVariable Long id,
            @Valid @RequestBody DadosNegacaoReservaDTO dados,
            @AuthenticationPrincipal UserDetails autenticado) {
        exigirDecisaoEfetuada(reservaService.negar(id, dados, autenticado));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<DadosConsultaEspacoDTO>> listarDisponiveis(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data) {
        return ResponseEntity.ok(espacoCondominialService.listarDisponiveis(data));
    }

    @GetMapping("/disponiveis/{id}")
    public ResponseEntity<DadosConsultaEspacoDTO> encontrarEspacoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(espacoCondominialService.buscarEspacoId(id));
    }

    private void exigirDecisaoEfetuada(boolean efetuada) {
        if (!efetuada) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "O prazo de 24 horas para decidir expirou; a reserva foi cancelada");
        }
    }
}
