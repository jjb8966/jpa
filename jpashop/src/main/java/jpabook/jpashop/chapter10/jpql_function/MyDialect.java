package jpabook.jpashop.chapter10.jpql_function;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MyDialect extends MySQL8Dialect {

    // 생성자
    public MyDialect() {

        // 사용자 정의 함수를 방언에 등록
        this.registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
}
