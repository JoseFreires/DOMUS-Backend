package com.domus.tcc.backend.repository;

import com.domus.tcc.backend.domain.LogVisita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogVisitaRepository extends JpaRepository<LogVisita, Long> {
}
