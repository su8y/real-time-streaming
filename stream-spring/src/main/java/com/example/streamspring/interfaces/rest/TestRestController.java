package com.example.streamspring.interfaces.rest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RequestMapping("/stream/rest")
@RestController
public class TestRestController {

    @GetMapping(value = "/output",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public StreamingResponseBody answering(){
        return outputStream -> {
            ClassPathResource classPathResource = new ClassPathResource("static/answers/stream.txt");
            if(!classPathResource.isReadable() || !classPathResource.isFile()) {
                outputStream.write(("data: Error No answer\n\n").getBytes(StandardCharsets.UTF_8));
            }
            outputStream.write(("data: Stream started\n\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            File file = classPathResource.getFile();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));

            String str = null;
            while((str = reader.readLine()) != null){
                outputStream.write(("data: "+str).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            outputStream.close();
        };
    }
}
