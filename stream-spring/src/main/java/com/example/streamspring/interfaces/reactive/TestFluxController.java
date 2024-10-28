package com.example.streamspring.interfaces.reactive;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/stream/reactive")
public class TestFluxController {

    @GetMapping("/answer")
    Flux<String> answering(){
        ClassPathResource classPathResource = new ClassPathResource("static/answers/stream.txt");
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(classPathResource.getInputStream(), StandardCharsets.UTF_8));

            Stream<String> lines = reader.lines();
            return Flux.fromStream(lines)
                    .delayElements(Duration.ofMillis(50)) // 50ms 딜레이 추가
                    .map(line -> "data: " + line + "\n\n"); // SSE 형식으로 각 줄을 준비

        } catch (IOException e) {
            return Flux.just("data: Error occurred: " + e.getMessage() + "\n\n");
        }
    }
}
