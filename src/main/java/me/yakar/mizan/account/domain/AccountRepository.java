package me.yakar.mizan.account.domain;

import java.util.Optional;

public interface AccountRepository {

  void save(Account account);

  Optional<AccountSummary> findSummary(AccountId accountId);
}
