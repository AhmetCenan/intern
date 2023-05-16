package com.sau.intern.dto;

public record SignupDto(
        String name,
        String surName,
        String email,
        String password,
        Long roleId
) {
}