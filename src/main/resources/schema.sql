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

CREATE TABLE Customer(
                         id INT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         address VARCHAR(500) NOT NULL,
                         INDEX idx_customer_name (name)
);

CREATE TABLE Contact(
                        contact VARCHAR(15) NOT NULL,
                        customer_id INT NOT NULL,
                        CONSTRAINT uk_contact UNIQUE KEY (contact),
                        CONSTRAINT pk_contact PRIMARY KEY (contact, customer_id)
);
ALTER TABLE Contact ADD CONSTRAINT fk_contact FOREIGN KEY (customer_id) REFERENCES Customer (id);



CREATE TABLE IF NOT EXISTS Loyalty
(
    customer_name VARCHAR(100)   NOT NULL,
    bill_number   INT            NOT NULL,
    bill_date     DATETIME       NOT NULL,
    bill_value    DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_customer_name FOREIGN KEY (customer_name) REFERENCES Customer(name) ,
    CONSTRAINT fk_bill_number FOREIGN KEY (bill_number) REFERENCES Bills (number),
    CONSTRAINT fk_bill_date FOREIGN KEY (bill_date) REFERENCES Bills (date_time)
);



CREATE TABLE User(
        username VARCHAR(50) PRIMARY KEY,
        full_name VARCHAR(100) NOT NULL,
        password VARCHAR(300) NOT NULL,
        role ENUM('ADMIN', 'USER') NOT NULL
);



CREATE TABLE IF NOT EXISTS Supplier(
                                       id INT PRIMARY KEY ,
                                       name VARCHAR(100) NOT NULL ,
                                       contact VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Batches(
                                      supplier_id INT NOT NULL ,
                                      supplier_name VARCHAR(100) NOT NULL ,
                                      batch_no INT PRIMARY KEY ,
                                      date DATETIME NOT NULL ,
                                      total DECIMAL(12,2) NOT NULL,
                                      CONSTRAINT supplier_id_batch_no FOREIGN KEY (supplier_id) REFERENCES Supplier(id)
);


CREATE TABLE IF NOT EXISTS Brands(
    brand_name VARCHAR(300) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS List_Of_Bikes(
                                            brand_name VARCHAR(300) NOT NULL ,
                                            bike VARCHAR(400) PRIMARY KEY ,
                                            CONSTRAINT brands_to_brand_name FOREIGN KEY(brand_name) REFERENCES Brands(brand_name)
);

CREATE TABLE IF NOT EXISTS Parts_Category(
    parts_category VARCHAR(500) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS Parts(
                                    parts_category VARCHAR(500) NOT NULL ,
                                    parts_type VARCHAR(600) PRIMARY KEY ,
                                    CONSTRAINT Parts_Category_to_Parts FOREIGN KEY (parts_category) REFERENCES Parts_Category(parts_category)
);

CREATE TABLE IF NOT EXISTS Items(
                                    role VARCHAR(100) NOT NULL ,
                                    batch_num INT NOT NULL ,
                                    item_code BIGINT  PRIMARY KEY ,
                                    brand_name VARCHAR(300) NOT NULL ,
                                    parts_category VARCHAR(500) NOT NULL ,
                                    model VARCHAR(100) NOT NULL ,
                                    item_name VARCHAR(200) NOT NULL ,
                                    supplier_price DECIMAL(12,2)NOT NULL ,
                                    net_price DECIMAL(12,2) NOT NULL ,
                                    qty  INT NOT NULL,
                                    discount DECIMAL(12,2) NOT NULL ,
                                    profit_percentage DECIMAL(12,2) NOT NULL,
                                    date_bought DATETIME NOT NULL ,
                                    selling_price DECIMAL(12,2) NOT NULL ,
                                    profit DECIMAL(12,2) NOT NULL ,
                                    price DECIMAL(12, 2) NOT NULL,
                                    CONSTRAINT batch_no_item FOREIGN KEY (batch_num) REFERENCES Batches(batch_no)


);

DROP TABLE IF EXISTS Loyalty;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Bills;
DROP TABLE IF EXISTS BillDescription;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Contact;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Items;
DROP TABLE IF EXISTS Parts;
DROP TABLE IF EXISTS Parts_Category;
DROP TABLE IF EXISTS List_Of_Bikes;
DROP TABLE IF EXISTS Brands;
DROP TABLE IF EXISTS Batches;
DROP TABLE IF EXISTS Supplier;












