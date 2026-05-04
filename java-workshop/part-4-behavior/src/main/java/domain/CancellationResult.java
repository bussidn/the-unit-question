package domain;

public sealed interface CancellationResult {
    record Success() implements CancellationResult {
    }

    record Failure(String reason) implements CancellationResult {
    }
}