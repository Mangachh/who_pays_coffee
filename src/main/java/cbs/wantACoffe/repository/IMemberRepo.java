package cbs.wantACoffe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.entity.Member;

/**
 * Repositorio para {@link Member}
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
@Repository
public interface IMemberRepo extends JpaRepository<Member, Long> {

}
