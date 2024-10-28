package com.example.streamspring.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class AnswerService {
    public void answer(String filename, OutputStream outputStream) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(filename);
        if(!classPathResource.isReadable() || !classPathResource.isFile()) {
            outputStream.write(("data: Error No answer\n\n").getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            return;
        }
        File file = classPathResource.getFile();
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
            BufferedReader reader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
            String str;
            while((str = reader.readLine()) != null){
                try{
                    outputStream.write(("data: "+str).getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }catch (IOException e){
                    System.out.println("OUTPUT STREAM CLOSED");
                    break;
                }
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.out.println("Stream interrupted" + e.getMessage());
            Thread.currentThread().interrupt(); // 인터럽트 상태 복구
        }

    }
}
