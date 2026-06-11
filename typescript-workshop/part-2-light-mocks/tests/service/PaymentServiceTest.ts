import { describe, it, expect, vi } from 'vitest';
import { PaymentRequest } from '../../src/domain/PaymentRequest';
import { PaymentResult } from '../../src/domain/PaymentResult';
import { PaymentStatus } from '../../src/domain/PaymentStatus';
import type { PaymentGateway } from '../../src/gateway/PaymentGateway';
import { GatewayPaymentService } from '../../src/service/GatewayPaymentService';

describe('PaymentService', () => {
  it('p2_processPayment_returnsSuccess_whenGatewaySucceeds', () => {
    const paymentGateway = { process: vi.fn(), refund: vi.fn() } as unknown as PaymentGateway;
    const paymentService = new GatewayPaymentService(paymentGateway);

    (paymentGateway as any).process.mockReturnValue(new PaymentResult('', PaymentStatus.Success, 'TXN-789'));

    const result = paymentService.processPayment('ORDER-123', 'CUST-456', 100.0);

    expect(result.status).toBe(PaymentStatus.Success);
    expect(result.orderId).toBe('ORDER-123');
    expect(result.transactionId).toBe('TXN-789');
    expect((paymentGateway as any).process).toHaveBeenCalledWith(
      expect.objectContaining({
        orderId: 'ORDER-123',
        customerId: 'CUST-456',
        amount: 100.0,
      }),
    );
  });

  it('p2_processPayment_returnsFailure_forZeroAmount_withoutCallingGateway', () => {
    const paymentGateway = { process: vi.fn(), refund: vi.fn() } as unknown as PaymentGateway;
    const paymentService = new GatewayPaymentService(paymentGateway);

    (paymentGateway as any).process.mockReturnValue(new PaymentResult('', PaymentStatus.Success, 'TXN-789'));

    const result = paymentService.processPayment('ORDER-123', 'CUST-456', 0.0);

    expect(result.status).toBe(PaymentStatus.Failed);
    expect(result.orderId).toBe('ORDER-123');
    expect((paymentGateway as any).process).not.toHaveBeenCalled();
  });

  it('p2_processPayment_returnsFailure_whenGatewayFails', () => {
    const paymentGateway = { process: vi.fn(), refund: vi.fn() } as unknown as PaymentGateway;
    const paymentService = new GatewayPaymentService(paymentGateway);

    (paymentGateway as any).process.mockReturnValue(new PaymentResult('', PaymentStatus.Failed, null));

    const result = paymentService.processPayment('ORDER-123', 'CUST-456', 100.0);

    expect(result.status).toBe(PaymentStatus.Failed);
    expect(result.transactionId).toBeNull();
  });

  it('p2_refundPayment_delegatesToGateway', () => {
    const paymentGateway = { process: vi.fn(), refund: vi.fn() } as unknown as PaymentGateway;
    const paymentService = new GatewayPaymentService(paymentGateway);

    (paymentGateway as any).refund.mockReturnValue(true);

    expect(paymentService.refundPayment('TXN-123')).toBe(true);
    expect((paymentGateway as any).refund).toHaveBeenCalledWith('TXN-123');
  });
});
