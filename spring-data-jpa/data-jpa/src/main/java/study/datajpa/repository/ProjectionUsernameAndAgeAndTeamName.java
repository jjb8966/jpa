package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface ProjectionUsernameAndAgeAndTeamName {

    // 전체 엔티티를 조회한 다음에 계산하는(골라내는) 것이므로 쿼리 최적화가 되지는 않음
    @Value("#{target.username + ' ' + target.age + ' ' + target.getTeam().name}")
    String getUsernameAndAgeAndTeamName();
}
