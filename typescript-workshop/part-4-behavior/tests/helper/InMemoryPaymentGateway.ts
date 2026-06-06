import { PaymentRequest } from '../../src/domain/PaymentRequest';
import { PaymentResult } from '../../src/domain/PaymentResult';
import { PaymentStatus } from '../../src/domain/PaymentStatus';
import { PaymentGateway } from '../../src/gateway/PaymentGateway';

export class InMemoryPaymentGateway implements PaymentGateway {
  private declined = false;
  private lastResult: PaymentResult | null = null;
  private refundedTransactions: string[] = [];

  process(request: PaymentRequest): PaymentResult {
    if (this.declined) {
      this.lastResult = new PaymentResult(request.orderId, PaymentStatus.Failed, null);
    } else {
      this.lastResult = new PaymentResult(request.orderId, PaymentStatus.Success, 'TXN-' + request.orderId);
    }
    return this.lastResult;
  }

  refund(transactionId: string): boolean {
    this.refundedTransactions.push(transactionId);
    return true;
  }

  willDecline(): void {
    this.declined = true;
  }

  wasCharged(): boolean {
    return this.lastResult !== null && this.lastResult.status === PaymentStatus.Success;
  }

  lastTransactionId(): string | null {
    return this.lastResult?.transactionId ?? null;
  }

  wasRefunded(transactionId: string): boolean {
    return this.refundedTransactions.includes(transactionId);
  }
}
