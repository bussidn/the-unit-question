// In Part 1, PaymentService is a concrete class with no dependencies injected.
// It is always used via vi.mock() in OrderServiceTest, never tested in isolation
// (it would just throw "Not implemented — requires payment gateway connection").
//
// This file documents the vi.mock() pattern for PaymentService.

vi.mock('../../src/service/PaymentService');

import { describe, it, expect, vi } from 'vitest';
import { PaymentResult } from '../../src/domain/PaymentResult';
import { PaymentStatus } from '../../src/domain/PaymentStatus';
import { PaymentService } from '../../src/service/PaymentService';

describe('PaymentService (module mock pattern)', () => {
  it('p1_paymentService_canBeFullyControlledViaModuleMock', () => {
    // When vi.mock() is used, the constructor is intercepted and all methods
    // become vi.fn() automatically — no real implementation is called.
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(
      new PaymentResult('ORDER-001', PaymentStatus.Success, 'TXN-789'),
    );

    const service = new PaymentService();
    const result = service.processPayment('ORDER-001', 'CUST-001', 100.0);

    expect(result.status).toBe(PaymentStatus.Success);
    expect(result.transactionId).toBe('TXN-789');
    // The real implementation (which throws) was never called
    expect(vi.mocked(PaymentService.prototype.processPayment)).toHaveBeenCalledOnce();
  });
});
