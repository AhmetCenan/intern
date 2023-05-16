package com.sau.intern.dto;

public record UserRequest(
        String name,
        String surName,
        String email,
        Long roleId
) {
}