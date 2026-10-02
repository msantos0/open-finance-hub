package com.marcio.open_finance_hub.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.marcio.open_finance_hub.dto.InvestmentRequestDTO;
import com.marcio.open_finance_hub.dto.InvestmentResponseDTO;
import com.marcio.open_finance_hub.exception.InvestmentNotFoundException;
import com.marcio.open_finance_hub.model.Investment;
import com.marcio.open_finance_hub.repository.InvestmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final CurrentUserService currentUserService;

    public InvestmentResponseDTO create(InvestmentRequestDTO request) {
        validateDates(request);
        String userId = currentUserService.get().getId();
        Instant now = Instant.now();
        Investment investment = Investment.builder()
                .userId(userId)
                .type(request.type())
                .institution(request.institution().trim())
                .description(request.description().trim())
                .investedAmount(request.investedAmount())
                .currentValue(request.currentValue())
                .annualRate(request.annualRate())
                .applicationDate(request.applicationDate())
                .maturityDate(request.maturityDate())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return toResponse(investmentRepository.save(investment));
    }

    public List<InvestmentResponseDTO> findAll() {
        return investmentRepository.findByUserId(currentUserService.get().getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public InvestmentResponseDTO findById(String id) {
        return toResponse(findInvestment(id));
    }

    public InvestmentResponseDTO update(String id, InvestmentRequestDTO request) {
        validateDates(request);
        Investment investment = findInvestment(id);
        investment.setType(request.type());
        investment.setInstitution(request.institution().trim());
        investment.setDescription(request.description().trim());
        investment.setInvestedAmount(request.investedAmount());
        investment.setCurrentValue(request.currentValue());
        investment.setAnnualRate(request.annualRate());
        investment.setApplicationDate(request.applicationDate());
        investment.setMaturityDate(request.maturityDate());
        investment.setUpdatedAt(Instant.now());

        return toResponse(investmentRepository.save(investment));
    }

    public void delete(String id) {
        investmentRepository.delete(findInvestment(id));
    }

    private Investment findInvestment(String id) {
        return investmentRepository.findByIdAndUserId(id, currentUserService.get().getId())
                .orElseThrow(() -> new InvestmentNotFoundException(id));
    }

    private void validateDates(InvestmentRequestDTO request) {
        if (request.maturityDate() != null && request.applicationDate() != null
                && request.maturityDate().isBefore(request.applicationDate())) {
            throw new IllegalArgumentException("Maturity date must be on or after application date");
        }
    }

    private InvestmentResponseDTO toResponse(Investment investment) {
        return new InvestmentResponseDTO(
                investment.getId(),
                investment.getType(),
                investment.getInstitution(),
                investment.getDescription(),
                investment.getInvestedAmount(),
                investment.getCurrentValue(),
                investment.getAnnualRate(),
                investment.getApplicationDate(),
                investment.getMaturityDate(),
                investment.getCreatedAt(),
                investment.getUpdatedAt());
    }
}