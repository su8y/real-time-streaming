package com.example.streamspring.interfaces.rest;

import com.example.streamspring.services.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/stream/rest")
@RestController
public class TestRestController {
    @Autowired
    private AnswerService answerService;

    @ExceptionHandler(IOException.class)
    public void ioClientDisconnectedHandler(){
        System.out.println("TestRestController.ioClientDisconnectedHandler"+ "client disconnected");
    }

    @GetMapping(value = "/output",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public StreamingResponseBody answering(){
        return outputStream -> {
            answerService.answer("static/answers/stream.txt",outputStream);
        };
    }
    @GetMapping(value ="/output2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter answering2() throws IOException, InterruptedException {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        new Thread(()->{
            try{
                String message = "data: Hello Response Body Emitter\n\n";
                for(int i = 0 ; i< 10;i++){
                    emitter.send(message, MediaType.APPLICATION_JSON);
                    Thread.sleep(200);
                }
                emitter.complete();
            } catch(Exception e){
                emitter.completeWithError(e);
            }

        }).start();

        emitter.onCompletion(()->{
            System.out.println("Complete");
        });
        emitter.onTimeout(()->{
            System.out.println("Timeout");
        });
        return emitter;
    }

    @GetMapping(value = "/single/output")
    public DeferredResult<String> singleAnswering(@RequestParam("filename") String filename){
        DeferredResult<String> deferredResult = new DeferredResult<>();

        new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(filename.startsWith("admin-")){
                deferredResult.setErrorResult("auth error");
            }
            deferredResult.setResult(filename);
        });
        return deferredResult;
    }

    @GetMapping(value = "/multi/output")
    public DeferredResult<List<String>> multiAnswering(@RequestParam("filename") String filename){
        DeferredResult<List<String>> deferredResult = new DeferredResult<>();

        new Thread(()->{
            if(filename.startsWith("admin-")){
                deferredResult.setErrorResult("auth error");
            }
            List list = new ArrayList<>();
            for(int i = 0 ; i < 5;i++){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                list.add(filename + (i+1));
            }

            deferredResult.setResult(list);
        }).start();


        return deferredResult;
    }
}
