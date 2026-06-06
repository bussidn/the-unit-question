namespace Part3.MocksClean.Domain;

public abstract record CancellationResult
{
    public sealed record Success() : CancellationResult;
    public sealed record Failure(string Reason) : CancellationResult;
}
