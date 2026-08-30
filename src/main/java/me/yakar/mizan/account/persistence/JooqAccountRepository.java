package me.yakar.mizan.account.persistence;

import static me.yakar.mizan.db.tables.Accounts.ACCOUNTS;
import static me.yakar.mizan.db.tables.Entries.ENTRIES;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sum;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import me.yakar.mizan.account.domain.Account;
import me.yakar.mizan.account.domain.AccountId;
import me.yakar.mizan.account.domain.AccountRepository;
import me.yakar.mizan.account.domain.AccountSummary;
import me.yakar.mizan.account.domain.OwnerName;
import me.yakar.mizan.shared.money.Currencies;
import me.yakar.mizan.shared.money.Money;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record5;

public class JooqAccountRepository implements AccountRepository {

  private final DSLContext dsl;

  public JooqAccountRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public void save(Account account) {
    dsl.insertInto(ACCOUNTS)
        .set(ACCOUNTS.ID, account.id().value())
        .set(ACCOUNTS.OWNER_NAME, account.ownerName().value())
        .set(ACCOUNTS.CURRENCY, account.currency().getCurrencyCode())
        .set(ACCOUNTS.CREATED_AT, account.openedAt().atOffset(ZoneOffset.UTC))
        .execute();
  }

  @Override
  public Optional<AccountSummary> findSummary(AccountId accountId) {
    return dsl.select(
            ACCOUNTS.ID,
            ACCOUNTS.OWNER_NAME,
            ACCOUNTS.CURRENCY,
            ACCOUNTS.CREATED_AT,
            balanceOfCurrentAccount())
        .from(ACCOUNTS)
        .where(ACCOUNTS.ID.eq(accountId.value()))
        .fetchOptional()
        .map(JooqAccountRepository::toSummary);
  }

  private static Field<BigDecimal> balanceOfCurrentAccount() {
    Field<BigDecimal> postedAmounts =
        select(sum(ENTRIES.AMOUNT))
            .from(ENTRIES)
            .where(ENTRIES.ACCOUNT_ID.eq(ACCOUNTS.ID))
            .asField();
    return coalesce(postedAmounts, BigDecimal.ZERO);
  }

  private static AccountSummary toSummary(
      Record5<UUID, String, String, OffsetDateTime, BigDecimal> row) {
    Currency currency = Currencies.parse(row.value3());
    Account account =
        new Account(
            new AccountId(row.value1()),
            new OwnerName(row.value2()),
            currency,
            row.value4().toInstant());
    return new AccountSummary(account, Money.of(row.value5(), currency));
  }
}
