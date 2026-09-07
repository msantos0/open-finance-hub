package com.marcio.open_finance_hub.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.marcio.open_finance_hub.dto.AccountRequestDTO;
import com.marcio.open_finance_hub.dto.AccountResponseDTO;
import com.marcio.open_finance_hub.exception.AccountNotFoundException;
import com.marcio.open_finance_hub.model.Account;
import com.marcio.open_finance_hub.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponseDTO create(AccountRequestDTO request) {
        Instant now = Instant.now();
        Account account = Account.builder()
                .name(request.name().trim())
                .bank(request.bank().trim())
                .accountType(request.accountType())
                .initialBalance(request.initialBalance())
                .currentBalance(request.initialBalance())
                .active(request.active() == null || request.active())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return toResponse(accountRepository.save(account));
    }

    public List<AccountResponseDTO> findAll() {
        return accountRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<AccountResponseDTO> findActive() {
        return accountRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    public AccountResponseDTO findById(String id) {
        return toResponse(findAccount(id));
    }

    public AccountResponseDTO update(String id, AccountRequestDTO request) {
        Account account = findAccount(id);
        account.setName(request.name().trim());
        account.setBank(request.bank().trim());
        account.setAccountType(request.accountType());
        account.setInitialBalance(request.initialBalance());
        account.setActive(request.active() == null || request.active());
        account.setUpdatedAt(Instant.now());

        return toResponse(accountRepository.save(account));
    }

    public void delete(String id) {
        Account account = findAccount(id);
        accountRepository.delete(account);
    }

    private Account findAccount(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    private AccountResponseDTO toResponse(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getName(),
                account.getBank(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getCurrentBalance(),
                account.isActive(),
                account.getCreatedAt(),
                account.getUpdatedAt());
    }
}