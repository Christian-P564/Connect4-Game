# 🔵 Connect4 Online: A Study in Java Concurrency

A robust multiplayer game server built to explore the intricacies of network programming and thread management. This project enables real-time Connect4 matches between online players, focusing heavily on solving the challenges of **concurrency** and **deadlock prevention** in a multi-client environment.

---

### 🚀 Key Features

* **Real-Time Matchmaking:** An efficient socket-based system that pairs players into game instances while respecting player preferences.
* **Concurrent Game Sessions:** The server handles multiple independent game instances simultaneously without cross-talk or performance degradation.
* **Integrated Chat:** A live chatbox feature allowing opponents to communicate during their match.
* **Robust Error Handling:** A custom failsafe system to manage mid-game disconnections, ensuring players aren't "locked in limbo" if an opponent leaves.
* **Rematch Logic:** Persistent connection handling that allows players to transition from a finished game into a rematch or back into the queue.

---

### 🛠️ Technical Stack

* **Language:** Java
* **GUI Framework:** JavaFX
* **Networking:** Java Sockets (TCP/IP)
* **Core Concepts:** Multi-threading, Synchronization, Deadlock Avoidance, and Wireframing.

---

### 🧠 The Development Journey

This project was built over a two-week sprint, moving from initial wireframes to a fully functional networked application. The primary goal was to bridge the gap between theoretical concurrency and practical implementation.

#### **Challenges & Solutions**

| Challenge | Solution |
| :--- | :--- |
| **Race Conditions** | Implemented strict object locking and synchronization to ensure multiple clients didn't corrupt shared game states. |
| **Deadlock/Freezing** | Diagnosed and resolved "catastrophic" server hangs by optimizing how threads requested resources during simultaneous games. |
| **Mid-Game Disconnects** | Developed a listener-based failsafe that detects lost socket connections and gracefully redirects the remaining player to the matchmaking queue. |
| **UI/UX Design** | Navigated the difficulty of sourcing assets by creating a clean, functional interface using JavaFX components and custom wireframes. |

---

### 📈 Learning Outcomes

The core of this project was mastering **Java Sockets**. By managing "listeners" and "senders" across different threads, I gained a deep understanding of:
1.  **Data Serialization:** How data is transmitted and interpreted across a network.
2.  **Timing & Synchronization:** Managing the issues inherent in multi-client systems where multiple players access objects simultaneously.
3.  **Iterative Design:** Moving from the "drawing board" to a doable, functional MVP within a strict deadline.

### Demo
https://github.com/user-attachments/assets/8cdcd51b-d5cd-490c-9098-5c5268f11a7f
