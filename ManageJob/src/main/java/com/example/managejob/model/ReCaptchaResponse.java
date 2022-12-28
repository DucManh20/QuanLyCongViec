package com.example.managejob.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReCaptchaResponse {
    private boolean success;
    private String challenge_ts;
    private String hostName;
}
