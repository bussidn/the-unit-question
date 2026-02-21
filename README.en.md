# The Unit Question

> What is a "unit" in unit test?

## 🎯 About

This repository contains material for a workshop (or presentation) about testing heuristics.

**The topic**: The notion of "unit" in unit tests is overloaded. Everyone has their own definition. This workshop lets you experience different testing approaches to discover which one works best for you.

## 📁 Structure

```
├── presentation/          # Presentation support and outline (live coding)
│
├── java/                  # Workshop in Java
├── javascript/            # Workshop in JavaScript
├── python/                # Workshop in Python
├── kotlin-workshop/       # Workshop in Kotlin
│   ├── part-1-powermock/
│   ├── part-2-light-mocks/
│   ├── part-3-mocks-clean/
│   └── part-4-behavior/
│
└── kotlin-presentation/   # Demo code for conference format
```

## 🛠️ Prerequisites

Depending on your language choice:
- **Java**: JDK 17+, Maven or Gradle
- **JavaScript**: Node.js 18+
- **Python**: Python 3.10+
- **Kotlin**: JDK 17+, Gradle

## 🚀 Workshop Flow (1h45)

| Phase | Duration | Description |
|-------|----------|-------------|
| Introduction | ~10 min | Personal story and analogy |
| Part 1 - PowerMock | ~15 min | Extreme isolation (light exercise) |
| Part 2 - Light Mocks | ~25 min | Evolution A with classic mocks |
| Transition | ~5 min | Improvement: factorize mocks |
| Part 3 - Mocks Clean | ~25 min | Evolution B (same inputs, different output) |
| Part 4 - Behavior | ~20 min | Redo evolutions A and B |
| Conclusion | ~5 min | The moral: What vs How |

## 📝 Evolutions to implement

### Evolution A
*Add promo code management*

### Evolution B
*When stock is insufficient, set the order to "backorder" instead of rejecting it*

## 📄 License

MIT

