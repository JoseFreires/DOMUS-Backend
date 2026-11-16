package com.domus.tcc.backend.repository;

import com.domus.tcc.backend.domain.ReservaCondominial;
import com.domus.tcc.backend.domain.enums.StatusReserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaCondominialRepository extends JpaRepository<ReservaCondominial, Long> {

    boolean existsByEspaco_IdEspacoCondominialAndDataReservaAndStatusReservaIn(
            Long idEspaco, LocalDate dataReserva, Collection<StatusReserva> statusReserva);

    @Query("""
            SELECT COUNT(r) FROM ReservaCondominial r
            WHERE r.morador.id = :idMorador
              AND r.dataReserva >= :inicio
              AND r.dataReserva < :fim
              AND r.statusReserva IN :estados
            """)
    long contarNoPeriodo(
            @Param("idMorador") Long idMorador,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("estados") Collection<StatusReserva> estados);

    @Query("""
            SELECT r FROM ReservaCondominial r
            JOIN FETCH r.espaco
            JOIN FETCH r.morador
            WHERE r.morador.id = :idMorador
            ORDER BY r.dataReserva DESC, r.idReserva DESC
            """)
    List<ReservaCondominial> findAllByMoradorId(@Param("idMorador") Long idMorador);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservaCondominial r WHERE r.idReserva = :id")
    Optional<ReservaCondominial> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r FROM ReservaCondominial r
            WHERE r.statusReserva = :status
              AND r.criadaEm <= :limite
            """)
    List<ReservaCondominial> findPendentesVencidasForUpdate(
            @Param("status") StatusReserva status,
            @Param("limite") Instant limite);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r FROM ReservaCondominial r
            WHERE r.statusReserva = :status
              AND r.dataReserva < :data
            """)
    List<ReservaCondominial> findConfirmadasVencidasForUpdate(
            @Param("status") StatusReserva status,
            @Param("data") LocalDate data);
}
