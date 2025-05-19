package com.devsuperior.dsmeta.services;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import com.devsuperior.dsmeta.dto.SaleReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;

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

		if (maxDateString == null || maxDateString.trim().isEmpty()) {
		// data final não informada (pegar a data atual)
			maxDate = today;
		} else {
		// data final informada
			maxDate = convertStringToDate(maxDateString, pattern);
		}

		if (minDateString == null || minDateString.trim().isEmpty()) {
			if (maxDateString == null || maxDateString.trim().isEmpty()) {
			// data inicial não informada, 	data final também não informada (pegar a atual)
				minDate = today.minusYears(1L);
			} else {
			// data inicial não informada, mas data final foi informada
				minDate = maxDate.minusYears(1L);
			}
		} else {
		// data inicial informada
			minDate = convertStringToDate(minDateString, pattern);
		}

		if (name == null) {
			name = "";
		}

		Page<SaleReportDTO> result = repository.searchReport(minDate, maxDate, name, pageable);
		return result;
	}

	public static LocalDate convertStringToDate(String dateString, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDate.parse(dateString, formatter);
	}

}
