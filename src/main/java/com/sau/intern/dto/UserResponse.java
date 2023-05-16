package com.sau.intern.dto;

public record UserResponse(
        Long id,
        String name,
        String surName,
        String email,
        Long roleId
) {
}