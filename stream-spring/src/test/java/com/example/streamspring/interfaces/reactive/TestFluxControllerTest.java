package com.example.streamspring.interfaces.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestFluxControllerTest {
    private WebTestClient client;

    @BeforeEach
    public void setup(){
        this.client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8080/")
                .build();
    }
    @Test
    void answeringTest(){
        FluxExchangeResult<String> result= client
                .get()
                .uri("stream/reactive/answer")
                .accept(MediaType.TEXT_EVENT_STREAM)
                //.accept(MediaType.APPLICATION_STREAM_JSON) /* 실패하는 코드 한줄로 묶여서 나옴. 이유는 String에 대해서는 특별하게 처리하기 때문 */
                .exchange()
                .returnResult(String.class);

        StepVerifier.create(result.getResponseBody())
                .expectNext("Hello")
                .expectNext("world!")
                .verifyComplete();
    }

}