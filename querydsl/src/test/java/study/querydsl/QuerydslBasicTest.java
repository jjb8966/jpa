package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    @PersistenceUnit
    EntityManagerFactory emf;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    void startJPQL() {
        String sql = "select m from Member m where username = :username";

        Member findMember = em.createQuery(sql, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void startQuerydsl() {
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void 기본_인스턴스_사용() {         // 권장하는 방법
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void search() {
        Member result1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.lt(15)))
                .fetchOne();

        assertThat(result1.getUsername()).isEqualTo("member1");
        assertThat(result1.getAge()).isLessThan(15);

        List<Member> result2 = queryFactory
                .selectFrom(member)
                .where(member.username.startsWith("member"))
                .fetch();

        for (Member member : result2) {
            System.out.println("member = " + member);
        }

        List<Member> result3 = queryFactory
                .selectFrom(member)
                .where(member.age.between(20, 40))
                .fetch();

        for (Member member : result3) {
            System.out.println("member = " + member);
        }

        Member result4 = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(result4.getUsername()).isEqualTo("member1");
    }

    @Test
    void resultFetch() {
//        List<Member> fetch = queryFactory.selectFrom(member)
//                .fetch();
//
//        Member member1 = queryFactory.selectFrom(member)
//                .fetchOne();
//
//        Member member2 = queryFactory.selectFrom(member)
//                .fetchFirst();

        QueryResults<Member> memberQueryResults = queryFactory.selectFrom(member)
                .fetchResults();

        long total = memberQueryResults.getTotal();
        List<Member> results = memberQueryResults.getResults();

        System.out.println("total = " + total);
        for (Member member : results) {
            System.out.println("member = " + member);
        }

        long count = queryFactory.selectFrom(member)
                .fetchCount();

        System.out.println("count = " + count);
    }

    /**
     * 정렬 조건
     * 1. 나이 내림차순
     * 2. 이름 오름차순
     * 3. 이름이 null일 시 마지막
     */
    @Test
    void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory  // member5, member6, null
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isEqualTo(null);
    }

    @Test
    void paging1() {
        // member1 [member2 member3] member4
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.asc())
                .offset(1)  // index 0부터 시작 (첫번째 건너 뛰고 조회)
                .limit(2)   // 2개만 조회
                .fetch();

        assertThat(result.get(0).getUsername()).isEqualTo("member2");
        assertThat(result.get(1).getUsername()).isEqualTo("member3");
    }

    @Test
    void paging2() {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)  // index 0부터 시작 (첫번째 건너 뛰고 조회)
                .limit(2)   // 2개만 조회
                .fetchResults();

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    @Test
    void aggregation() {
        Tuple tuple = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetchOne();

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    void group() {
        List<Tuple> result = queryFactory
                .select(
                        team.name, member.age.avg()
                )
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        /**
         * [JPQL]
         * select
         *     team.name,
         *     avg(member1.age)
         * from
         *     Member member1
         * inner join
         *     member1.team as team
         * group by
         *     team.name
         *
         * [SQL]
         * select
         *     team1_.name as col_0_0_,
         *     avg(cast(member0_.age as double)) as col_1_0_
         * from
         *     member member0_
         * inner join
         *     team team1_
         *         on member0_.team_id=team1_.id
         * group by
         *     team1_.name
         */

        Tuple team1 = result.get(0);
        Tuple team2 = result.get(1);

        assertThat(team1.get(team.name)).isEqualTo("teamA");
        assertThat(team1.get(member.age.avg())).isEqualTo(15);

        assertThat(team2.get(team.name)).isEqualTo("teamB");
        assertThat(team2.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    void 기본_조인() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        for (Member member : result) {
            System.out.println("member = " + member);
        }

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타 조인 - 연관관계가 없는 테이블 간 조인
     * 회원 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    void 세타_조인() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        for (Member member : result) {
            System.out.println("member = " + member);
        }

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * 회원과 팀을 조인. 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * [JPQL]
     * select m, t
     * from member m left join m.team t
     * on t.name = "teamA"
     *
     * [SQL]
     * select m.*, t.*
     * from member m left join team t
     * on m.team_id = t.id and t.name = "teamA"
     */
    @Test
    void 조인_on절() {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

        /**
         * tuple = [Member(id=3, username=member1, age=10), Team(id=1, name=teamA)]
         * tuple = [Member(id=4, username=member2, age=20), Team(id=1, name=teamA)]
         * tuple = [Member(id=5, username=member3, age=30), null]
         * tuple = [Member(id=6, username=member4, age=40), null]
         *
         * -> 그냥 join을 한다면 위의 2개만 나옴
         * --> where절을 쓴 것과 같은 결과이므로 더 익숙한 where을 쓰는게 나음
         */
    }

    @Test
    void 조인_on_세타_조인() {    // on을 사용하는 주된 이유
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    void 페치_조인x() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // 지연 로딩으로 team은 조회하지 않은 상태
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.team);

        assertThat(loaded).isFalse();
    }

    @Test
    void 페치_조인o() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .leftJoin(member.team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.team);

        assertThat(loaded).isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    void 서브_쿼리_eq() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result)
                .extracting("age")
                .containsExactly(40);
    }

    /**
     * 나이가 평균 이상인 회원 조회
     */
    @Test
    void 서브_쿼리_goe() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    /**
     * teamA 소속인 회원 조회 - 저렇게 조회하진 않음..
     */
    @Test
    void 서브_쿼리_in() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.team.name.eq("teamA"))
                ))
                .fetch();

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void select절에서_서브_쿼리() {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * from절에서 서브 쿼리는 사용할 수 없음!!
     * 해결 방법
     * 1. 서브 쿼리를 join으로 변경하기 (불가능할 수 도 있음)
     * 2. 애플리케이션에서 쿼리를 2번 날려 실행
     * 3. nativeSQL 사용
     */

    @Test
    void static_import로_서브_쿼리() {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * case문을 꼭 사용해야 하는지 생각해보기
     * DB에 쿼리를 날려서 데이터를 가져올 때는 데이터를 퍼오기만 하고
     * 다음과 같은 처리는 애플리케이션 로직으로 처리하는게 더 일반적임
     */
    @Test
    void case문() {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void case문_복잡한_조건() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void 상수_사용() {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    void 문자_더하기() {
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}
