package com.codeexecutor.CodeTestEx.controller;


import com.codeexecutor.CodeTestEx.model.FunctionRequest;
import com.codeexecutor.CodeTestEx.service.FunctionCallerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/function")
public class FunctionController {

    @Autowired
    private FunctionCallerService functionCallerService;

    @PostMapping("/call")
    public ResponseEntity<Object> callFunction(@RequestBody FunctionRequest request) {
        try {
            Object result = functionCallerService.executeFunction(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}