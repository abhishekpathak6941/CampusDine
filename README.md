# 🍽️ CampusDine — Intelligent QR Mess Card & Dining Management System

[![Java](https://img.shields.io/badge/Java-8%20%7C%2011%20%7C%2017%20%7C%2021%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![SQLite](https://img.shields.io/badge/SQLite-3.45-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org)
[![Platform](https://img.shields.io/badge/Platform-macOS%20%7C%20Linux%20%7C%20Windows-4A5568?style=for-the-badge)](https://github.com/abhishekpathak6941/CampusDine)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)
[![Author](https://img.shields.io/badge/Developed%20By-Abhishek%20Pathak-10B981?style=for-the-badge&logo=github)](https://github.com/abhishekpathak6941)

**CampusDine** is a digital campus mess management and dining attendance verification system designed for universities, colleges, and residential hostels. By pairing an interactive desktop administration kiosk with an embedded lightweight web server, students simply scan a dynamic meal QR code with their mobile phone camera, verify their credentials on a responsive mobile interface, and log their meal in seconds — completely eliminating manual paper mess cards and queues.

---

## 🌟 Key Highlights

- **⚡ Zero-App Mobile Scanning:** Students do not need to install any mobile app. Scanning the kiosk QR code opens a responsive web interface served directly by the built-in HTTP server.
- **🛡️ Strict Duplicate Prevention:** Intelligent database-level validation prevents double-scanning or multiple claims for the same meal session (Breakfast, Lunch, Snacks, Dinner) on the same day.
- **🏢 Multi-Tenant Mess Isolation:** Support for multiple independent mess sections (e.g., Mess Hall 1, Mess Hall 2, Girls Mess, Boys Mess). Each admin manages their own student roster, meal records, and kiosks with zero cross-tenant data leakage.
- **📊 Real-Time Analytics & Audit Logs:** Live tracking of daily headcounts, session-wise consumption statistics, and historical student dining audits.
- **🖥️ Lightweight Cross-Platform GUI:** Custom-styled, high-performance desktop interface built on Java AWT with custom double-buffered canvas components that render reliably across macOS, Linux, and Windows.
- **🔒 Standalone & Offline-Capable:** Self-contained SQLite database with automated table provisioning on startup — no external database server or cloud subscription required.

---

## 📐 System Workflow

```
┌────────────────────────┐
│   Admin / Scan Station │
│  (Desktop Application) │
└───────────┬────────────┘
            │ Generates dynamic meal QR codes
            ▼
┌────────────────────────┐
│  Kiosk Display Station │ ──► Breakfast / Lunch / Snacks / Dinner QRs
└───────────┬────────────┘
            │ Scanned via smartphone camera
            ▼
┌────────────────────────┐
│ Mobile Web Portal      │ ──► Served by embedded HTTP server (Port 8080)
│ (Phone Browser)        │     Roll No + Password Verification
└───────────┬────────────┘
            │ Instant validation against SQLite DB
            ▼
┌────────────────────────┐
│ ✅ Meal Verified & Logged
│ • Duplicate checks passed
│ • Real-time stats updated
│ • Student views meal history
└────────────────────────┘
```

---

## 👥 Access Roles & Permissions

| Role | Access Level | Responsibilities | Default Access |
|---|---|---|---|
| **Super Admin** | Full System Control | Create and manage mess supervisors/admins, change master credentials, audit all sections. | `superadmin` / `super@123` |
| **Mess Admin** | Section Scoped | Register students, generate daily QR codes, monitor section meal logs, supervise kiosk station. | Configured by Super Admin |
| **Student** | Personal Portal | Scan QR at dining hall, authenticate meal entry, inspect personal dining logs & counts. | Registered by Mess Admin |

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java Development Kit (JDK 8 or higher)** — JDK 11, 17, 21, or 25 recommended.
- **Git**

---

### macOS & Linux

1. **Clone the repository:**
   ```bash
   git clone https://github.com/abhishekpathak6941/CampusDine.git
   cd CampusDine
   ```

2. **Make scripts executable:**
   ```bash
   chmod +x compile.sh run.sh setup.sh
   ```

3. **(Optional) Fetch dependencies:**
   *Note: Pre-bundled JARs are included in `lib/`. If missing, run:*
   ```bash
   ./setup.sh
   ```

4. **Compile and launch:**
   ```bash
   ./compile.sh
   ./run.sh
   ```

---

### Windows

1. **Clone the repository:**
   ```cmd
   git clone https://github.com/abhishekpathak6941/CampusDine.git
   cd CampusDine
   ```

2. **Compile the source:**
   ```cmd
   mkdir out
   javac -cp "lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" -d out src\*.java
   ```

3. **Run the system:**
   ```cmd
   java -cp "out;lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" MessCardSystem
   ```

---

## 📡 Network & QR Code Configuration

The system runs an embedded HTTP server on **port 8080**. When a student scans the QR code at the entrance, their phone connects to this server.

### Mode 1: Local Campus Wi-Fi / Personal Hotspot (Default)
1. Connect the host computer and student phones to the same campus Wi-Fi network or mobile hotspot.
2. Launch the application. The system auto-detects your local IP (e.g. `192.168.1.X`) and embeds it in the QR codes.
3. Students scan the QR code and are immediately directed to the login page.

### Mode 2: Campus-Wide / Remote Access via Tunneling (ngrok)
To allow scanning over cellular data or across isolated subnets:
1. Start an ngrok tunnel on the host computer:
   ```bash
   ngrok http 8080
   ```
2. Copy the generated public URL (e.g., `https://xxxx.ngrok-free.app`).
3. In the Admin Panel, navigate to **Scan Station** → paste the URL → click **Apply**.
4. All generated QR codes will now route securely through the public endpoint.

---

## 🗄️ Database Architecture

The system utilizes an embedded relational SQLite database with the following schema:

```
  ┌────────────────────────┐          ┌────────────────────────┐
  │         admins         │          │        students        │
  ├────────────────────────┤          ├────────────────────────┤
  │ admin_id (PK, AutoInc) │◄──┐      │ roll_number (PK)       │
  │ username (Unique)      │   │      │ admin_id    (PK, FK)   │──────┐
  │ password               │   │      │ name                   │      │
  │ full_name              │   │      │ password               │      │
  │ created_date           │   │      │ reg_date               │      │
  └────────────────────────┘   │      └────────────────────────┘      │
                               │                                      │
                               │      ┌────────────────────────┐      │
                               │      │       meal_logs        │      │
                               │      ├────────────────────────┤      │
                               │      │ id         (PK, AutoInc)│     │
                               └──────│ admin_id   (FK)        │      │
                                      │ roll_number (FK)       │◄─────┘
                                      │ meal_type              │
                                      │ meal_date              │
                                      │ meal_time              │
                                      └────────────────────────┘
```

---

## 🛠️ Technology Stack

| Component | Technology | Rationale |
|---|---|---|
| **Desktop UI** | Java AWT & Custom 2D Graphics | Fast startup, zero heavy dependencies, native rendering across all desktop OS. |
| **Database** | SQLite 3 + Xerial JDBC | Zero configuration, ACID-compliant local storage with auto-initialization. |
| **QR Code Engine** | Google ZXing (Zebra Crossing) | Industrial-standard barcode & 2D QR matrix generation. |
| **Web Server** | `com.sun.net.httpserver` | Ultra-lightweight built-in HTTP server requiring no external container (Tomcat/Jetty). |
| **Mobile Client** | Semantic HTML5 & Modern CSS3 | Responsive dark-mode interface with mobile-first viewport optimization. |

---

## 📂 Project Directory Structure

```
CampusDine/
├── src/
│   ├── MessCardSystem.java           # Main application entry point & lifecycle manager
│   ├── DatabaseManager.java          # SQLite data access object (DAO) & operations
│   ├── MessWebServer.java            # Embedded HTTP server & mobile web endpoints
│   ├── QRCodeGenerator.java          # ZXing barcode/QR matrix generation wrapper
│   ├── MainFrame.java                # Central desktop dashboard & portal selector
│   ├── AdminLoginDialog.java         # Modal authentication for admins & super admin
│   ├── SuperAdminFrame.java          # Master administration panel
│   ├── AdminFrame.java               # Section supervisor panel (students, logs, QR)
│   ├── ScanStationFrame.java         # Kiosk station displaying live 4-meal QR codes
│   ├── MealLoginFrame.java           # Kiosk manual fallback authentication
│   ├── MealSuccessFrame.java         # Confirmation splash dialog
│   ├── StudentLoginFrame.java        # Student credentials portal
│   ├── StudentDashboardFrame.java    # Student attendance analytics & logs
│   ├── ChangeCredentialsDialog.java  # Password & credential update utility
│   ├── DatabaseAccessDialog.java     # Database password protection handler
│   ├── FlatButton.java               # Custom double-buffered UI button component
│   └── UIHelper.java                 # Central design system, palettes & formatters
├── lib/
│   ├── sqlite-jdbc.jar               # SQLite JDBC driver
│   ├── zxing-core.jar                # ZXing core encoding library
│   └── zxing-javase.jar              # ZXing JavaSE image renderer
├── compile.sh                        # Automated JDK detection & build script
├── run.sh                            # Execution launcher script
├── setup.sh                          # Dependency verification & download utility
├── .gitignore                        # Git ignore rules for build & database files
└── README.md                         # Documentation
```

---

## 👨‍💻 Author

**Abhishek Pathak**  
- GitHub: [@abhishekpathak6941](https://github.com/abhishekpathak6941)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
