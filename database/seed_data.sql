-- ============================================
-- Données de test pour Mondial 2030
-- ============================================

USE mondial2030_db;

-- Utilisateurs de test (mots de passe: password123)
-- Note: role_id 1 = STAFF, role_id 2 = SUPPORTER
-- IMPORTANT: Les hashs BCrypt sont uniques à chaque génération.
-- Si ces hashs ne fonctionnent pas, exécutez: mvn exec:java -Dexec.mainClass="ma.mondial2030.util.FixAllPasswords"
-- Utilisation de INSERT IGNORE pour éviter les erreurs si les utilisateurs existent déjà
INSERT IGNORE INTO users (username, email, password_hash, first_name, last_name, phone, role_id) VALUES
('staff1', 'staff1@mondial2030.ma', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Ahmed', 'Alaoui', '+212612345678', 1),
('supporter1', 'supporter1@mondial2030.ma', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Fatima', 'Benali', '+212698765432', 2),
('supporter2', 'supporter2@mondial2030.ma', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mehdi', 'Tazi', '+212612345679', 2);

-- Matchs supplémentaires
INSERT INTO match_events (match_name, match_date, venue, team_a, team_b, total_capacity, available_tickets, ticket_price) VALUES
('Quart de finale', '2030-06-20 18:00:00', 'Stade Ibn Battouta', 'Maroc', 'Brésil', 45000, 40000, 800.00),
('Demi-finale', '2030-06-28 20:00:00', 'Stade Mohammed V', 'Maroc', 'France', 50000, 30000, 1200.00),
('Finale', '2030-07-05 20:00:00', 'Stade Mohammed V', 'TBD', 'TBD', 50000, 50000, 2000.00);

-- Tickets de test
-- Note: Les user_id sont déterminés dynamiquement en fonction des utilisateurs créés
-- staff1, supporter1, supporter2 doivent exister avant d'exécuter cette requête
-- Utilisation de INSERT IGNORE pour éviter les erreurs si les tickets existent déjà
INSERT IGNORE INTO tickets (ticket_code, user_id, match_event_id, seat_number, status) 
SELECT 'TKT-2024-001', u.id, 1, 'A-101', 'VALID'
FROM users u WHERE u.username = 'staff1'
UNION ALL
SELECT 'TKT-2024-002', u.id, 1, 'A-102', 'VALID'
FROM users u WHERE u.username = 'supporter1'
UNION ALL
SELECT 'TKT-2024-003', u.id, 2, 'VIP-50', 'VALID'
FROM users u WHERE u.username = 'supporter2';

-- Accréditations de test
-- Note: Les user_id sont déterminés dynamiquement
-- Utilisation de INSERT IGNORE pour éviter les erreurs si l'accréditation existe déjà
INSERT IGNORE INTO accreditations (user_id, accreditation_type, valid_from, valid_until, access_levels, status, issued_by)
SELECT u.id, 'VIP', '2030-06-01', '2030-07-31', 'VIP_LOUNGE,ALL_MATCHES', 'ACTIVE', 
       (SELECT id FROM users WHERE username = 'staff1' LIMIT 1)
FROM users u WHERE u.username = 'supporter1';
