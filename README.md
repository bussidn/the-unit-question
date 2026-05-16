# The Unit Question

> What is a "unit" in a unit test?

## 🎯 About

This repository contains material for a workshop (or presentation) about testing heuristics.

**The topic**: The notion of "unit" in unit tests is overloaded. Everyone has their own definition. This workshop lets you experience different testing approaches to discover which one works best for you.

## 🧭 Workshop flow

The workshop is built around a single business domain: **a small online shop**. You'll find production code centered on order placement, payment, inventory, shipping, and notifications.

The goal is not to discover a new domain in every exercise, but instead to **stay in a stable functional world** so you can better feel the impact of your testing choices.

The codebase is organized into five packages — `domain`, `service`, `infrastructure`, `repository`, and `gateway` — and exposes several services: `OrderService`, `StockService`, `PricingService`, `PaymentService`, and `ShippingService`. The central entry point is `OrderService.placeOrder(Order)`, which orchestrates the full order flow by coordinating stock reservation, pricing calculation, payment processing, and shipping dispatch. This method is the focus of every exercise throughout the workshop.

In each language, the workshop unfolds in **4 parts that are meant to be completed in order**. Each part asks you to implement **the same feature** — but with a different testing toolbox. This way, you can directly compare how each approach affects the way you write, read and maintain tests.

> 💡 **Hint**: The production code is the same across all parts. Once you've implemented the feature in one part, you can copy your production code to the next one and focus solely on the testing approach.

Detailed instructions, business scenarios, and the feature to implement are provided in each part's `README`.

## 💻 Choose a language

- **Java** — [Start with Part 1](java-workshop/part-1-powermock/README.md)
- **Kotlin** — [Start with Part 1](kotlin-workshop/part-1-powermock/README.md)

## 📄 License

MIT

