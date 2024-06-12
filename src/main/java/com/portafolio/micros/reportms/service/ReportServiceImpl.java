package com.portafolio.micros.reportms.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.stereotype.Service;

import com.netflix.discovery.EurekaClient;
import com.portafolio.micros.reportms.helper.ReportHelper;
import com.portafolio.micros.reportms.models.Company;
import com.portafolio.micros.reportms.models.WebSite;
import com.portafolio.micros.reportms.repositories.CompaniesFallBackRepository;
import com.portafolio.micros.reportms.repositories.CompaniesRepository;
import com.portafolio.micros.reportms.streams.ReportPublisher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService{

	private final CompaniesRepository companiesRepository;
	private final ReportHelper reportTemplate;
	private final CompaniesFallBackRepository companiesFallBackRepository;
	private final Resilience4JCircuitBreakerFactory circuitBrakerFactory;
	private final ReportPublisher reportPublisher;
	
	@Override
	public String makeReport(String name) {
		
		var circuitBraket = this.circuitBrakerFactory.create("company-circuitBraker");
		
		return circuitBraket.run(
				()-> this.makeReportMain(name),
				throwable -> this.makeReportFallback(name, throwable)
				);
			}
	
	public String makeReportMain(String name) {
		return this.reportTemplate.readTemplate(companiesRepository.getByName(name).orElseThrow());
	}
	
	public String makeReportFallback(String name,Throwable error ) {
		log.warn(error.getMessage());
		return this.reportTemplate.readTemplate(companiesFallBackRepository.getByName(name));
	}

	@Override
	public String saveReport(String report) {
		
		var format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		var placeholders = this.reportTemplate.getPlacesHolderFromTemplate(report);
		var webSites = Stream.of(placeholders.get(3))
				.map(webSite-> WebSite.builder().name(webSite).build()).toList();
		
		var company = Company.builder()
				.name(placeholders.get(0))
				.foundationDate(LocalDate.parse(placeholders.get(1),format))
				.founder(placeholders.get(2))
				.webSites(webSites)
				.build();
		this.reportPublisher.publishReport(report);
		this.companiesRepository.postByName(company);
		return "saved";
	}

	@Override
	public void deleteReport(String nameReport) {
		this.companiesRepository.deleteName(nameReport);
	}

}
