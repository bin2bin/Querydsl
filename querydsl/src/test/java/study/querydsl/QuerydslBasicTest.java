package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach // 각 테스트 실행전에 데이터를 셋팅하고 시작
    public void before() {
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
    public void startJPQL(){
        //member1을 찾아라.
        String qlString =
                "select m from Member m " +
                "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        //JPAQueryFactory queryFactory = new JPAQueryFactory(em); // JPAQueryFactory 를 만들때 em을 넘겨줌 / 필드로 뺼 수도 있음
        //QMember m = new QMember("m");
        //QMember m = QMember.member;
        Member findMember = queryFactory // JPQL과 같이 문자열로 쿼리 작성을 하지 않아서 컴파일 시점에 오류를 잡아줌.
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1"); // Assertions. : static import 해주고 생략!
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member) // select + From을 합칠 수 있음
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member) // select + From을 합칠 수 있음
                .where(
                        member.username.eq("member1"), // "," 가 and 와 동일함
                        member.age.eq(10)
                      )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }
}
