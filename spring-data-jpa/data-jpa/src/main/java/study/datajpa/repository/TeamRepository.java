package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

// @Repository -> 어노테이션을 생략해도 스프링이 인식함
public interface TeamRepository extends JpaRepository<Team, Long> {
}
