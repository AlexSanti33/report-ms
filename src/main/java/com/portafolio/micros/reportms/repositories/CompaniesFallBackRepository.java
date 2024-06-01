package com.portafolio.micros.reportms.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import com.portafolio.micros.reportms.models.Company;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CompaniesFallBackRepository {
	
	private final WebClient webClient;
	private final String uri;
	
	public CompaniesFallBackRepository(WebClient webClient, @Value("${fallback.uri}")String uri) {
		this.webClient = webClient;
		this.uri = uri;
	}
	
	public Company getByName(String name) {
		log.warn("Calling fallback {}",uri);
		return this.webClient
				.get()
				.uri(uri,name)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Company.class)
				.log()
				.block();
	}
}
