import { describe, it, expect } from 'vitest';
import { PaymentStatus } from '../../src/domain/PaymentStatus';
import { GatewayPaymentService } from '../../src/service/GatewayPaymentService';
import { InMemoryPaymentGateway } from '../helper/InMemoryPaymentGateway';

describe('PaymentService', () => {
  it('p4_processPayment_returnsSuccess_whenGatewaySucceeds', () => {
    const paymentGateway = new InMemoryPaymentGateway();
    const paymentService = new GatewayPaymentService(paymentGateway);

    const result = paymentService.processPayment('ORDER-123', 'CUST-456', 100.0);

    expect(result.status).toBe(PaymentStatus.Success);
    expect(result.orderId).toBe('ORDER-123');
    expect(result.transactionId).toBe('TXN-ORDER-123');
  });

  it('p4_processPayment_returnsFailure_forZeroAmount_withoutCallingGateway', () => {
    const paymentGateway = new InMemoryPaymentGateway();
    const paymentService = new GatewayPaymentService(paymentGateway);

    const result = paymentService.processPayment('ORDER-123', 'CUST-456', 0.0);

    expect(result.status).toBe(PaymentStatus.Failed);
    expect(result.orderId).toBe('ORDER-123');
    expect(paymentGateway.wasCharged()).toBe(false);
  });

  it('p4_processPayment_returnsFailure_whenGatewayDeclines', () => {
    const paymentGateway = new InMemoryPaymentGateway();
    paymentGateway.willDecline();
    const paymentService = new GatewayPaymentService(paymentGateway);

    const result = paymentService.processPayment('ORDER-123', 'CUST-456', 100.0);

    expect(result.status).toBe(PaymentStatus.Failed);
    expect(result.transactionId).toBeNull();
  });

  it('p4_refundPayment_delegatesToGateway', () => {
    const paymentGateway = new InMemoryPaymentGateway();
    const paymentService = new GatewayPaymentService(paymentGateway);

    expect(paymentService.refundPayment('TXN-123')).toBe(true);
    expect(paymentGateway.wasRefunded('TXN-123')).toBe(true);
  });
});
