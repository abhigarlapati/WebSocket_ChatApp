# WebSocket-Based Chat Application (Backend-Only)

## Overview

This project is a real-time chat application backend built using **Spring Boot WebSockets**. It enables users to communicate in **public** and **private** chat modes via WebSocket connections. The backend handles **user sessions, message broadcasting, and private messaging** without any frontend UI, making it ideal for integration with various frontend frameworks or external clients like Postman or WebSocket testing tools.

---

## Features

✅ **WebSocket-Based Communication** – Enables real-time bidirectional messaging between clients.\
✅ **Public Chat** – Users can join a shared chatroom where messages are broadcasted to all participants.\
✅ **Private Chat** – Users can see a list of online users and start **one-to-one** private conversations.\
✅ **User Session Management** – Keeps track of connected users and removes them upon disconnection.\
✅ **Automatic User List Updates** – Sends real-time updates when users join or leave the chat.

---

## Technical Stack

- **Spring Boot** – Backend framework for managing WebSocket connections.
- **Spring WebSockets** – Handles real-time communication between users.
- **ConcurrentHashMap** – Stores user sessions efficiently.
- **Jackson (ObjectMapper)** – Parses JSON messages for easy communication.

---

## How It Works

1. **User Connects**

   - Users establish a WebSocket connection with a **username** and **chat mode** (public/private).

2. **Public Chat**

   - Messages are broadcasted to **all users in public mode**.
   - When a user **joins or leaves**, a system message is sent to the public chat.

3. **Private Chat**

   - Users receive a **list of active users** and can select a user to chat with.
   - Messages are sent **directly** to the chosen recipient.

4. **User Disconnects**

   - The session is removed, and the user list updates in real-time.

---

## API Endpoints (WebSocket Messages)

| **Action**               | **WebSocket Message Format (JSON)**                   | **Description**                    |
| ------------------------ | ----------------------------------------------------- | ---------------------------------- |
| **Connect**              | `ws://localhost:8080/chat?username=John&mode=public`  | User joins public/private chat.    |
| **Send Public Message**  | `{"recipient":"public", "message":"Hello everyone!"}` | Broadcasts message to public chat. |
| **Send Private Message** | `{"recipient":"Mike", "message":"Hey Mike!"}`         | Sends a direct message to "Mike".  |
| **Receive Message**      | `{"from":"John", "message":"Hello!"}`                 | User receives a message.           |
| **User List Update**     | `{"type":"userList", "users":["John","Mike"]}`        | Sends the latest online user list. |

---

## Deployment

### Prerequisites

- Java 17+ installed
- Maven installed

### Running Locally

```sh
# Clone the repository
git clone https://github.com/your-username/chat-app-backend.git
cd chat-app-backend

# Build and run the application
mvn spring-boot:run
```

### Testing WebSocket Connection

Use **Postman** or a WebSocket client to connect to:

```
ws://localhost:8080/chat?username=YourName&mode=public
```

---

## Future Enhancements

🚀 Add message persistence using a database (e.g., MySQL, MongoDB).\
🚀 Integrate authentication (e.g., JWT-based login system).\
🚀 Build a frontend using React/Angular for UI interactions.

---

This backend provides a **scalable** and **efficient** real-time messaging system. You can integrate it with a frontend or use it as a backend API for WebSocket-based applications. 🚀

