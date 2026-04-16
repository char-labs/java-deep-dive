package io.github.deepdive.common;

/**
 * 주제에 대한 데모의 공통 인터페이스.
 * Spring 컨텍스트가 모든 구현체를 모아서 CLI 인자로 선택 실행한다.
 */
public interface Demo {
    /** CLI에서 사용할 식별자. ex) "jvm.jit" */
    String name();

    /** 데모 본체 */
    void run() throws Exception;
}
