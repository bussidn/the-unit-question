namespace Part1.StaticDeps.Domain;

public abstract record CancellationResult
{
    public sealed record Success() : CancellationResult;
    public sealed record Failure(string Reason) : CancellationResult;
}
