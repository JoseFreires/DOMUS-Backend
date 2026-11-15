package com.domus.tcc.backend.repository;

import com.domus.tcc.backend.domain.Convidado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConvidadoRepository extends JpaRepository<Convidado, Long> {
    boolean existsByCpfAndMoradorId(String cpf, Long moradorId);
    List<Convidado> findByMoradorId(Long moradorId);
    List<Convidado> findByMoradorIdAndAtivoTrue(Long moradorId);
}
