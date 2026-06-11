using System.Reflection;

namespace Part1.StaticDeps.Tests.Helper;

/// <summary>
/// Simulates PowerMock's private/internal method invocation using reflection.
/// In Java: PowerMock.invokePrivate(instance, "methodName", args)
/// In C#:  ReflectionHelper.InvokeInternal&lt;TReturn&gt;(instance, "MethodName", args)
/// <para>
/// Because <c>IsValid</c> is <c>internal virtual</c> in
/// <see cref="Part1.StaticDeps.Service.OrderService"/>, it can also be called
/// directly from the test assembly (via InternalsVisibleTo).
/// This helper exists for parity with the Java PowerMock.invokePrivate() pattern.
/// </para>
/// </summary>
public static class ReflectionHelper
{
    /// <summary>
    /// Invokes a non-public (private or internal) instance method by name using reflection.
    /// </summary>
    /// <typeparam name="TReturn">The expected return type of the method.</typeparam>
    /// <param name="instance">The object on which to invoke the method.</param>
    /// <param name="methodName">The name of the method to invoke.</param>
    /// <param name="args">Arguments to pass to the method.</param>
    /// <returns>The return value cast to <typeparamref name="TReturn"/>.</returns>
    public static TReturn InvokeInternal<TReturn>(object instance, string methodName, params object[] args)
    {
        var type = instance.GetType();

        // Walk up past any proxy/generated classes (e.g., NSubstitute proxies)
        while (type is not null
               && (type.Name.Contains("Proxy") || type.Name.Contains("Castle") || type.Name.Contains("__"))
               && type.BaseType is not null)
        {
            type = type.BaseType;
        }

        if (type is null)
        {
            throw new InvalidOperationException(
                $"Could not resolve concrete type for '{instance.GetType().Name}'.");
        }

        var method = type.GetMethod(
            methodName,
            BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public)
            ?? throw new ArgumentException(
                $"Method '{methodName}' not found in '{type.Name}'.");

        var result = method.Invoke(instance, args);
        return (TReturn)result!;
    }
}
