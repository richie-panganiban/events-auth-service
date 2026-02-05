package com.birthday.invitation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BirthdayInvitationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BirthdayInvitationApplication.class, args);
    }
}
