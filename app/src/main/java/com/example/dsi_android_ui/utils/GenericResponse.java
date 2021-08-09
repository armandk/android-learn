package com.example.dsi_android_ui.utils;

import androidx.annotation.Nullable;

public class GenericResponse <T> {

    private State state;
    private T response;
    private Error error;

    public GenericResponse(State state, @Nullable T response, Error error) {
        this.state = state;
        this.response = response;
        this.error = error;
    }

    public State getState() {
        return state;
    }

    public T getResponse() {
        return response;
    }

    public Error getError() {
        return error;
    }

    public static enum State{
        SUCCESS, FAILURE, LOADING
    }

    public static class Error{
        private  int errorCode;
        private String errorMessage;

        public Error(int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
