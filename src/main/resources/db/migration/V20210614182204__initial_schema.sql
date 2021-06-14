/******************************************************************/
/*************************     Bank              ******************/
/******************************************************************/
CREATE TABLE bank(
     id                    BIGSERIAL PRIMARY KEY,
     "name"                character varying(255)       NOT NULL,
     iban                  character varying(100)       NOT NULL,
     version               bigint                       NOT NULL DEFAULT 1,
     created_at            timestamp without time zone  NOT NULL,
     updated_at            timestamp without time zone  NOT NULL
);

ALTER TABLE ONLY bank
    ADD CONSTRAINT bank_unique01 UNIQUE (iban);

/******************************************************************/
/*************************     Customer          ******************/
/******************************************************************/
CREATE TABLE customer(
     id                    BIGSERIAL PRIMARY KEY,
     "name"                character varying(255)       NOT NULL,
     national_identifier   character varying(100)       NOT NULL,
     version               bigint                       NOT NULL DEFAULT 1,
     created_at            timestamp without time zone  NOT NULL,
     updated_at            timestamp without time zone  NOT NULL
);

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_unique01 UNIQUE (national_identifier);


/******************************************************************/
/*************************     Account          *******************/
/******************************************************************/
CREATE TABLE account(
     id                    BIGSERIAL PRIMARY KEY,
     amount                numeric(15,2)                NOT NULL,
     owner_id              bigint                       NOT NULL,
     bank_id               bigint                       NOT NULL,
     version               bigint                       NOT NULL DEFAULT 1,
     created_at            timestamp without time zone  NOT NULL,
     updated_at            timestamp without time zone  NOT NULL
);

ALTER TABLE ONLY account
    ADD CONSTRAINT account_unique01 UNIQUE (bank_id, owner_id);

/******************************************************************/
/*************************     Transfer         *******************/
/******************************************************************/
CREATE TABLE transfer(
    id                    BIGSERIAL PRIMARY KEY,
    amount                numeric(15,2)                NOT NULL,
    origin_id             bigint                       NOT NULL,
    destination_id        bigint                       NOT NULL,
    transfer_at           timestamp without time zone  NOT NULL,
    version               bigint                       NOT NULL DEFAULT 1,
    created_at            timestamp without time zone  NOT NULL,
    updated_at            timestamp without time zone  NOT NULL
);

ALTER TABLE ONLY transfer
    ADD CONSTRAINT transfer_unique01 UNIQUE (origin_id, destination_id, transfer_at);