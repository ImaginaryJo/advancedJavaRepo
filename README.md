# Inventory Management System

## Overview
This Inventory Management System is a Java-based application designed to manage inventory records, employee details, customer information, and sales orders. The system interacts with a database to perform CRUD operations and provides various functionalities such as updating employee details, managing products, creating sales orders, and more.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)

## Features
1. **Employee Management**
   - Add new employees
   - Update employee details (name, username, password)
   - Security checks using challenge questions and answers
   - Logging system for tracking employee activities

2. **Product Management**
   - Add new products to the inventory
   - Update product details and quantity
   - Delete products from the inventory
   - Search for products

3. **Customer Management**
   - Add new customers
   - Search for customers
   - Create customer profiles

4. **Sales Order Management**
   - Create sales orders
   - Search for sales orders
   - Generate and print receipts
   - Update delivery day for sales orders

5. **Security and Logging**
   - Security checks using challenge questions
   - Logging of various activities and operations

## Prerequisites
- Java Development Kit (JDK)
- Database Management System (e.g., MySQL)
- JDBC Driver for connecting Java application to the database

## Setup and Installation
1. **Clone the Repository**
   ```sh
   git clone https://github.com/your-repository/inventory-management-system.git
   ```

2. **Setup Database**
   - Create a database and configure the tables as per the SQL schema provided.
   - Update the database connection details in the Java application.

3. **Compile and Run**
   - Navigate to the project directory and compile the Java files.
   - Run the `Main` class to start the application.

## Usage
1. **Employee Operations**
   - Log in using employee credentials.
   - Perform various operations such as updating personal details, managing products, and handling sales orders.

2. **Product Operations**
   - Add, update, delete, and search for products in the inventory.

3. **Customer Operations**
   - Create new customer profiles and search for existing customers.

4. **Sales Order Operations**
   - Create new sales orders, update delivery days, and search for sales orders.

5. **Logging**
   - Monitor the logs for tracking employee activities and operations.

**Note:** This README is a template and should be customized according to the actual details and requirements of the project. The project is not open for contributions and does not have a license for distribution or modification.
