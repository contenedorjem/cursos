-- Primero eliminamos hijos (alumno), luego padres (curso), y usuarios
DELETE FROM alumno;
DELETE FROM curso;
DELETE FROM usuario_aplicacion;

-- Reiniciar autoincrementos
ALTER TABLE alumno ALTER COLUMN id RESTART WITH 1;
ALTER TABLE curso ALTER COLUMN id RESTART WITH 1;
ALTER TABLE usuario_aplicacion ALTER COLUMN id RESTART WITH 1;

-- ============ CURSOS ============
INSERT INTO curso (nombre, descripcion, fecha_inicio, fecha_fin) VALUES
('Java Básico',               'Fundamentos de Java',                             '2025-09-01', '2025-10-31'),
('Spring Boot 3',             'REST + Seguridad + JPA',                          '2025-11-01', '2025-12-15'),
('Python para Datos',         'Pandas, NumPy y visualización',                   '2025-10-15', '2025-11-30'),
('JavaScript Avanzado',       'ES2023+, asincronía y patrones',                  '2025-09-15', '2025-11-15'),
('DevOps Essentials',         'CI/CD, Docker y monitorización',                  '2025-10-01', '2025-10-31'),
('Microservicios en Java',    'Arquitectura, resiliencia y observabilidad',      '2025-12-01', '2026-01-31');

-- ============ ALUMNOS ============
INSERT INTO alumno (nombre, apellidos, email, fecha_nacimiento, telefono, curso_id) VALUES
('Ana',      'López',        'ana@example.com',          '1995-05-10', '600111222', 1),
('Carlos',   'Pérez',        'carlos.perez@example.com', '1992-08-21', '600111223', 1),
('María',    'García',       'maria.garcia@example.com', '1998-01-12', '600111224', 1),
('David',    'Santos',       'david.santos@example.com', '1990-11-03', '600111225', 1);

INSERT INTO alumno (nombre, apellidos, email, fecha_nacimiento, telefono, curso_id) VALUES
('Lucía',    'Martín',       'lucia.martin@example.com', '1997-02-18', '600222111', 2),
('Jorge',    'Ramírez',      'jorge.ramirez@example.com','1993-07-09', '600222112', 2),
('Elena',    'Serrano',      'elena.serrano@example.com','1996-03-27', '600222113', 2),
('Iván',     'Nadal',        'ivan.nadal@example.com',   '1991-12-30', '600222114', 2);

INSERT INTO alumno (nombre, apellidos, email, fecha_nacimiento, telefono, curso_id) VALUES
('Sofía',    'Vega',         'sofia.vega@example.com',   '1999-04-05', '600333111', 3),
('Raúl',     'Iglesias',     'raul.iglesias@example.com','1994-06-14', '600333112', 3),
('Nerea',    'Campos',       'nerea.campos@example.com', '1996-10-08', '600333113', 3),
('Pablo',    'Mora',         'pablo.mora@example.com',   '1993-01-23', '600333114', 3),
('Clara',    'Ruiz',         'clara.ruiz@example.com',   '1998-09-11', '600333115', 3);

INSERT INTO alumno (nombre, apellidos, email, fecha_nacimiento, telefono, curso_id) VALUES
('Adrián',   'Ríos',         'adrian.rios@example.com',  '1997-07-17', '600444111', 4),
('Noa',      'Gil',          'noa.gil@example.com',      '2000-02-02', '600444112', 4),
('Ismael',   'Cano',         'ismael.cano@example.com',  '1995-05-29', '600444113', 4);

INSERT INTO alumno (nombre, apellidos, email, fecha_nacimiento, telefono, curso_id) VALUES
('Hugo',     'Navarro',      'hugo.navarro@example.com', '1992-10-20', '600555111', 5),
('Julia',    'Soler',        'julia.soler@example.com',  '1994-03-06', '600555112', 5),
('Rocío',    'Benítez',      'rocio.benitez@example.com','1996-12-01', '600555113', 5),
('Aitor',    'León',         'aitor.leon@example.com',   '1991-08-18', '600555114', 5);

INSERT INTO alumno (nombre, apellidos, email, fecha_nacimiento, telefono, curso_id) VALUES
('Marta',    'Rey',          'marta.rey@example.com',    '1993-09-09', '600666111', 6),
('Óscar',    'Bravo',        'oscar.bravo@example.com',  '1990-04-28', '600666112', 6),
('Teresa',   'Lara',         'teresa.lara@example.com',  '1997-11-15', '600666113', 6),
('Jaime',    'Durán',        'jaime.duran@example.com',  '1995-01-31', '600666114', 6),
('Paula',    'Ortega',       'paula.ortega@example.com', '1998-06-22', '600666115', 6);

-- ============ USUARIOS ============
INSERT INTO usuario_aplicacion (username, password, rol, enabled) VALUES
('admin', '{noop}admin123', 'ROLE_ADMIN', TRUE),
('user',  '{noop}user123',  'ROLE_USER',  TRUE);
