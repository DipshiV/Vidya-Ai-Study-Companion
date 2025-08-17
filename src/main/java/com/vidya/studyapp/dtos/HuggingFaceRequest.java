package com.vidya.studyapp.dtos;

public class HuggingFaceRequest {
    private String inputs;

    public HuggingFaceRequest(String inputs) {
        this.inputs = inputs;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }
}

