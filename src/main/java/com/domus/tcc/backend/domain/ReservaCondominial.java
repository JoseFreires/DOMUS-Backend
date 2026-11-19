package com.domus.tcc.backend.domain;

import com.domus.tcc.backend.domain.enums.StatusReserva;
import com.domus.tcc.backend.domain.enums.MotivoCancelamento;
import com.domus.tcc.backend.dto.request.DadosRegistrarReservaDTO;
import com.domus.tcc.backend.security.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reserva_morador_espaco")
public class ReservaCondominial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "data_reserva", nullable = false)
    private LocalDate dataReserva;

    @Column(name = "quantidade_convidados", nullable = false)
    private Integer quantidadeConvidados;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_reserva", nullable = false, length = 20)
    private StatusReserva statusReserva;

    @Column(name = "justificativa_reserva_negada", length = 500)
    private String justificativaReservaNegada;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private Instant criadaEm;

    @Column(name = "cancelada_em")
    private Instant canceladaEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cancelamento", length = 40)
    private MotivoCancelamento motivoCancelamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espaco_id", nullable = false)
    private EspacoCondominial espaco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador;

    public ReservaCondominial(LocalDate dataReserva, DadosRegistrarReservaDTO dados,
                              EspacoCondominial espaco, Morador morador, Instant criadaEm) {
        this.dataReserva = dataReserva;
        this.quantidadeConvidados = dados.quantidadeConvidados();
        this.espaco = espaco;
        this.morador = morador;
        this.statusReserva = StatusReserva.RESERVADA;
        this.criadaEm = criadaEm;
    }
}
