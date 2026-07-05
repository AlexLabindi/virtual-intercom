# Smart Virtual Intercom (Network Geofencing-Based)

A secure, enterprise-grade virtual intercom system built with **Spring Boot 3.x**, **React**, and **WebRTC**.
It features automated network isolation using a Wi-Fi Captive Portal to ensure only physical visitors at the gate can trigger door-bell notifications and voice calls.

## 🚀 Key Features
- **Network Geofencing**: Operates entirely within an isolated local subnet via Captive Portal.
- **Real-Time Voice Communication**: Peer-to-Peer audio streaming powered by **WebRTC** and WebSockets signaling.
- **Enterprise Security**: Built-in Rate Limiting with **Resilience4j** (Max 3 calls/min per IP) to prevent DoS attacks.
- **Diagnostic Logging**: Request tracing using **SLF4J MDC** (Mapped Diagnostic Context) with unique session IDs.
- **Containerized Infrastructure**: Fully orchestrated environment using **Docker Compose** and **PostgreSQL**.

## 🛠️ Tech Stack
- **Backend**: Java 17/21, Spring Boot 3.x, Spring Data JPA, WebSockets, Resilience4j, Maven
- **Frontend**: React (SPA), WebRTC API
- **Database & DevOps**: PostgreSQL, Docker, Docker Compose

## 📦 How to Run (Local Setup)
1. Clone the repository.
2. Start the database infrastructure:
   ```bash
   docker-compose up -d