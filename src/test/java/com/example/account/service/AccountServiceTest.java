package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSuccess() {
        AccountUser user = AccountUser.builder()
                        .id(12L)
                        .name("Daehun").build();
        given(accountUserRepository.findById(anyLong()))
            .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                                .accountNumber("10000000012").build()));
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("10000000015").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("10000000013", captor.getValue().getAccountNumber());
    }

    @Test
    void createFirstAccount() {
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Daehun").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("10000000015").build());
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("10000000000", captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("유저 당 최대 계좌는 10개")
    void createAccount_maxAccountIs10() {
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Daehun").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());
    }


    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound() {
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }



    @Test
    @DisplayName("계좌 조회 성공")
    void testXXX() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        //when
        Account account = accountService.getAccount(4555L);

        //then
        verify(accountRepository, times(1)).findById(captor.capture());
        verify(accountRepository, times(0)).save(any());
        assertEquals(4555L, captor.getValue());
        assertNotEquals(45515L, captor.getValue());
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 실패 - 음수로 조회")
    void testFailedToSearchAccount() {
        //given
        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getAccount(-10L));

        //then
        assertEquals("Minus", exception.getMessage());
    }

    @Test
    @DisplayName("Test 이름 변경")
    void testGetAccount() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    void testGetAccount2() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }
}