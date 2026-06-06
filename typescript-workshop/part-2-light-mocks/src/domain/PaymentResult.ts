import { PaymentStatus } from './PaymentStatus';

export class PaymentResult {
  constructor(
    readonly orderId: string,
    readonly status: PaymentStatus,
    readonly transactionId: string | null,
  ) {}
}
