export class PaymentRequest {
  constructor(
    readonly orderId: string,
    readonly customerId: string,
    readonly amount: number,
  ) {}
}
