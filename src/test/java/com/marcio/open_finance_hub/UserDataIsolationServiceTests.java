package com.marcio.open_finance_hub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.marcio.open_finance_hub.dto.AccountRequestDTO;
import com.marcio.open_finance_hub.dto.InvestmentRequestDTO;
import com.marcio.open_finance_hub.dto.TransactionRequestDTO;
import com.marcio.open_finance_hub.exception.CategoryNotFoundException;
import com.marcio.open_finance_hub.exception.InvestmentNotFoundException;
import com.marcio.open_finance_hub.model.Account;
import com.marcio.open_finance_hub.model.Category;
import com.marcio.open_finance_hub.model.Investment;
import com.marcio.open_finance_hub.model.InvestmentType;
import com.marcio.open_finance_hub.model.Transaction;
import com.marcio.open_finance_hub.model.TransactionType;
import com.marcio.open_finance_hub.model.User;
import com.marcio.open_finance_hub.repository.AccountRepository;
import com.marcio.open_finance_hub.repository.CategoryRepository;
import com.marcio.open_finance_hub.repository.InvestmentRepository;
import com.marcio.open_finance_hub.repository.TransactionRepository;
import com.marcio.open_finance_hub.repository.UserRepository;
import com.marcio.open_finance_hub.service.AccountService;
import com.marcio.open_finance_hub.service.CurrentUserService;
import com.marcio.open_finance_hub.service.DashboardService;
import com.marcio.open_finance_hub.service.InvestmentService;
import com.marcio.open_finance_hub.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class UserDataIsolationServiceTests {

    private static final String USER_ID = "user-1";
    private static final String EMAIL = "user@example.com";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private CurrentUserService currentUserService;

    @BeforeEach
    void setUpSecurityContext() {
        currentUserService = new CurrentUserService(userRepository);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(User.builder().id(USER_ID).email(EMAIL).build()));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(EMAIL, null, List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void assignsAuthenticatedUserWhenCreatingAccount() {
        Account savedAccount = Account.builder().id("account-1").userId(USER_ID).build();
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountService service = new AccountService(accountRepository, currentUserService);
        service.create(new AccountRequestDTO("Checking", "Bank", com.marcio.open_finance_hub.model.AccountType.CHECKING,
                BigDecimal.TEN, true));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(USER_ID, captor.getValue().getUserId());
    }

    @Test
    void listsOnlyAuthenticatedUserAccounts() {
        when(accountRepository.findByUserId(USER_ID)).thenReturn(List.of(Account.builder().id("account-1").userId(USER_ID).build()));

        AccountService service = new AccountService(accountRepository, currentUserService);
        service.findAll();

        verify(accountRepository).findByUserId(USER_ID);
    }

    @Test
    void assignsAuthenticatedUserWhenCreatingInvestment() {
        when(investmentRepository.save(any(Investment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvestmentService service = new InvestmentService(investmentRepository, currentUserService);
        service.create(investmentRequest());

        ArgumentCaptor<Investment> captor = ArgumentCaptor.forClass(Investment.class);
        verify(investmentRepository).save(captor.capture());
        assertEquals(USER_ID, captor.getValue().getUserId());
        assertNotNull(captor.getValue().getCreatedAt());
        assertEquals(captor.getValue().getCreatedAt(), captor.getValue().getUpdatedAt());
    }

    @Test
    void listsOnlyAuthenticatedUserInvestments() {
        when(investmentRepository.findByUserId(USER_ID)).thenReturn(List.of(
                Investment.builder().id("investment-1").userId(USER_ID).build()));

        InvestmentService service = new InvestmentService(investmentRepository, currentUserService);
        service.findAll();

        verify(investmentRepository).findByUserId(USER_ID);
    }

    @Test
    void updatesInvestmentOwnedByAuthenticatedUser() {
    Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
    Investment investment = Investment.builder()
        .id("investment-1")
        .userId(USER_ID)
        .createdAt(createdAt)
        .build();
    when(investmentRepository.findByIdAndUserId("investment-1", USER_ID))
        .thenReturn(Optional.of(investment));
    when(investmentRepository.save(any(Investment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    InvestmentService service = new InvestmentService(investmentRepository, currentUserService);
    var response = service.update("investment-1", investmentRequest());

    assertEquals("Bank", response.institution());
    assertEquals(createdAt, response.createdAt());
    verify(investmentRepository).findByIdAndUserId("investment-1", USER_ID);
    verify(investmentRepository).save(investment);
    }

    @Test
    void deletesInvestmentOwnedByAuthenticatedUser() {
    Investment investment = Investment.builder().id("investment-1").userId(USER_ID).build();
    when(investmentRepository.findByIdAndUserId("investment-1", USER_ID))
        .thenReturn(Optional.of(investment));

    InvestmentService service = new InvestmentService(investmentRepository, currentUserService);
    service.delete("investment-1");

    verify(investmentRepository).findByIdAndUserId("investment-1", USER_ID);
    verify(investmentRepository).delete(investment);
    }

    @Test
    void rejectsInvestmentReadsAndMutationsOwnedByAnotherUser() {
        when(investmentRepository.findByIdAndUserId("investment-2", USER_ID)).thenReturn(Optional.empty());
        InvestmentService service = new InvestmentService(investmentRepository, currentUserService);

        assertThrows(InvestmentNotFoundException.class, () -> service.findById("investment-2"));
        assertThrows(InvestmentNotFoundException.class, () -> service.update("investment-2", investmentRequest()));
        assertThrows(InvestmentNotFoundException.class, () -> service.delete("investment-2"));

        verify(investmentRepository, times(3)).findByIdAndUserId("investment-2", USER_ID);
    }

    @Test
    void rejectsMaturityDateBeforeApplicationDate() {
        InvestmentRequestDTO request = new InvestmentRequestDTO(
                InvestmentType.CDB,
                "Bank",
                "Fixed income",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1050),
                BigDecimal.valueOf(10),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 8, 1));
        InvestmentService service = new InvestmentService(investmentRepository, currentUserService);

            currentUserService.get();
        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void rejectsTransactionReferenceOwnedByAnotherUser() {
        when(categoryRepository.existsByIdAndUserId("category-1", USER_ID)).thenReturn(false);

        TransactionService service = new TransactionService(transactionRepository, categoryRepository, accountRepository,
                currentUserService);

        assertThrows(CategoryNotFoundException.class, () -> service.create(new TransactionRequestDTO(
                "Lunch", BigDecimal.TEN, TransactionType.EXPENSE, LocalDate.now(), "category-1", "account-1")));
        verify(categoryRepository).existsByIdAndUserId("category-1", USER_ID);
    }

    @Test
    void dashboardAggregatesOnlyAuthenticatedUserTransactions() {
        when(transactionRepository.findByUserId(USER_ID)).thenReturn(List.of(
                Transaction.builder().userId(USER_ID).amount(BigDecimal.TEN).transactionType(TransactionType.INCOME)
                        .transactionDate(LocalDate.of(2026, 9, 1)).build()));

        DashboardService service = new DashboardService(transactionRepository, categoryRepository, currentUserService);
        var summary = service.getSummary();

        assertEquals(BigDecimal.TEN, summary.totalIncome());
        assertEquals(BigDecimal.ZERO, summary.totalExpense());
        assertEquals(1, summary.transactionCount());
        verify(transactionRepository).findByUserId(USER_ID);
    }

    private InvestmentRequestDTO investmentRequest() {
        return new InvestmentRequestDTO(
                InvestmentType.CDB,
                "Bank",
                "Fixed income",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1050),
                BigDecimal.valueOf(10),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2027, 9, 1));
    }
}
