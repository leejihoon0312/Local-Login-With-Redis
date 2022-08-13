package com.deadline826.bedi.Goal.exception;

import java.util.NoSuchElementException;

public class WrongGoalIDException extends RuntimeException {

    public WrongGoalIDException(String s) {
        super(s);
    }

}
