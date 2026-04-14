# 🧠 OS Scheduler Simulator — SJF vs Priority

<img width="130" height="145" alt="capital-university" src="https://github.com/user-attachments/assets/9a940f0b-d3d3-454e-9413-dd7fd88d9f2e" align="right" />

**Faculty of Computing and Artificial Intelligence**
**Capital University** *~(Formerly Helwan University)*

**Course:** Operating Systems -1                     
**Instructor:** Prof. Ahmed Hisham                   
**Academic Year:** 2025/2026

---

## 📋 Project Overview

The OS Scheduler Simulator is an interactive desktop application designed to evaluate and compare CPU scheduling algorithms. Specifically, this project implements and contrasts **Preemptive Shortest Job First (SJF)** against **Preemptive Priority Scheduling**. The system provides visual Gantt charts and computes essential performance metrics to analyze the trade-offs between system efficiency (SJF) and task urgency (Priority).

---

## 🛠️ Technologies & Architecture

| Layer                | Technology / Pattern                                          |
|----------------------|---------------------------------------------------------------|
| Language             | Java (JDK 26+)                                                |
| GUI Framework        | JavaFX                                                        |
| Architecture         | Clean Architecture (Model-Scheduler-GUI separation)           |
| Design Patterns      | Strategy (for schedulers), Factory, Observer                  |
| Version Control      | Git + GitHub (branching strategy enforced)                    |

---

## 📐 Project Structure

The repository follows the strict separation of concerns required by the OS Algorithm Comparison guidelines:


---

## ✨ Core Modules & Key Features

The system is structured around the required evaluation rubrics spanning accurate algorithm execution and visualization.

### 🎛️ Input Handling & Validation
- **Dynamic Process Entry** — Add, remove, and modify processes dynamically before execution.
- **Strict Validation** — Rejects invalid numeric inputs, duplicate IDs, missing values, and negative priority/burst values.
- **Unified Workload Loader** — Ensures both algorithms are tested against the exact same dataset for fair comparison.

### ⏱️ Algorithm A: Preemptive SJF
- **Dynamic Shortest Job Selection** — Chooses the process with the shortest available burst time whenever the CPU becomes free.
- **Context Switching Simulation** — Accurately preempts running jobs when a new job with a shorter burst time arrives.
- **Tie-Breaking Protocol** — Documented rules for handling processes with identical remaining burst times (e.g., FCFS).

### 🚨 Algorithm B: Preemptive Priority
- **Urgency Execution** — Executes jobs based on strict priority values (Lower Value = Higher Priority).
- **Preemption Logic** — Interrupts running processes immediately if a job with a higher priority arrives in the ready queue.
- **Starvation Analysis Ready** — Built to highlight workloads where low-priority tasks face unfair delay.

### 📊 Visualization & Metrics Calculation
- **Interactive Gantt Charts** — Renders clear, separate Gantt charts for each algorithm with precise time markers and execution order.
- **Per-Process Metrics** — Calculates WT, TAT, and RT for every individual process.
- **System Averages** — Computes overall system averages consistent with the Gantt chart outputs.
- **Comparison Tables** — Side-by-side metric comparison to reveal the efficiency vs. urgency trade-offs.

---

## 👥 Team Members & Responsibilities

| Name                  | GitHub                                                             | Contribution Area |
|-----------------------|--------------------------------------------------------------------|-------------------|
| Abdelhalim Yasser     | [@abdelhalimyasser](https://github.com/abdelhalimyasser)           | - |
| Ali Samy              | [@AliSamy12](https://github.com/AliSamy12)                         | - |
| Abdelullah Nasser     | [@](https://github.com/)   | - |
| Nada Moustafa         | [@qNVDV](https://github.com/qNVDV)                                 | - |
| Nourhan Mohamed       | [@Nour-FCAI](https://github.com/Nour-FCAI)                         | - |
| Nessreen Salah        | [@](https://github.com/)                                           | - |

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK):** Version 26 or higher if realesed.
- **JavaFX SDK:** Compatible with your JDK version (or managed via Maven/Gradle).
- **IDE:** IntelliJ IDEA (Recommended), Apache / Oracle Netbeans, Eclipse, VS Code or even Notepad.

---

### Running the Project

#### 1. Clone the repository
```bash
git clone https://github.com/abdelhalimyasser/sjf-priority-scheduler-simulator.git
cd sjf-priority-scheduler-simulator
```

#### 2. Open in IntelliJ IDEA or your fav. IDE

#### 3. Configure JavaFX (If not using Maven/Gradle)

#### 4. Run the Main Application
```bash
src/gui/Main.java
```

---

<p align="center">
  <strong>FCAI – Capital University ~ (Formerly Helwan University)</strong><br>
  Operating Systems · Algorithm Comparison Project · 2025/2026
  <br>
  <span>© 2026 <strong>OS Team</strong>. All Rights Reserved.</span>
  Released under the <a href="LICENSE"><code>LICENSE</code></a>.
</p>
