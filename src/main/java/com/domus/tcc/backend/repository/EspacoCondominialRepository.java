package com.domus.tcc.backend.repository;


import com.domus.tcc.backend.domain.EspacoCondominial;
import com.domus.tcc.backend.domain.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EspacoCondominialRepository extends JpaRepository<EspacoCondominial, Long> {

    @Query("""
            SELECT e FROM EspacoCondominial e
            WHERE e.ativo = true
              AND NOT EXISTS (
                  SELECT r.idReserva FROM ReservaCondominial r
                  WHERE r.espaco.idEspacoCondominial = e.idEspacoCondominial
                    AND r.dataReserva = :data
                    AND r.statusReserva IN :bloqueantes
              )
            """)
    List<EspacoCondominial> findDisponiveisNaData(
            @Param("data") LocalDate data,
            @Param("bloqueantes") Collection<StatusReserva> bloqueantes);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EspacoCondominial e WHERE e.idEspacoCondominial = :id")
    Optional<EspacoCondominial> findByIdForUpdate(@Param("id") Long id);
}
