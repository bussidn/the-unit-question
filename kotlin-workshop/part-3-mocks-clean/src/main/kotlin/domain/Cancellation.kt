package domain

sealed class CancellationResult {
    data object Success : CancellationResult()
    data class Failure(val reason: String) : CancellationResult()
}

