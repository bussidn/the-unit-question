import { PaymentRequest } from '../domain/PaymentRequest';
import { PaymentResult } from '../domain/PaymentResult';

export interface PaymentGateway {
  process(request: PaymentRequest): PaymentResult;
  refund(transactionId: string): boolean;
}
