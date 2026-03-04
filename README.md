# 🦁 Mufasa Robot - MT5 Trading Automation

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.02.00-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-26-brightgreen?style=for-the-badge&logo=android)
![Target SDK](https://img.shields.io/badge/targetSdk-35-brightgreen?style=for-the-badge&logo=android)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-blueviolet?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**Professional Mobile Trading Robot Controller for MetaTrader 5**

[Features](#-features) •
[Tech Stack](#-tech-stack) •
[Architecture](#-architecture) •
[Installation](#-installation) •
[Documentation](#-documentation) •
[Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Libraries](#-libraries)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Installation](#-installation)
- [Build Configuration](#-build-configuration)
- [Design System](#-design-system)
- [Testing Credentials](#-testing-credentials)
- [Future Backend Integration](#-future-backend-integration)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

---

## 🎯 Project Overview

**Mufasa Robot** is a professional-grade mobile trading robot controller designed to interface with MetaTrader 5 (MT5) accounts. This frontend application provides traders with real-time monitoring and control over their automated trading strategies.

### Purpose
Enable traders to:
- 🔌 Connect and monitor MT5 account status
- 🤖 Control bot operations (Start/Stop/Auto-trade)
- 📊 Track trade activity in real-time
- ⚙️ Configure automation parameters
- 🔔 Receive instant system feedback

Built with production-ready architecture, this app is designed for seamless backend integration, supporting REST APIs, WebSocket connections, and JWT authentication.

---

## ✨ Features

| Screen | Features |
|--------|----------|
| **Splash** | Animated logo, automatic navigation |
| **Login** | MT5-style credentials (Login ID, Server, Password), validation feedback, loading states |
| **Dashboard** | Connection status card with animated indicator |
| | Bot status card with start/stop controls |
| | Trade activity monitoring with P/L display |
| | Automation controls with risk level selection |
| | Real-time user feedback notifications |
| | Pull-to-refresh functionality |
| | Animated FAB for data refresh |

---

## 🛠 Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Kotlin | 2.1.0 |
| **UI Framework** | Jetpack Compose | 2025.02.00 |
| **Design System** | Material 3 | - |
| **Architecture** | Clean Architecture + MVVM | - |
| **Navigation** | Jetpack Navigation Compose | 2.8.7 |
| **Asynchronous** | Kotlin Coroutines & Flow | 1.8.1 |
| **Build System** | Gradle Kotlin DSL | 8.7.0 |
| **Min SDK** | Android 8.0 (Oreo) | 26 |
| **Target SDK** | Android 15 | 35 |

---

## 📚 Libraries

### Core Android
```kotlin
implementation("androidx.core:core-ktx:1.15.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
implementation("androidx.activity:activity-compose:1.10.1")

## Installation Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Ekisa02/Mufasa-Robot.git
   cd Mufasa-Robot
   ```

2. **Install Dependencies**:
   Ensure you have Python and pip installed, then run:
   ```bash
   pip install -r requirements.txt
   ```

3. **Set Up ROS**:
   Follow the instructions provided in the [ROS installation documentation](http://wiki.ros.org/ROS/Installation).

4. **Launch the Robot**:
   Start the Mufasa Robot using ROS launch files:
   ```bash
   roslaunch mufasa_robot.launch
   ```

## Design System Documentation
The design system for Mufasa Robot is based on modular design principles, allowing components to be added or removed as necessary. It follows best practices in software development, ensuring code is maintainable, scalable, and reusable. Documentation for the design system, including component specifications and design patterns, can be found in the `docs` directory of the repository.

### Contributions
We welcome contributions to improve Mufasa Robot. Please submit a pull request or open an issue if you have suggestions or improvements.
