package com.ljt.exam.service;

import com.ljt.exam.model.Account;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountService {

    int addAccount(Account account);

    boolean updateAccount(Account account);

    Account getAccountByUsername(String username);

    Map<String,Object> getAccounts(int pageNum,int pageSize);

    List<Account> getAccountsByStudentIds(List<Integer> studentIds);

    boolean deleteAccount(int id);

    boolean disabledAccount(int id);

    boolean ableAccount(int id);

    List<Account> getAccountsByIds(Set<Integer> ids);

    Account getAccountById(int id);
}
