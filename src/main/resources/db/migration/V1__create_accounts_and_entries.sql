create table accounts
(
    id         uuid         primary key,
    owner_name varchar(100) not null,
    currency   char(3)      not null,
    created_at timestamptz  not null default now()
);

comment on table accounts is 'Account holders. Balances are never stored here, they are derived from entries.';

create table entries
(
    id         uuid           primary key,
    account_id uuid           not null references accounts (id),
    amount     numeric(19, 4) not null,
    created_at timestamptz    not null default now()
);

comment on table entries is 'Append-only ledger postings. The balance of an account is the sum of its entries.';

create index entries_account_id_idx on entries (account_id);
