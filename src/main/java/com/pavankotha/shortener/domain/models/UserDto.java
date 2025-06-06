package com.pavankotha.shortener.domain.models;

import java.io.Serializable;

public record UserDto(Long id, String name) implements Serializable {
}
