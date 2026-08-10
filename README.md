# Poker en Réseau

Multiplayer poker game supporting Texas Hold'em and Five-Card Draw with client-server architecture.

---

## Features
- **Two poker variants**: Texas Hold'em and Five-Card Draw
- **Network play**: Client-server with multi-threading
- **AI opponents**: Basic probability-based AI
- **Dual interface**: GUI (Swing) and terminal text mode
- **Turn timer**: 60-second timeout per turn

---

## Gameplay Screenshots

![Registration for a new game](./images/Aspose.Words.91a4f964-dbc5-493d-b531-bfc1e1b47d5e.005.png)
*Registration screen*

![Pre-flop view](./images/Aspose.Words.91a4f964-dbc5-493d-b531-bfc1e1b47d5e.006.png)
*Pre-flop view*

![Game in progress](./images/Aspose.Words.91a4f964-dbc5-493d-b531-bfc1e1b47d5e.007.png)
*Game in progress*

![Game in progress with cards displayed](./images/Aspose.Words.91a4f964-dbc5-493d-b531-bfc1e1b47d5e.008.png)
*Game in progress with cards displayed*

---

## Tech Stack
- Java
- Swing (GUI)
- Gradle
- GitLab/GitHub

---

## Setup & Run
```bash
./gradlew build
java -jar server.jar    # Start server
java -jar client.jar    # Start client(s)
```
---

## Known Issues

- All-in partially implemented
- GUI display inconsistencies across machines
- Advanced AI currently non-functional