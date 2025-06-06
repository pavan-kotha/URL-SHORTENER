package com.pavankotha.shortener.domain.services;

import com.pavankotha.shortener.domain.entities.ShortUrl;
import com.pavankotha.shortener.domain.entities.User;
import com.pavankotha.shortener.domain.models.UserDto;
import com.pavankotha.shortener.domain.models.ShortUrlDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {


    public ShortUrlDto toShortUrlDto(ShortUrl shortUrl) {
        UserDto userDto = null;
        if(shortUrl.getCreatedBy() != null) {
            userDto = toUserDto(shortUrl.getCreatedBy());
        }

        return new ShortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                userDto,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName());
    }
}
