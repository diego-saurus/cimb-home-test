INSERT INTO cs_agents (id, cs_name) VALUES
  (1, 'Ahmad Wijaya'),
  (2, 'Citra Lestari'),
  (3, 'Eka Pratama');

INSERT INTO customers (id, customer_name) VALUES
  (1, 'Budi Santoso'),
  (2, 'Dewi Anggraini'),
  (3, 'Fajar Nugroho');

INSERT INTO call_monitorings (call_id, call_timestamp, cs_agent_id, customer_id, sentiment_score) VALUES
  ('CM-0001', TIMESTAMP '2025-05-01 09:30:00', 1, 1, 82.00),
  ('CM-0002', TIMESTAMP '2025-05-02 10:15:00', 1, 2, 55.00),
  ('CM-0003', TIMESTAMP '2025-05-12 11:00:00', 2, 1, 91.00),
  ('CM-0004', TIMESTAMP '2025-04-22 13:45:00', 2, 3, 48.00),
  ('CM-0005', TIMESTAMP '2025-05-18 14:20:00', 3, 2, 73.00),
  ('CM-0006', TIMESTAMP '2025-06-05 08:50:00', 1, 3, 64.00),
  ('CM-0007', TIMESTAMP '2025-06-15 16:10:00', 2, 1, 88.00),
  ('CM-0008', TIMESTAMP '2025-07-08 09:00:00', 3, 2, 69.00),
  ('CM-0009', TIMESTAMP '2025-07-12 10:30:00', 1, 3, 95.00),
  ('CM-0010', TIMESTAMP '2025-07-18 15:00:00', 3, 1, 59.00);
