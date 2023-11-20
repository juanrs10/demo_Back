// package com.example.demo.controllers;

// import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import com.example.demo.exceptions.IllegalOperationException;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


// @ControllerAdvice
// public class GlobalExceptionHandler {

//     private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//     @ExceptionHandler(IllegalOperationException.class)
//     public ResponseEntity<String> handleIllegalOperationException(IllegalOperationException e) {
//         logger.error("----ERROR CAPTURADO EN HANDLER--------{}", e.getMessage(), e);
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//     }

//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<String> handleException(Exception e) {
//         logger.error("----ERROR CAPTURADO EN HANDLER--------{}", e.getMessage(), e);
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la consulta.");
//     }
// }
