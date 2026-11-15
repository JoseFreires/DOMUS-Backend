package com.domus.tcc.backend.domain;

import com.domus.tcc.backend.domain.enums.Recorrencia;
import com.domus.tcc.backend.dto.request.DadosRegistrarConvidadoDTO;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "convidado",
    uniqueConstraints = @UniqueConstraint(columnNames = {"cpf", "morador_idMorador"})
)
public class Convidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConvidado")
    private Long idConvidado;

    @Column(name = "nome_convidado", nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Recorrencia recorrencia;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_idMorador", nullable = false)
    private Morador morador;

    public Convidado(DadosRegistrarConvidadoDTO dados, Morador morador) {
        this.nome = dados.nome();
        this.cpf = dados.cpf();
        this.telefone = dados.telefone();
        this.recorrencia = dados.recorrencia();
        this.ativo = true;
        this.morador = morador;
    }
}
