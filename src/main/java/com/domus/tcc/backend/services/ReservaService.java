package com.domus.tcc.backend.services;

import com.domus.tcc.backend.domain.EspacoCondominial;
import com.domus.tcc.backend.domain.Morador;
import com.domus.tcc.backend.domain.ReservaCondominial;
import com.domus.tcc.backend.domain.enums.MotivoCancelamento;
import com.domus.tcc.backend.domain.enums.StatusReserva;
import com.domus.tcc.backend.dto.request.DadosAtualizacaoReservaDTO;
import com.domus.tcc.backend.dto.request.DadosNegacaoReservaDTO;
import com.domus.tcc.backend.dto.request.DadosRegistrarReservaDTO;
import com.domus.tcc.backend.dto.response.DadosConsultaEspacoDTO;
import com.domus.tcc.backend.dto.response.DadosConsultaReservaDTO;
import com.domus.tcc.backend.repository.EspacoCondominialRepository;
import com.domus.tcc.backend.repository.MoradorRepository;
import com.domus.tcc.backend.repository.ReservaCondominialRepository;
import com.domus.tcc.backend.repository.UsuarioRepository;
import com.domus.tcc.backend.security.Usuario;
import com.domus.tcc.backend.util.ReservaRegistradaEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

@Service
public class ReservaService {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final Set<StatusReserva> ESTADOS_QUE_CONTAM =
            Set.of(StatusReserva.RESERVADA, StatusReserva.CONFIRMADA, StatusReserva.CONCLUIDA);
    private static final Set<StatusReserva> ESTADOS_BLOQUEANTES =
            Set.of(StatusReserva.RESERVADA, StatusReserva.CONFIRMADA);

    @Autowired
    private  ReservaCondominialRepository reservaRepository;

    @Autowired
    private  MoradorRepository moradorRepository;

    @Autowired
    private  EspacoCondominialRepository espacoRepository;

    @Autowired
    private  UsuarioRepository usuarioRepository;

    @Autowired
    private  Clock clock;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    // Listar Espaços disponiveis
    @Transactional(readOnly = true)
    public List<DadosConsultaEspacoDTO> listarDisponiveis(LocalDate data) {
        validarAntecedencia(data, clock.instant());
        return espacoRepository.findDisponiveisNaData(data, ESTADOS_BLOQUEANTES)
                .stream()
                .map(DadosConsultaEspacoDTO::new)
                .toList();
    }

    //POST enorme
    @Transactional
    public DadosConsultaReservaDTO criar(DadosRegistrarReservaDTO dados, Usuario autenticado, LocalDate dataReserva, Long idEspaco) {
        exigirPapel(autenticado, "ROLE_MORADOR");
        if (autenticado.getPessoa() == null || autenticado.getPessoa().getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário sem pessoa associada");
        }
        Morador morador = moradorRepository.findByPessoaIdForUpdate(autenticado.getPessoa().getId())
                .orElseThrow(() -> naoEncontrado("Morador não encontrado"));

        Instant agora = clock.instant();
        validarAntecedencia(dataReserva, agora);

        EspacoCondominial espaco = espacoRepository.findByIdForUpdate(idEspaco)
                .orElseThrow(() -> naoEncontrado("Espaço não encontrado"));
        if (!Boolean.TRUE.equals(espaco.getAtivo())) {
            throw conflito("Não é possível reservar um espaço inativo");
        }
        validarCapacidade(dados.quantidadeConvidados(), espaco);

        LocalDate inicioSemana = dataReserva
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long reservasNaSemana = reservaRepository.contarNoPeriodo(
                morador.getId(), inicioSemana, inicioSemana.plusWeeks(1), ESTADOS_QUE_CONTAM);
        if (reservasNaSemana >= 1) {
            throw conflito("O limite de uma reserva por semana ISO foi atingido");
        }

        LocalDate inicioMes = dataReserva.withDayOfMonth(1);
        long reservasNoMes = reservaRepository.contarNoPeriodo(
                morador.getId(), inicioMes, inicioMes.plusMonths(1), ESTADOS_QUE_CONTAM);
        if (reservasNoMes >= 2) {
            throw conflito("O limite de duas reservas no mês foi atingido");
        }

        if (reservaRepository.existsByEspaco_IdEspacoCondominialAndDataReservaAndStatusReservaIn(
                espaco.getIdEspacoCondominial(), dataReserva, ESTADOS_BLOQUEANTES)) {
            throw conflito("O espaço já está reservado para essa data");
        }

        ReservaCondominial reserva = new ReservaCondominial(
                dataReserva, dados, espaco, morador, agora);

        ReservaCondominial salva = reservaRepository.save(reserva);

        publicarEmailReserva(salva, salva.getMorador().getPessoa().getEmail(),
                "email-ReservasCondominiais-Reservada", "Sua Reserva Domus!");
        publicarEmailReserva(salva, salva.getEspaco().getSindico().getPessoa().getEmail(),
                "email-ReservasCondominiais-Reservada-Sindico", "Nova solicitação de reserva");

        return new DadosConsultaReservaDTO(salva);
    }


    // Lista somente do Usuaio Logado
    @Transactional(readOnly = true)
    public List<DadosConsultaReservaDTO> listarMinhas(Usuario autenticado) {
        exigirPapel(autenticado, "ROLE_MORADOR");
        Morador morador = buscarMorador(autenticado);
        return reservaRepository.findAllByMoradorId(morador.getId())
                .stream()
                .map(DadosConsultaReservaDTO::new)
                .toList();
    }

    // Lista reserva pelo ID
    @Transactional(readOnly = true)
    public DadosConsultaReservaDTO buscarPorId(Long id, UserDetails autenticado) {
        ReservaCondominial reserva = reservaRepository.findById(id)
                .orElseThrow(() -> naoEncontrado("Reserva não encontrada"));
        if (temPapel(autenticado, "ROLE_MORADOR")) {
            if (!(autenticado instanceof Usuario usuarioMorador)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário autenticado não é um morador");
            }
            validarProprietario(reserva, usuarioMorador);
        } else {
            exigirPapelDeGestao(autenticado);
        }
        return new DadosConsultaReservaDTO(reserva);
    }


    //PUT só consegue mudar quantidade ksks
    @Transactional
    public DadosConsultaReservaDTO atualizarQuantidade(
            Long id, DadosAtualizacaoReservaDTO dados, Usuario autenticado) {
        exigirPapel(autenticado, "ROLE_MORADOR");

        ReservaCondominial reserva = reservaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> naoEncontrado("Reserva não encontrada"));

        validarProprietario(reserva, autenticado);
        if (reserva.getStatusReserva() != StatusReserva.RESERVADA) {
            throw conflito("Somente reservas pendentes podem ser editadas");
        }
        validarCapacidade(dados.quantidadeConvidados(), reserva.getEspaco());
        reserva.setQuantidadeConvidados(dados.quantidadeConvidados());
        return new DadosConsultaReservaDTO(reservaRepository.save(reserva));
    }

    // Cancelar Reserva (até 2 dias antes)
    @Transactional
    public void cancelar(Long id, Usuario autenticado) {
        exigirPapel(autenticado, "ROLE_MORADOR");

        ReservaCondominial reserva = reservaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> naoEncontrado("Reserva não encontrada"));

        validarProprietario(reserva, autenticado);

        Instant limite = inicioDaData(reserva.getDataReserva()).minus(48, ChronoUnit.HOURS);
        if (clock.instant().isAfter(limite)) {
            throw conflito("O prazo para cancelar esta reserva expirou");
        }

        reserva.setStatusReserva(StatusReserva.CANCELADA);
        reserva.setCanceladaEm(clock.instant());
        reserva.setMotivoCancelamento(MotivoCancelamento.CANCELAMENTO_MORADOR);
        reservaRepository.save(reserva);

        publicarEmailReserva(reserva, reserva.getEspaco().getSindico().getPessoa().getEmail(),
                "email-ReservasCondominiais-Cancelada", "Reserva cancelada");
    }

    // Sindico conforma Reserva (em até 24 horas para validar, se não é cancelada automaticamente)
    @Transactional
    public boolean confirmar(Long id, UserDetails autenticado) {
        exigirPapelDeGestao(autenticado);

        ReservaCondominial reserva = reservaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> naoEncontrado("Reserva não encontrada"));

        validarPendente(reserva);
        if (clock.instant().compareTo(reserva.getCriadaEm().plus(24, ChronoUnit.HOURS)) >= 0) {
            expirar(reserva);
            return false;
        }
        reserva.setStatusReserva(StatusReserva.CONFIRMADA);
        reservaRepository.save(reserva);
        publicarEmailReserva(reserva, reserva.getMorador().getPessoa().getEmail(),
                "email-ReservasCondominiais-Confirmada", "Sua reserva foi confirmada");
        return true;
    }


    // Nega Reserva (aqui também tem 24 horas para negar)
    @Transactional
    public boolean negar(Long id, DadosNegacaoReservaDTO dados, UserDetails autenticado) {
        exigirPapelDeGestao(autenticado);
        if (dados == null || !StringUtils.hasText(dados.justificativa())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A justificativa é obrigatória");
        }
        String justificativa = dados.justificativa().trim();
        if (justificativa.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A justificativa excede 500 caracteres");
        }

        ReservaCondominial reserva = reservaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> naoEncontrado("Reserva não encontrada"));

        validarPendente(reserva);
        if (clock.instant().compareTo(reserva.getCriadaEm().plus(24, ChronoUnit.HOURS)) >= 0) {
            expirar(reserva);
            return false;
        }
        reserva.setStatusReserva(StatusReserva.NEGADA);
        reserva.setJustificativaReservaNegada(justificativa);
        reservaRepository.save(reserva);
        publicarEmailReserva(reserva, reserva.getMorador().getPessoa().getEmail(),
                "email-ReservasCondominiais-Negada", "Sua reserva foi negada");
        return true;
    }

    // Função para expirar solicitação automaticamente (chamada no ReservaExpiracaoDate)
    @Transactional
    public int expirarSolicitacoesSemDecisao() {
        Instant limite = clock.instant().minus(24, ChronoUnit.HOURS);
        List<ReservaCondominial> vencidas = reservaRepository.findPendentesVencidasForUpdate(
                StatusReserva.RESERVADA, limite);
        vencidas.forEach(this::expirar);
        return vencidas.size();
    }

    // Função para concluir reserva automaticamente (chamada no ReservaExpiracaoDate)
    @Transactional
    public int concluirReservasVencidas() {
        LocalDate hoje = LocalDate.now(clock);
        List<ReservaCondominial> vencidas = reservaRepository.findConfirmadasVencidasForUpdate(
                StatusReserva.CONFIRMADA, hoje);
        vencidas.forEach(reserva -> reserva.setStatusReserva(StatusReserva.CONCLUIDA));
        reservaRepository.saveAll(vencidas);
        return vencidas.size();
    }

    // Função para polpar repetição de código
    private Usuario buscarUsuario(Usuario autenticado) {
        if (autenticado == null || autenticado.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        return usuarioRepository.findById(autenticado.getId())
                .orElseThrow(() -> naoEncontrado("Usuário autenticado não encontrado"));
    }

    // Função para polpar repetição de código
    private Morador buscarMorador(Usuario autenticado) {
        Usuario usuario = buscarUsuario(autenticado);
        return moradorRepository.findByPessoa_Id(usuario.getPessoa().getId())
                .orElseThrow(() -> naoEncontrado("Morador associado ao usuário não encontrado"));
    }

    // Função para polpar repetição de código
    private void publicarEmailReserva(
            ReservaCondominial reserva, String destinatario, String template, String assunto) {
        applicationEventPublisher.publishEvent(new ReservaRegistradaEvent(
                destinatario,
                template,
                assunto,
                reserva.getMorador().getPessoa().getNomeCompleto(),
                reserva.getEspaco().getSindico().getPessoa().getNomeCompleto(),
                reserva.getEspaco().getNomeEspaco(),
                reserva.getStatusReserva().name(),
                reserva.getDataReserva().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                reserva.getQuantidadeConvidados(),
                reserva.getJustificativaReservaNegada(),
                reserva.getEspaco().getValorReserva()
        ));
    }



    private void validarProprietario(ReservaCondominial reserva, Usuario autenticado) {
        Morador morador = buscarMorador(autenticado);
        if (!reserva.getMorador().getId().equals(morador.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta reserva pertence a outro morador");
        }
    }

    private void validarPendente(ReservaCondominial reserva) {
        if (reserva.getStatusReserva() != StatusReserva.RESERVADA) {
            throw conflito("Somente reservas pendentes podem ser decididas");
        }
    }

    private void validarCapacidade(Integer quantidade, EspacoCondominial espaco) {
        if (quantidade == null || quantidade < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade de convidados inválida");
        }
        int capacidade;
        try {
            capacidade = Integer.parseInt(espaco.getCapacidadeMaxima());
        } catch (NumberFormatException | NullPointerException e) {
            throw conflito("A capacidade máxima do espaço está inválida");
        }
        if (quantidade > capacidade) {
            throw conflito("A quantidade de convidados excede a capacidade máxima do espaço");
        }
    }

    private void validarAntecedencia(LocalDate data, Instant agora) {
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data da reserva é obrigatória");
        }

        if (inicioDaData(data).isBefore(agora)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Reserva não pode ser realizada antes da data atual");
        }

        if (inicioDaData(data).isBefore(agora.plus(48, ChronoUnit.HOURS))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A reserva deve ser feita com pelo menos 48 horas de antecedência");
       }

    }

    private Instant inicioDaData(LocalDate data) {
        return data.atStartOfDay(ZONE).toInstant();
    }

    private void expirar(ReservaCondominial reserva) {
        reserva.setStatusReserva(StatusReserva.CANCELADA);
        reserva.setCanceladaEm(clock.instant());
        reserva.setMotivoCancelamento(MotivoCancelamento.APROVACAO_EXPIRADA);
        reservaRepository.save(reserva);
    }

    private void exigirPapelDeGestao(UserDetails usuario) {
        exigirPapel(usuario, "ROLE_SINDICO", "ROLE_ADMIN");
    }

    private void exigirPapel(UserDetails usuario, String... papeis) {
        if (!temPapel(usuario, papeis)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operação não permitida para este perfil");
        }
    }

    private boolean temPapel(UserDetails usuario, String... papeis) {
        if (usuario == null) {
            return false;
        }
        return usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> List.of(papeis).contains(authority));
    }

    private ResponseStatusException conflito(String mensagem) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensagem);
    }

    private ResponseStatusException naoEncontrado(String mensagem) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
    }
}
