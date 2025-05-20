package com.devsuperior.dsmeta.repositories;

import com.devsuperior.dsmeta.dto.SaleReportDTO;
import com.devsuperior.dsmeta.projections.SaleSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dsmeta.entities.Sale;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT new com.devsuperior.dsmeta.dto.SaleReportDTO(obj.id, obj.date, obj.amount, obj.seller.name) " +
            "FROM Sale obj " +
            "WHERE (obj.date BETWEEN :minDate AND :maxDate) " +
            "AND UPPER(obj.seller.name) LIKE UPPER(CONCAT('%', :name, '%'))")
    Page<SaleReportDTO> searchReport(LocalDate minDate, LocalDate maxDate, String name, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT tb_seller.name AS sellerName, SUM(tb_sales.amount) AS total " +
            "FROM tb_sales " +
            "INNER JOIN tb_seller ON tb_sales.seller_id = tb_seller.id " +
            "WHERE tb_sales.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY tb_seller.name")
    Page<SaleSummaryProjection> searchSummary(LocalDate minDate, LocalDate maxDate, Pageable pageable);

}
