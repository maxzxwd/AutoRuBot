package com.maxzxwd.autoruc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutorucApplication {

	@Nullable
	private final ServletWebServerApplicationContext webServerAppContext;

	private final static Logger logger = LoggerFactory.getLogger(AutorucApplication.class);

	@Autowired
	public AutorucApplication(@Nullable ServletWebServerApplicationContext webServerAppContext) {
		this.webServerAppContext = webServerAppContext;
	}

	public static void main(String[] args) {

		SpringApplication.run(AutorucApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void logRunningAddress() {

		if (webServerAppContext != null) {

			var port = Integer.toString(webServerAppContext.getWebServer().getPort());

			logger.info("Swagger at http://localhost:{}/swagger-ui.html", port);
		}
	}
}
