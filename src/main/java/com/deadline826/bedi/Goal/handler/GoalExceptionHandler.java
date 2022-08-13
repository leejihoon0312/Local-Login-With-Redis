package com.deadline826.bedi.Goal.handler;

import com.deadline826.bedi.Goal.exception.OutRangeOfGoalException;
import com.deadline826.bedi.Goal.exception.WrongGoalIDException;
import com.deadline826.bedi.exception.ErrorResponse;
import io.opencensus.trace.Status;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GoalExceptionHandler {

    @ExceptionHandler(OutRangeOfGoalException.class)
    public ResponseEntity<ErrorResponse> handleOutRangeOfGoalException() {
        ErrorResponse errorResponse = new ErrorResponse(403, "목표 위치로 부터 너무 멉니다.");
        return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(WrongGoalIDException.class)
    public ResponseEntity<ErrorResponse> handleWrongGoalIDException() {
        ErrorResponse errorResponse = new ErrorResponse(404, "잘못된 목표 아이디입니다.");
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(errorResponse);
    }

}
