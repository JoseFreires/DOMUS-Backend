package com.domus.tcc.backend.domain;

import com.domus.tcc.backend.domain.enums.TipoVisita;
import com.domus.tcc.backend.security.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "log_visita")
public class LogVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVisitante")
    private Long idLogVisita;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_visita", nullable = false)
    private TipoVisita tipoVisita;

    @Column(name = "data_hora_chegada", nullable = false)
    private LocalDateTime dataHoraChegada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMoradia", nullable = false)
    private Moradia moradia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_porteiro", nullable = false)
    private Usuario porteiro;

    public LogVisita(String nome, TipoVisita tipoVisita, LocalDateTime dataHoraChegada, Moradia moradia, Usuario porteiro) {
        this.nome = nome;
        this.tipoVisita = tipoVisita;
        this.dataHoraChegada = dataHoraChegada;
        this.moradia = moradia;
        this.porteiro = porteiro;
    }
}
