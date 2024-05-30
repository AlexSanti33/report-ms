package com.portafolio.micros.reportms.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.portafolio.micros.reportms.service.ReportService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path="report")
@AllArgsConstructor
public class ReportController {

	private ReportService reportService;
	
	@GetMapping(path="{name}")
	public ResponseEntity<Map<String, String>>getReport(@PathVariable String name){
		
		Map<String, String> response = Map.of("report",this.reportService.makeReport(name));
		return ResponseEntity.ok(response);
	}	
	@PostMapping()
	public ResponseEntity<String>postReport(@RequestBody String name){
		
		var response = this.reportService.saveReport(name);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping(path="{name}")
	public ResponseEntity<?>deleteReport(@PathVariable String name){
		this.reportService.deleteReport(name);
		return ResponseEntity.noContent().build();
	}
}
