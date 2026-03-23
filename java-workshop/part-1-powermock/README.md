# The Unit Question — Java Workshop

> **What is a "unit" in unit test?**

## 🎯 About this workshop

Everyone talks about "unit tests", but nobody agrees on what a "unit" is.

- Is it a method? A class? A module?
- Should we mock everything? Nothing? Something in between?
- Why do some test suites break with every refactoring while others survive major changes?

**This workshop makes you experience different testing approaches** through hands-on exercises. You'll feel the friction of each strategy and discover which one works best for you.

### Format

4 parts, each with a different approach to unit testing. You'll implement features and experience firsthand the pros and cons of each strategy.

---

## 🛠️ Prerequisites

### Required

| Tool | Version | Check command |
|------|---------|---------------|
| **Java (JDK)** | 21+ | `java -version` |
| **Git** | any | `git --version` |

### Included (no installation needed)

- **Gradle 9.3** — via the wrapper (`./gradlew`)
- **JUnit 5.11** — downloaded by Gradle
- **Mockito 5.14** — downloaded by Gradle

---

## 📦 Before you start

### 1. Clone the repository

```bash
git clone https://github.com/bussidn/the-unit-question.git
cd the-unit-question/java-workshop/part-1-powermock
```

### 2. Verify the setup

Run the tests to make sure everything works:

```bash
./gradlew test
```

You should see:

```
BUILD SUCCESSFUL
```

> ⚠️ **On Windows**, use `gradlew.bat test` instead.

### 3. Open in your IDE

Open the `part-1-powermock` folder in your favorite IDE (IntelliJ IDEA recommended).

Make sure your IDE:
- Recognizes the project as a Gradle project
- Uses JDK 21+
- Can run the tests

---

## ✅ You're ready!

If the tests pass, you're all set for the workshop.

**See you there!** 🚀

---

## 🏋️ The exercise

👉 **[exercises/email-notification.md](exercises/email-notification.md)**

---

## 📁 Project structure

```
part-1-powermock/
├── src/
│   ├── main/java/           # Production code
│   │   ├── domain/          # Domain objects (Order, Stock, etc.)
│   │   ├── infrastructure/  # External systems (Database, APIs)
│   │   ├── repository/      # Data access
│   │   └── service/         # Business logic
│   │
│   └── test/java/           # Test code
│       └── service/         # Tests for services
│
├── build.gradle             # Dependencies & configuration
└── gradlew                  # Gradle wrapper (use this!)
```

---

## ❓ Troubleshooting

### "java: error: release version 21 not supported"

Your IDE is using an older JDK. Configure it to use JDK 21+.

**IntelliJ**: File → Project Structure → Project SDK → 21

### "Could not resolve dependencies"

Check your internet connection. Gradle needs to download dependencies.

```bash
./gradlew --refresh-dependencies test
```

### Tests fail with "UnsupportedClassVersionError"

You're running with an old Java version. Check `java -version` and install JDK 21+.

