DROP TABLE IF EXISTS User;
CREATE TABLE User(
        username VARCHAR(50) PRIMARY KEY,
        full_name VARCHAR(100) NOT NULL,
        password VARCHAR(300) NOT NULL,
        role ENUM('ADMIN', 'USER') NOT NULL
);

CREATE TABLE Customer(
                         id INT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         address VARCHAR(500) NOT NULL
);

CREATE TABLE Contact(
                        contact VARCHAR(15) NOT NULL,
                        customer_id INT NOT NULL,
                        CONSTRAINT uk_contact UNIQUE KEY (contact),
                        CONSTRAINT pk_contact PRIMARY KEY (contact, customer_id)
);

ALTER TABLE Contact ADD CONSTRAINT fk_contact FOREIGN KEY (customer_id) REFERENCES Customer (id);
