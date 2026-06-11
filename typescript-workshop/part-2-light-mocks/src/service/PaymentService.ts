import { PaymentResult } from '../domain/PaymentResult';

export interface PaymentService {
  processPayment(orderId: string, customerId: string, amount: number): PaymentResult;
  refundPayment(transactionId: string): boolean;
}
