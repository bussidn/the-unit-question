import { PaymentRequest } from '../domain/PaymentRequest';
import { PaymentResult } from '../domain/PaymentResult';
import { PaymentStatus } from '../domain/PaymentStatus';
import { PaymentGateway } from '../gateway/PaymentGateway';
import { PaymentService } from './PaymentService';

export class GatewayPaymentService implements PaymentService {
  constructor(private readonly paymentGateway: PaymentGateway) {}

  processPayment(orderId: string, customerId: string, amount: number): PaymentResult {
    if (amount <= 0) {
      return new PaymentResult(orderId, PaymentStatus.Failed, null);
    }
    const request = new PaymentRequest(orderId, customerId, amount);
    const gatewayResult = this.paymentGateway.process(request);
    return new PaymentResult(orderId, gatewayResult.status, gatewayResult.transactionId);
  }

  refundPayment(transactionId: string): boolean {
    return this.paymentGateway.refund(transactionId);
  }
}
