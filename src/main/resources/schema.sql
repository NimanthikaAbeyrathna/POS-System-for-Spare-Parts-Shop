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
                                    role ENUM('ADMIN','USER') NOT NULL ,
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
                                    CONSTRAINT batch_no_item FOREIGN KEY (batch_num) REFERENCES Batches(batch_no)


);
