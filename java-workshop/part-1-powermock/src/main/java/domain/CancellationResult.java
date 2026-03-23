package domain;

public sealed interface CancellationResult permits CancellationResult.Success, CancellationResult.Failure {
    record Success() implements CancellationResult {}

    record Failure(String reason) implements CancellationResult {}
}