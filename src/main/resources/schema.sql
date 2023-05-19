CREATE TABLE IF NOT EXISTS Bills
(
    number       INT PRIMARY KEY,
    date_time    DATETIME       NOT NULL,
    cashier_name VARCHAR(500)   NOT NULL,
    total_price  DECIMAL(12, 2) NOT NULL,
    cash         DECIMAL(12, 2) NOT NULL,
    balance      DECIMAL(12, 2) NOT NULL
);
ALTER TABLE Bills ADD INDEX date_time_idx (date_time);
ALTER TABLE Bills ADD INDEX total_price_idx (total_price);


INSERT INTO Bills
VALUES (1, '2023-04-11 12:30:00', 'kasun', 100.00, 150.00, 50.00),
       (2, '2022-04-11 11:30:00', 'kasun', 200.00, 250.00, 50.00);


CREATE TABLE IF NOT EXISTS BillDescription
(
    bill_number INT            NOT NULL,
    item_code   BIGINT            NOT NULL,
    item        VARCHAR(200)   NOT NULL,
    unit_price  DECIMAL(12, 2) NOT NULL,
    quantity    INT            NOT NULL,
    price       DECIMAL(12, 2) NOT NULL,
    CONSTRAINT supplier_bill_no FOREIGN KEY (bill_number) REFERENCES Bills (number),
    PRIMARY KEY (bill_number, item_code)

);

INSERT INTO BillDescription
VALUES (1, 1232, 'key', 20.00, 1, 20.00),
       (1, 1233, 'bolt', 10.00, 2, 20.00);



CREATE TABLE IF NOT EXISTS Items
(

    item_code     BIGINT PRIMARY KEY,
    item_name     VARCHAR(200)   NOT NULL,
    selling_price DECIMAL(12, 2) NOT NULL,
    qty           INT            NOT NULL,
    price         DECIMAL(12, 2) NOT NULL

);

INSERT INTO Items
VALUES (1, 'generator', 50.00, 5, 250.00),
       (2, 'key', 20.00, 10, 100.00),
       (3, 'bolt', 10.00, 10, 100.00);

CREATE TABLE IF NOT EXISTS Customer
(
    name    VARCHAR(200) PRIMARY KEY,
    address VARCHAR(200) NOT NULL,
    contact VARCHAR(200) NOT NULL

);


INSERT INTO Customer
VALUES ('kasun', 'panadura', '077-9301085'),
       ('saman', 'kandy', '081-5656946');


CREATE TABLE IF NOT EXISTS Loyalty
(
    customer_name VARCHAR(200)   NOT NULL,
    bill_number   INT            NOT NULL,
    bill_date     DATETIME       NOT NULL,
    bill_value    DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_customer_name FOREIGN KEY (customer_name) REFERENCES Customer (name),
    CONSTRAINT fk_bill_number FOREIGN KEY (bill_number) REFERENCES Bills (number),
    CONSTRAINT fk_bill_date FOREIGN KEY (bill_date) REFERENCES Bills (date_time)
);

DROP TABLE IF EXISTS Loyalty;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Bills;
DROP TABLE IF EXISTS BillDescription;
DROP TABLE IF EXISTS Items;
