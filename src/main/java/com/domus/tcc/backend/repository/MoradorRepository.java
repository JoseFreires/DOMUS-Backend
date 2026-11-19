
package com.domus.tcc.backend.repository;

import com.domus.tcc.backend.security.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.domus.tcc.backend.domain.Morador;

import java.util.Optional;

@Repository
public interface MoradorRepository extends JpaRepository<Morador, Long>{
    Optional<Morador> findByPessoa_Id(Long idPessoa);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Morador m WHERE m.pessoa.id = :idPessoa")
    Optional<Morador> findByPessoaIdForUpdate(@Param("idPessoa") Long idPessoa);

    @Query("SELECT u FROM Usuario u WHERE u.pessoa.morador.id = :idMorador")
    Optional<Usuario> findUsuarioByMoradorId(@Param("idMorador") Long idMorador);
}
