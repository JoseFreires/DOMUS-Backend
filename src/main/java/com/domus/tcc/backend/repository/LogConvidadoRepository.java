package com.domus.tcc.backend.repository;

import com.domus.tcc.backend.domain.LogConvidado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogConvidadoRepository extends JpaRepository<LogConvidado, Long> {
}
