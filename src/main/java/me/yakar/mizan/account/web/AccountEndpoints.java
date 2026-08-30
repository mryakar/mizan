package me.yakar.mizan.account.web;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import me.yakar.mizan.account.application.AccountService;
import me.yakar.mizan.account.domain.AccountId;
import me.yakar.mizan.account.domain.AccountSummary;
import me.yakar.mizan.account.domain.OwnerName;
import me.yakar.mizan.shared.error.ValidationException;
import me.yakar.mizan.shared.money.Currencies;

public class AccountEndpoints {

  private final AccountService accounts;

  public AccountEndpoints(AccountService accounts) {
    this.accounts = accounts;
  }

  public void register(JavalinConfig configuration) {
    configuration.routes.post("/accounts", this::openAccount);
    configuration.routes.get("/accounts/{id}", this::showAccount);
  }

  private void openAccount(Context context) {
    OpenAccountRequest request = readRequest(context);
    AccountSummary summary =
        accounts.open(new OwnerName(request.ownerName()), Currencies.parse(request.currency()));
    context
        .status(201)
        .header("Location", "/accounts/" + summary.account().id())
        .json(AccountResponse.from(summary));
  }

  private void showAccount(Context context) {
    AccountId accountId = AccountId.parse(context.pathParam("id"));
    context.json(AccountResponse.from(accounts.summaryOf(accountId)));
  }

  private OpenAccountRequest readRequest(Context context) {
    try {
      return context.bodyAsClass(OpenAccountRequest.class);
    } catch (Exception unreadable) {
      throw new ValidationException(null, "The request body is not valid JSON");
    }
  }
}
