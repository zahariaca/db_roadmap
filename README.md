# Overview

1. Create a `Vending machine` console program. The data should be persisted in a file. (M1) Milestone 1
2. Extend the M1 program to persist the data into a MySql database.  (M2) Milestone 2
3. Extend the M2 program to create a Rest API instead of console client. (M3) Milestone 3
4. Provide a tool for suppliers to update in relatime their prices to different vending machines. 


## Milestone 1

Create a vending machine  console application that will persist the data in a file. The data must contains: "products info", "all transactions" and "supplier information".

### Use cases to cover
1. A supplier can introduce new product
2. A supplier can change product information(name, description, price)
3. A customer is able to buy throu console application a product

### Learning path
1. OOP principles 
2. Java 1.8 (Collections, Functional Interfaces, Threads, Java IO, Exceptions)
3. Serialize/Deserialise object to a file(Persist state of a program)


## Milestone 2

Upgrade the `M1` application with `basic patterns` where applies and persist the data into a `mysql` database.

### Use cases to cover
1. Change the persistence layer from a file to mysql db
2. Decouople domain data from the project.
3. Write your own DAO implementation for the product entity. The CRUD abstract class must apply for the further entities as well.
4. Apply basic patterns where apply.

### Learning path 
1. [Basic patterns](https://www.tutorialspoint.com/design_pattern/)
2. [DAO pattern](https://www.tutorialspoint.com/design_pattern/data_access_object_pattern.htm)
3. [JDBC](https://www.tutorialspoint.com/jdbc/jdbc-sample-code.htm)


## Milestone 3 

Create a spring boot application that will provide `vending machine` functionality through a `rest api` and persist data into `mysql` db.

### Use cases to cover
1. Use the rest api through http to be able to create/update/delete/get product data
2. Use the rest api through http to be able to buy products
3. The api should consider REST API [conventions](https://restfulapi.net/). 

### Learning path
1. Spring
2. Inversion Of Control Pattern
3. Rest API concepts
4. JPA

## Milestone 4
Provide to suppliers the possibility to update their prices on a particular `vending machine`.

### Use cases to cover
1. A supplier can connect using a client to a vending machine pipe and push a message related to price upgrade on a particular product.
2. The application must have a router using `apache flink` to process data and persist the updates accordingly to a database. The processing must validate price information berfore inserting to db.

### Learning path
1. [Apazhe Zookeeper](https://zookeeper.apache.org/)
2. [Apache Kafka](https://kafka.apache.org/)
3. [Apache Flink](https://flink.apache.org/)




