package com.pavankotha.shortener.web.controllers;

import com.pavankotha.shortener.ApplicationProperties;
import com.pavankotha.shortener.domain.entities.ShortUrl;
import com.pavankotha.shortener.domain.exceptions.ShortUrlNotFoundException;
import com.pavankotha.shortener.domain.models.CreateShortUrlCmd;
import com.pavankotha.shortener.domain.models.ShortUrlDto;
import com.pavankotha.shortener.domain.services.ShortUrlService;
import com.pavankotha.shortener.web.dtos.CreateShortUrlForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {


    private final ShortUrlService shortUrlService;
    private final ApplicationProperties properties;

    public HomeController(ShortUrlService shortUrlService, ApplicationProperties properties) {
        this.shortUrlService = shortUrlService;
        this.properties = properties;

    }

    @GetMapping("/")
    public String home(Model model) {
      List<ShortUrlDto> shortUrls= shortUrlService.findAllPublicShortUrls();
      model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("createShortUrlForm", new CreateShortUrlForm(""));
      return "index";
    }

    @PostMapping("/short-urls")
    String createShortUrl(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if(bindingResult.hasErrors()) {
            List<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls();
            model.addAttribute("shortUrls", shortUrls);
            model.addAttribute("baseUrl", properties.baseUrl());
            return "index";
        }
        try {
            CreateShortUrlCmd cmd=new CreateShortUrlCmd(form.originalUrl());
            var shortUrlDto= shortUrlService.createShortUrl(cmd);


            redirectAttributes.addFlashAttribute("successMessage", "Short URL created successfully "+
                   properties.baseUrl()+shortUrlDto.shortKey());
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create short URL");
        }
        return "redirect:/";
    }

    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable String shortKey) {
       Optional<ShortUrlDto> shortUrlDtoOptional= shortUrlService.accessShortUrl(shortKey);
       if(shortUrlDtoOptional.isEmpty())
       {
           throw new ShortUrlNotFoundException("Invalid short url "+shortKey);
       }
       return "redirect:"+shortUrlDtoOptional.get().originalUrl();
    }
}
