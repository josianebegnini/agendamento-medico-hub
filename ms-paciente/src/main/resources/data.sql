INSERT INTO convenio (nome, cobertura, telefone_contato) VALUES ('Particular', 'Completa', '11955555555');
INSERT INTO convenio (nome, cobertura, telefone_contato) VALUES ('Bradesco', 'Nacional', '11988888888');
INSERT INTO convenio (nome, cobertura, telefone_contato) VALUES ('Unimed', 'Completa', '11988888888');

INSERT INTO paciente (nome, email, telefone, data_nascimento, convenio_id)
VALUES
('João Silva','joao@example.com','11999998888','1988-03-22',1),
('Ana Oliveira','ana@example.com','21977776666','1995-07-15',2),
('Carlos Pereira','carlos@example.com','31966665555','1980-11-30',3),
('Fernanda Lima','fernanda@example.com','21955554444','1992-01-18',1),
('Bruno Santos','bruno@example.com','41944443333','1998-06-05',2),
('Patrícia Mendes','patricia@example.com','31933332222','1985-09-25',3),
('Lucas Almeida','lucas@example.com','11922221111','1993-12-12',1),
('Juliana Costa','juliana@example.com','31911110000','1997-04-08',2),
('Rodrigo Martins','rodrigo@example.com','41900009999','1989-08-21',3);
