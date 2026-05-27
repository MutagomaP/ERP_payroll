-- Initialize Deduction Rates Table with Required Percentages
-- Run this script after the application creates the tables

-- Note: These rates are hardcoded in the PayrollService for calculation
-- This table is for reference and future configurability

INSERT INTO deduction_rate (code, deduction_name, percentage, is_active, created_at, updated_at) VALUES
('TAX-001', 'Employee Tax', 30.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PEN-001', 'Pension', 6.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MED-001', 'Medical Insurance', 5.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CSH-001', 'Cash Advance', 5.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SNK-001', 'Sinking Fund', 1.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TRN-001', 'Transport', 14.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('HSG-001', 'Housing', 14.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Verify the data
SELECT * FROM deduction_rate;

-- Expected Output:
-- +----+---------+-------------------+------------+-----------+---------------------+---------------------+
-- | id | code    | deduction_name    | percentage | is_active | created_at          | updated_at          |
-- +----+---------+-------------------+------------+-----------+---------------------+---------------------+
-- |  1 | TAX-001 | Employee Tax      |      30.00 |         1 | 2025-01-15 10:00:00 | 2025-01-15 10:00:00 |
-- |  2 | PEN-001 | Pension           |       6.00 |         1 | 2025-01-15 10:00:00 | 2025-01-15 10:00:00 |
-- |  3 | MED-001 | Medical Insurance |       5.00 |         1 | 2025-01-15 10:00:00 | 2025-01-15 10:00:00 |
-- |  4 | CSH-001 | Cash Advance      |       5.00 |         1 | 2025-01-15 10:00:00 | 2025-01-15 10:00:00 |
-- |  5 | SNK-001 | Sinking Fund      |       1.00 |         1 | 2025-01-15 10:00:00 | 2025-01-15 10:00:00 |
-- |  6 | TRN-001 | Transport         |      14.00 |         1 | 2025-01-15 10:00:00 | 2025-01-15 10:00:00 |
-- |  7 | HSG-001 | Housing           |      14.00 |         1 | 2025-01-15 10:00:00 | 2025-01-15 10:00:00 |
-- +----+---------+-------------------+------------+-----------+---------------------+---------------------+

-- Summary of Deduction Rates:
-- 1. Employee Tax: 30% (deducted from base salary)
-- 2. Pension: 6% (increased from 3% as per January 2025 update)
-- 3. Medical Insurance: 5% (deducted from base salary)
-- 4. Cash Advance: 5% (deducted from base salary)
-- 5. Sinking Fund: 1% (deducted from base salary)
-- 6. Transport Allowance: 14% (added to base salary)
-- 7. Housing Allowance: 14% (added to base salary)
