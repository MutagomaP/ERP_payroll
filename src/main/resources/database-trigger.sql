-- Database Trigger for Payroll Message Generation for PostgreSQL
-- This trigger automatically generates a message when payroll status changes from PENDING to PAID

CREATE OR REPLACE FUNCTION generate_payroll_message()
RETURNS TRIGGER AS $$
BEGIN
    -- Only execute if status changed from PENDING to PAID
    IF NEW.status = 'PAID' AND OLD.status = 'PENDING' THEN
        -- Insert message into payroll_message table
        INSERT INTO payroll_message (
            payroll_deduction_id,
            employee_email,
            message_content,
            sent_status,
            created_at
        )
        SELECT 
            NEW.id,
            e.email,
            'Dear ' || e.first_name || 
            ', Your salary of ' || NEW.month || '/' || NEW.year || 
            ' from Rwanda Government RWF ' || NEW.net_salary || 
            ' has been credited to your ' || emp.code || 
            ' account successfully.',
            'PENDING',
            NOW()
        FROM employment emp
        INNER JOIN employee e ON emp.employee_id = e.id
        WHERE emp.id = NEW.employment_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_payroll_approved
AFTER UPDATE ON payroll_deduction
FOR EACH ROW
EXECUTE FUNCTION generate_payroll_message();

-- Note: This trigger is implemented in the PayrollService.approvePayroll() method
-- as Spring Boot JPA doesn't directly support database triggers easily via Hibernate.
-- The message generation logic is often handled at the application level.
