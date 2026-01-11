-- ============================================
-- Base de données : Mondial 2030 Billetterie
-- ============================================

CREATE DATABASE IF NOT EXISTS mondial2030_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mondial2030_db;

-- ============================================
-- Table : roles
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : match_events
-- ============================================
CREATE TABLE IF NOT EXISTS match_events (
    id INT PRIMARY KEY AUTO_INCREMENT,
    match_name VARCHAR(255) NOT NULL,
    match_date DATETIME NOT NULL,
    venue VARCHAR(255) NOT NULL,
    team_a VARCHAR(100) NOT NULL,
    team_b VARCHAR(100) NOT NULL,
    total_capacity INT NOT NULL,
    available_tickets INT NOT NULL,
    ticket_price DECIMAL(10, 2) NOT NULL,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_match_date (match_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : tickets
-- ============================================
CREATE TABLE IF NOT EXISTS tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_code VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    match_event_id INT NOT NULL,
    seat_number VARCHAR(20),
    qr_code_data TEXT,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('VALID', 'USED', 'CANCELLED', 'EXPIRED') DEFAULT 'VALID',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (match_event_id) REFERENCES match_events(id) ON DELETE RESTRICT,
    INDEX idx_ticket_code (ticket_code),
    INDEX idx_user_id (user_id),
    INDEX idx_match_event_id (match_event_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : biometric_data
-- ============================================
CREATE TABLE IF NOT EXISTS biometric_data (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    has_face_data BOOLEAN DEFAULT FALSE,
    has_fingerprint_data BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : face_embeddings
-- ============================================
CREATE TABLE IF NOT EXISTS face_embeddings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    biometric_data_id INT NOT NULL,
    embedding_data BLOB NOT NULL,
    image_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (biometric_data_id) REFERENCES biometric_data(id) ON DELETE CASCADE,
    INDEX idx_biometric_data_id (biometric_data_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : fingerprint_data
-- ============================================
CREATE TABLE IF NOT EXISTS fingerprint_data (
    id INT PRIMARY KEY AUTO_INCREMENT,
    biometric_data_id INT NOT NULL,
    fingerprint_template BLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (biometric_data_id) REFERENCES biometric_data(id) ON DELETE CASCADE,
    INDEX idx_biometric_data_id (biometric_data_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : accreditations
-- ============================================
CREATE TABLE IF NOT EXISTS accreditations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    accreditation_type ENUM('MEDIA', 'STAFF', 'VIP', 'SECURITY') NOT NULL,
    valid_from DATE NOT NULL,
    valid_until DATE NOT NULL,
    access_levels TEXT,
    status ENUM('ACTIVE', 'EXPIRED', 'REVOKED') DEFAULT 'ACTIVE',
    issued_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (issued_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_valid_dates (valid_from, valid_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : gate_devices
-- ============================================
CREATE TABLE IF NOT EXISTS gate_devices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    device_name VARCHAR(100) NOT NULL UNIQUE,
    device_location VARCHAR(255) NOT NULL,
    device_type ENUM('ENTRANCE', 'EXIT', 'VIP') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_sync TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_device_name (device_name),
    INDEX idx_location (device_location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table : access_logs
-- ============================================
CREATE TABLE IF NOT EXISTS access_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    ticket_id INT,
    gate_device_id INT,
    access_type ENUM('TICKET', 'ACCREDITATION', 'BIOMETRIC') NOT NULL,
    access_result ENUM('GRANTED', 'DENIED') NOT NULL,
    denial_reason TEXT,
    access_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE SET NULL,
    FOREIGN KEY (gate_device_id) REFERENCES gate_devices(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_gate_device_id (gate_device_id),
    INDEX idx_access_timestamp (access_timestamp),
    INDEX idx_access_result (access_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Données initiales
-- ============================================

-- Insertion des rôles
INSERT INTO roles (name, description) VALUES
('STAFF', 'Personnel autorisé pour le contrôle d''accès, gestion des tickets et annonces'),
('SUPPORTER', 'Supporteur avec accès aux matchs, génération QR Codes et biométrie');

-- Note: Les utilisateurs sont créés via seed_data.sql
-- Pas d'utilisateur admin par défaut - utilisez staff1 pour les tests

-- Exemple de match
INSERT INTO match_events (match_name, match_date, venue, team_a, team_b, total_capacity, available_tickets, ticket_price) VALUES
('Match d''ouverture', '2030-06-10 20:00:00', 'Stade Mohammed V', 'Maroc', 'Espagne', 50000, 45000, 500.00);

-- Exemple de gate device
INSERT INTO gate_devices (device_name, device_location, device_type) VALUES
('Porte Principale A', 'Entrée Nord', 'ENTRANCE'),
('Porte VIP', 'Entrée Est', 'VIP'),
('Porte Sortie', 'Sortie Sud', 'EXIT');
