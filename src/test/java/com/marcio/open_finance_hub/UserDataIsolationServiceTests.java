package com.marcio.open_finance_hub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.marcio.open_finance_hub.dto.TransactionRequestDTO;
import com.marcio.open_finance_hub.exception.CategoryNotFoundException;
import com.marcio.open_finance_hub.model.Account;
import com.marcio.open_finance_hub.model.Category;
import com.marcio.open_finance_hub.model.Transaction;
import com.marcio.open_finance_hub.model.TransactionType;
import com.marcio.open_finance_hub.model.User;
import com.marcio.open_finance_hub.repository.AccountRepository;
import com.marcio.open_finance_hub.repository.CategoryRepository;
import com.marcio.open_finance_hub.repository.TransactionRepository;
import com.marcio.open_finance_hub.repository.UserRepository;
import com.marcio.open_finance_hub.service.AccountService;
import com.marcio.open_finance_hub.service.CurrentUserService;
import com.marcio.open_finance_hub.service.DashboardService;
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
}
