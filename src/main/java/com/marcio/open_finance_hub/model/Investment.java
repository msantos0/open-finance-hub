package com.marcio.open_finance_hub.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "investments")
public class Investment {

    @Id
    private String id;

    private String userId;
    private InvestmentType type;
    private String institution;
    private String description;
    private BigDecimal investedAmount;
    private BigDecimal currentValue;
    private BigDecimal annualRate;
    private LocalDate applicationDate;
    private LocalDate maturityDate;
    private Instant createdAt;
    private Instant updatedAt;
}