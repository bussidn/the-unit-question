export class ShippingConfirmation {
  constructor(
    readonly orderId: string,
    readonly trackingNumber: string,
    readonly estimatedDelivery: string,
  ) {}
}
