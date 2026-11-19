package com.domus.tcc.backend.services;

import com.domus.tcc.backend.domain.ContaAdm;
import com.domus.tcc.backend.repository.ContaAdmRepository;
import com.domus.tcc.backend.repository.UsuarioRepository;
import com.domus.tcc.backend.security.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContaAdmRepository contaAdmRepository;

    @Mock
    private LogSistemaService logSistemaService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    void localizaUsuarioPorUsernameIgnorandoEspacosECapitalizacao() {
        String username = "ANA";
        Usuario usuario = new Usuario();
        usuario.setUsername("ana");
        when(usuarioRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(usuario));

        UserDetails resultado = autenticacaoService.loadUserByUsername("  ANA ");

        assertSame(usuario, resultado);
        verify(usuarioRepository, never()).findByUsername(anyString());
        verify(usuarioRepository, never()).findByPessoaEmailIgnoreCase(anyString());
        verifyNoInteractions(contaAdmRepository);
    }

    @Test
    void naoLocalizaUsuarioComumPorEmail() {
        String email = "ana@example.test";
        when(usuarioRepository.findByUsernameIgnoreCase(email)).thenReturn(Optional.empty());
        when(contaAdmRepository.findByUsername(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> autenticacaoService.loadUserByUsername(email));
        verify(usuarioRepository, never()).findByPessoaEmailIgnoreCase(anyString());
    }

    @Test
    void mantemAutenticacaoDeContaAdministrativa() {
        String username = "admin";
        UserDetails contaAdm = org.mockito.Mockito.mock(ContaAdm.class);
        when(usuarioRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
        when(contaAdmRepository.findByUsername(username)).thenReturn(contaAdm);

        UserDetails resultado = autenticacaoService.loadUserByUsername(username);

        assertSame(contaAdm, resultado);
    }

    @Test
    void lancaExcecaoQuandoUsuarioNaoExiste() {
        String username = "inexistente";
        when(usuarioRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
        when(contaAdmRepository.findByUsername(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> autenticacaoService.loadUserByUsername(username));
    }
}
