-- ============================================================
-- V3: Alinear nombres de roles a convención USER / MAINTAINER / ADMIN
-- Hackaton 2026 · Bysone
-- ============================================================

UPDATE roles_bysone SET nombre_rol = 'ADMIN'      WHERE nombre_rol = 'ADMINISTRADOR';
UPDATE roles_bysone SET nombre_rol = 'MAINTAINER' WHERE nombre_rol = 'ASESOR';
UPDATE roles_bysone SET nombre_rol = 'USER'        WHERE nombre_rol = 'USUARIO';
