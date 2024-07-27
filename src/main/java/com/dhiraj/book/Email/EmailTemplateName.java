package com.dhiraj.book.Email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATION_ACCOUNT("activation_account");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
