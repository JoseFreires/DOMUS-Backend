package com.domus.tcc.backend.services;

import com.domus.tcc.backend.domain.Bloco;
import com.domus.tcc.backend.domain.Convidado;
import com.domus.tcc.backend.domain.LogConvidado;
import com.domus.tcc.backend.domain.Moradia;
import com.domus.tcc.backend.domain.Morador;
import com.domus.tcc.backend.domain.Pessoa;
import com.domus.tcc.backend.repository.ConvidadoRepository;
import com.domus.tcc.backend.repository.LogConvidadoRepository;
import com.domus.tcc.backend.repository.UsuarioRepository;
import com.domus.tcc.backend.security.Usuario;
import com.domus.tcc.backend.util.ConvidadoChegouEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogConvidadoServiceTest {

    @Mock
    private ConvidadoRepository convidadoRepository;

    @Mock
    private LogConvidadoRepository logConvidadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LogConvidadoService logConvidadoService;

    @Test
    void aoRegistrarChegadaPublicaEventoComDadosDoMoradorEConvidado() {
        Pessoa pessoaMorador = new Pessoa();
        pessoaMorador.setNomeCompleto("Maria Silva");
        pessoaMorador.setEmail("maria@example.com");

        Bloco bloco = new Bloco();
        bloco.setNome_torre("Torre A");
        Moradia moradia = new Moradia();
        moradia.setNumero("101");
        moradia.setBloco(bloco);

        Morador morador = new Morador();
        morador.setId(7L);
        morador.setPessoa(pessoaMorador);
        morador.setMoradias(java.util.List.of(moradia));

        Convidado convidado = new Convidado();
        convidado.setIdConvidado(12L);
        convidado.setNome("João Souza");
        convidado.setAtivo(true);
        convidado.setMorador(morador);

        Pessoa pessoaPorteiro = new Pessoa();
        pessoaPorteiro.setNomeCompleto("Porteiro");
        Usuario porteiro = new Usuario();
        porteiro.setPessoa(pessoaPorteiro);
        Usuario logado = new Usuario();
        logado.setId(5L);

        when(convidadoRepository.findById(12L)).thenReturn(Optional.of(convidado));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(porteiro));
        when(logConvidadoRepository.save(any(LogConvidado.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        logConvidadoService.registrar(7L, 12L, logado);

        ArgumentCaptor<ConvidadoChegouEvent> eventoCaptor =
            ArgumentCaptor.forClass(ConvidadoChegouEvent.class);
        verify(eventPublisher).publishEvent(eventoCaptor.capture());
        ConvidadoChegouEvent evento = eventoCaptor.getValue();
        assertEquals("Maria Silva", evento.nomeMorador());
        assertEquals("maria@example.com", evento.emailMorador());
        assertEquals("João Souza", evento.nomeConvidado());
        assertEquals("101 - Torre A", evento.moradia());
        assertTrue(evento.dataHoraChegada().matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}"));
    }

    @Test
    void naoRegistraConvidadoDeOutroMorador() {
        Morador morador = new Morador();
        morador.setId(8L);
        Convidado convidado = new Convidado();
        convidado.setMorador(morador);
        when(convidadoRepository.findById(12L)).thenReturn(Optional.of(convidado));

        assertThrows(
            jakarta.persistence.EntityNotFoundException.class,
            () -> logConvidadoService.registrar(7L, 12L, new Usuario())
        );

        verify(logConvidadoRepository, never()).save(any(LogConvidado.class));
    }

}
