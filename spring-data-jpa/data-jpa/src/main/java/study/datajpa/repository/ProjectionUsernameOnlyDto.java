package study.datajpa.repository;

// 인터페이스가 아닌 클래스 프로젝션
public class ProjectionUsernameOnlyDto {

    private final String username;

    public ProjectionUsernameOnlyDto(String username) {     // 파라미터명 == 프로퍼티
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
