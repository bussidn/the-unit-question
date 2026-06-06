export type CancellationResult =
  | { readonly kind: 'success' }
  | { readonly kind: 'failure'; readonly reason: string };

export const CancellationResult = {
  success: (): CancellationResult => ({ kind: 'success' }),
  failure: (reason: string): CancellationResult => ({ kind: 'failure', reason }),
};
