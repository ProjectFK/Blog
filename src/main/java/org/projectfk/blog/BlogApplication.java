package org.projectfk.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class BlogApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	/**
	 * Set default rawContent type as Json
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

//	@Bean
//	public ObjectMapper defaultJacksonConfig() {
//		return new ObjectMapper()
//				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
//				.setDateFormat(DateTimeFormatter.ISO_)
//	}


}
