Ledger Manager - JavaFX Ledger Management Module
---

📘 Ledger Manager - JavaFX Ledger Management Module

A ledger management module based on JavaFX, providing a graphical interface for viewing, creating, categorizing, and opening multiple ledgers, with support for cover images and category management.

---

🚀 Project Overview

This module is the `LedgerManagerController` of a JavaFX application, featuring:

- Displaying a list of today’s and historical ledgers (with optional cover images)
- Dialog to create new ledgers
- Sorting ledgers by time or category
- Opening specific ledger pages for operations

---

✨ Key Features

Multi-ledger Management**: Manages multiple ledger objects via `LedgerManager`, supporting dynamic addition and retrieval.
Cover Image Support**: Allows cover images for a visual display of each ledger.
Date-based Categorization**: Automatically organizes ledgers into "Today" or historical sections (e.g., "July").
New Ledger Dialog**: A standalone modal window for creating new ledgers.
Sorting Capabilities**: Sort ledgers by creation date or category.
Modular Page Loading**: Uses `MainController` to dynamically load ledger pages for seamless navigation.

---

 📷 UI Features

| Feature         | Description                                                 |
|------------------|-------------------------------------------------------------|
| 📅 Today's Ledgers | Ledgers created today are shown in the `todayGrid` pane     |
| 📦 Historical Ledgers | Other ledgers are displayed in `julyGrid` for browsing     |
| ➕ Create Ledger   | A dialog appears for input; upon confirmation, the ledger is added |
| 🔍 Sorting         | Quickly sort the list by time or category                  |
| 📖 Open Ledger     | Click any ledger button to load and open its detail page  |

---

🛠 Tech Stack

- Java 17+
- JavaFX 17
- FXML + CSS
- MVC Architecture

---

⚙️ How to Use

1. Import the project into a JavaFX-compatible IDE (e.g., IntelliJ IDEA).
2. Ensure the JavaFX SDK is properly configured.
3. Run the main application and navigate to the ledger management view.
4. Click "Create Ledger" to add a new ledger; click any existing ledger to open its detail page.
5. Use the "Sort by Time" or "Sort by Category" buttons to organize the view.

---

## ⚠ Notes

- Cover images must be local `File` objects with valid paths.
- The interface relies on `/fxml` and `/css` resources—ensure resource paths remain consistent.
- Ledger pages are handled by `LedgerController`, which must implement the `loadLedger(Ledger ledger)` method.

---

📄 License

This project is for learning and demonstration purposes only. For commercial use, please contact the author for licensing.

