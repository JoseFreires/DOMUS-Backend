package com.domus.tcc.backend.domain;

import com.domus.tcc.backend.security.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "log_convidado")
public class LogConvidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLogConvidado")
    private Long idLogConvidado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convidado_idConvidado", nullable = false)
    private Convidado convidado;

    @Column(name = "data_hora_chegada", nullable = false)
    private LocalDateTime dataHoraChegada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_porteiro", nullable = false)
    private Usuario porteiro;

    public LogConvidado(Convidado convidado, LocalDateTime dataHoraChegada, Usuario porteiro) {
        this.convidado = convidado;
        this.dataHoraChegada = dataHoraChegada;
        this.porteiro = porteiro;
    }
}
