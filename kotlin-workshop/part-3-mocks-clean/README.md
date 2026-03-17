# Part 3: Mocks Clean

## Context

You've inherited a codebase with an `OrderCancellationService` and its tests.

The tests were written by a developer who "knows what they're doing":
- Clean mocks with MockK
- `verifySequence` to ensure correct order of operations
- Good coverage

**All tests pass.** ✅

---

## Your Mission

1. **Read the code** — `OrderCancellationService.kt`
2. **Read the tests** — `OrderCancellationServiceTest.kt`
3. **Run the tests** — `./gradlew test`
4. **Answer:** Are these tests sufficient? Is the code correct?

---

## Time

~10 minutes

---

## Discussion

After reviewing, discuss with the group:

- What scenarios are tested?
- What scenarios are NOT tested?
- Would you trust this code to go to production?

---

## When the facilitator says "Open the bug report"

Go to `reveal/bug-report.md`

