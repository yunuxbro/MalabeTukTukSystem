# 🛺 Malabe Tuk-Tuk & Three-Wheeler Spares Depot POS & Inventory System

A **JavaFX desktop Point of Sale (POS) and Inventory Management System** developed for the **Malabe Tuk-Tuk & Three-Wheeler Spares Depot**.

The application manages inventory, dealer information, sales transactions, and audit logging while demonstrating the implementation of fundamental data structures and algorithms without relying on Java's built-in sorting utilities.

---

## ✨ Features

* 📦 Inventory Management (Add, Update, Delete Parts)
* 🔍 Multi-criteria Inventory Search & Filtering
* 📉 Dynamic Low Stock Monitoring
* 🛒 Point of Sale (POS) Shopping Cart
* 💰 Bulk Purchase & Synergy Discount Rules
* 🎲 Random Dealer Selection
* 🔤 Manual Bubble Sort Implementation
* 📝 Real-time Audit Logging
* 📂 Legacy Text File Parsing & Data Loading
* 🧪 JUnit 5 Unit Testing

---

# 🛠 Technologies Used

| Technology | Version                              |
| ---------- | ------------------------------------ |
| Java       | OpenJDK 26 (Compatible with JDK 21+) |
| JavaFX     | OpenJFX 21.0.6                       |
| Build Tool | Apache Maven                         |
| Testing    | JUnit 5 (Jupiter)                    |
| IDE        | IntelliJ IDEA (Recommended)          |

---

# 📁 Project Structure

```text
MalabeTukTuk/
│
├── data/
│   ├── audit_log.txt
│   ├── dealers_legacy.txt
│   ├── inventory_legacy.txt
│   ├── updated_data_legacy.txt
│   ├── brakepad.png
│   ├── canopy.png
│   ├── cl_cable.jpg
│   ├── filter_ape.jpeg
│   ├── hl_bulb.jpg
│   ├── mirror.png
│   ├── piston4s.jpg
│   ├── spark.jpg
│   └── tyer.png
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/malabetuktuk/
│   │   │
│   │   │── blueprints/
│   │   │     ├── Cartitems.java
│   │   │     ├── Dealer.java
│   │   │     └── Part.java
│   │   │
│   │   │── CRUD/
│   │   │     ├── AddItem.java
│   │   │     ├── DeleteItem.java
│   │   │     └── UpdateItem.java
│   │   │
│   │   │── loadFiles/
│   │   │     ├── DealerLoad.java
│   │   │     └── PartLoad.java
│   │   │
│   │   │── POS/
│   │   │     ├── InventoryFilter.java
│   │   │     ├── LowStock.java
│   │   │     ├── managerClass.java
│   │   │     └── RandomDealers.java
│   │   │
│   │   │── sorts/
│   │   │     ├── dealerSort.java
│   │   │     └── itemSort.java
│   │   │
│   │   ├── CartController.java
│   │   ├── DealersController.java
│   │   ├── HelloApplication.java
│   │   ├── InventoryController.java
│   │   ├── Launcher.java
│   │   ├── MainController.java
│   │   └── module-info.java
│   │
│   └── resources/
│
├── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

---

# 🚀 Running the Application

## Option 1 — IntelliJ IDEA (Recommended)

1. Clone the repository.

```bash
git clone https://github.com/yuhan20250383/MalabeTukTuk.git
```

2. Open the project in **IntelliJ IDEA**.

3. Set the Project SDK to **JDK 21+** (JDK 26 recommended).

4. Ensure the following files exist inside the project root:

```text
data/
    inventory_legacy.txt
    dealers_legacy.txt
```

5. Navigate to:

```text
src/main/java/org/example/malabetuktuk/Launcher.java
```

6. Run:

```
Launcher.main()
```

---

## Option 2 — Maven

From the project root:

```bash
mvn clean javafx:run
```

---

# 🧪 Running the Unit Tests

## IntelliJ IDEA

* Open `src/test`
* Right-click the **test** directory
* Select **Run Tests**

---

## Maven

```bash
mvn test
```

---

# 📌 System Assumptions

### 📂 Data Directory

The application expects a **data/** directory in the project root containing:

* `inventory_legacy.txt`
* `dealers_legacy.txt`

---

### 🚚 Dealer Selection

* Four dealers are selected randomly at startup.
* Dealers are then sorted alphabetically by location before display.

---

### 📉 Low Stock Monitoring

Each inventory item maintains its own minimum stock threshold through the `Part` object.

There is **no global low-stock limit**.

---

### 📝 Audit Logging

The following actions automatically append timestamped records to:

```text
data/audit_log.txt
```

* Adding inventory
* Updating inventory
* Deleting inventory
* Checkout transactions

---

### 🔤 Sorting

Manual **Bubble Sort** implementations are used for:

* Inventory sorting
* Dealer sorting

Sorting is performed **in memory only** and does **not** modify the original legacy text files.

---

# 🧠 Algorithms Implemented

* Bubble Sort
* Multi-criteria Search & Filtering
* Random Dealer Selection
* Bulk Discount Calculation
* Synergy Discount Rules
* Legacy Text File Parsing
* Low Stock Detection

---

# 📷 Data Resources

The project includes product images stored inside the `data/` directory for use within the JavaFX interface.

Examples include:

* Brake Pads
* Canopy
* Clutch Cable
* Oil Filter
* Headlight Bulb
* Mirror
* Piston
* Spark Plug
* Tyre

---

# 🔗 Repository

GitHub Repository:

**https://github.com/yuhan20250383/MalabeTukTuk**

---

# 👨‍💻 Author

**Yuhan**

JavaFX POS & Inventory Management System

Built as an academic project demonstrating Java programming, object-oriented design, manual algorithm implementation, file handling, and JavaFX desktop application development.
