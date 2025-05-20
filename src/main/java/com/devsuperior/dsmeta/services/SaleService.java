package com.devsuperior.dsmeta.services;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.dto.SaleReportDTO;
import com.devsuperior.dsmeta.dto.SaleSummaryDTO;
import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.projections.SaleSummaryProjection;
import com.devsuperior.dsmeta.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class SaleService {

	@Autowired
	private SaleRepository repository;
	
	public SaleMinDTO findById(Long id) {
		Optional<Sale> result = repository.findById(id);
		Sale entity = result.get();
		return new SaleMinDTO(entity);
	}

	public Page<SaleReportDTO> report(String minDateString, String maxDateString, String name, Pageable pageable) {

		LocalDate minDate, maxDate;
		String pattern = "yyyy-MM-dd";

		// Data atual
		LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());

		if ((maxDateString == null) || (maxDateString.trim().isEmpty())) {
			maxDate = today;
		} else {
			maxDate = convertStringToDate(maxDateString, pattern);
		}

		if ((minDateString == null) || minDateString.trim().isEmpty()) {
			minDate = maxDate.minusYears(1L);
		} else {
			minDate = convertStringToDate(minDateString, pattern);
		}

		if (name == null) {
			name = "";
		}

		Page<SaleReportDTO> result = repository.searchReport(minDate, maxDate, name, pageable);
		return result;
	}

	public Page<SaleSummaryDTO> summary(String minDateString, String maxDateString, Pageable pageable) {

		LocalDate minDate, maxDate;
		String pattern = "yyyy-MM-dd";

		// Data atual
		LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());

		if ((maxDateString == null) || (maxDateString.trim().isEmpty())) {
			maxDate = today;
		} else {
			maxDate = convertStringToDate(maxDateString, pattern);
		}

		if ((minDateString == null) || minDateString.trim().isEmpty()) {
			minDate = maxDate.minusYears(1L);
		} else {
			minDate = convertStringToDate(minDateString, pattern);
		}

		Page<SaleSummaryProjection> page = repository.searchSummary(minDate, maxDate, pageable);
		Page<SaleSummaryDTO> result = page.map(x -> new SaleSummaryDTO(x));

		return result;
	}

	public static LocalDate convertStringToDate(String dateString, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDate.parse(dateString, formatter);
	}

}
