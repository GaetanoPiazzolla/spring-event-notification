package gae.piaz.pattern.events.config;

import gae.piaz.pattern.events.service.ApplicationEventQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    private final ApplicationEventQueue applicationEventQueue;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("WebConfig::addInterceptors()");
        registry.addInterceptor(new ThreadLocalClearingInterceptor(applicationEventQueue));
    }
}
