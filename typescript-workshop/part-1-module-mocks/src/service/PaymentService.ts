import { PaymentResult } from '../domain/PaymentResult';

export class PaymentService {
  processPayment(orderId: string, customerId: string, amount: number): PaymentResult {
    throw new Error('Not implemented — requires payment gateway connection');
  }

  refundPayment(transactionId: string): boolean {
    throw new Error('Not implemented — requires payment gateway connection');
  }
}
