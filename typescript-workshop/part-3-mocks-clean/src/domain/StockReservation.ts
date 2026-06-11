export class StockReservation {
  constructor(
    readonly productId: string,
    readonly quantity: number,
    readonly reserved: boolean,
  ) {}
}
