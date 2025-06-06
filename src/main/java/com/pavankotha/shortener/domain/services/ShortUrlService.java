package com.pavankotha.shortener.domain.services;

import com.pavankotha.shortener.ApplicationProperties;
import com.pavankotha.shortener.domain.entities.ShortUrl;
import com.pavankotha.shortener.domain.models.CreateShortUrlCmd;
import com.pavankotha.shortener.domain.models.ShortUrlDto;
import com.pavankotha.shortener.domain.repositories.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final EntityMapper entityMapper;
    private final ApplicationProperties properties;
    public ShortUrlService(ShortUrlRepository shortUrlRepository, EntityMapper entityMapper, ApplicationProperties properties) {
        this.shortUrlRepository = shortUrlRepository;
        this.entityMapper = entityMapper;
        this.properties = properties;
    }
    public List<ShortUrlDto> findAllPublicShortUrls() {

        return shortUrlRepository.findPublicShortUrls()
                .stream().map(entityMapper::toShortUrlDto).toList();
    }
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd){

        if(properties.validateOriginalUrl())
        {
            boolean urlExixts=UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            if(!urlExixts){
                System.out.println("In Runtime excep");
                throw new RuntimeException("Invalid Url "+cmd.originalUrl());
            }
        }
        var shortkey=generateUniqueShortKey();
        var shortUrl=new ShortUrl();
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setShortKey(shortkey);
        shortUrl.setCreatedBy(null);
        shortUrl.setIsPrivate(false);
        shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), ChronoUnit.DAYS));
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setClickCount(0L);
        shortUrlRepository.save(shortUrl);
        return entityMapper.toShortUrlDto(shortUrl);
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(SHORT_KEY_LENGTH);
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public Optional<ShortUrlDto> accessShortUrl(String shortKey) {
        Optional<ShortUrl> shortUrlOptional=shortUrlRepository.findByShortKey(shortKey);
      if(shortUrlOptional.isEmpty() ){
          return Optional.empty();
      }
      ShortUrl shortUrl=shortUrlOptional.get();
      if(shortUrl.getExpiresAt()!=null && shortUrl.getExpiresAt().isBefore(Instant.now())){
          return Optional.empty();
      }
      shortUrl.setClickCount(shortUrl.getClickCount()+1);
      shortUrlRepository.save(shortUrl);
      return shortUrlOptional.map(entityMapper::toShortUrlDto);


    }
}
