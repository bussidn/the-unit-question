export enum DiscountCode {
  SUMMER20 = 'SUMMER20',
  ONCE50 = 'ONCE50',
  WELCOME = 'WELCOME',
}

export const discountPercentage: Record<DiscountCode, number> = {
  [DiscountCode.SUMMER20]: 20,
  [DiscountCode.ONCE50]: 50,
  [DiscountCode.WELCOME]: 10,
};
