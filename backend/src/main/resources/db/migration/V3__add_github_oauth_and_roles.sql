-- ============================================================
-- V3: Add GITHUB as OAuth provider + align roles to USER/MAINTAINER/ADMIN
-- Hackaton 2026 · Bysone
-- ============================================================

-- 1. Extend the CHECK constraint on usuarios to allow GITHUB
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS chk_u_proveedor;
ALTER TABLE usuarios
    ADD CONSTRAINT chk_u_proveedor
        CHECK (proveedor_oauth IN ('GOOGLE', 'MICROSOFT', 'GITHUB'));

-- 2. Rename existing roles to match the new naming convention
UPDATE roles_bysone SET nombre_rol = 'ADMIN'      WHERE nombre_rol = 'ADMINISTRADOR';
UPDATE roles_bysone SET nombre_rol = 'MAINTAINER' WHERE nombre_rol = 'ASESOR';
UPDATE roles_bysone SET nombre_rol = 'USER'        WHERE nombre_rol = 'USUARIO';

