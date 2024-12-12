package gae.piaz.pattern.events.config;

import gae.piaz.pattern.events.domain.Book;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestRepoConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        config.setReturnBodyOnUpdate(true)
                .setBasePath("crud")
                .exposeIdsFor(Book.class)
                .setReturnBodyForPutAndPost(true)
                .setReturnBodyOnCreate(true)
                .setReturnBodyOnDelete(true);

        cors.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
