-- =================================================================
-- 1. TABLAS PRINCIPALES
-- =================================================================
-- Tabla para 'EntityModel'  
-- Entidades como pagadores o proveedores  
CREATE TABLE
    entities (
        id UUID PRIMARY KEY,
        code VARCHAR(255),
        name VARCHAR(255),
        nit VARCHAR(255),
        account_bank VARCHAR(255),
        email VARCHAR(255),
        authentication_mode BOOLEAN, -- 0 = Convencional, 1 = Directo
        entity_type BOOLEAN
    );

-- Tabla para 'PermissionModel'  
-- Permisos granulares del sistema  
CREATE TABLE
    permissions (
        permission_id UUID PRIMARY KEY,
        permission_name VARCHAR(255) NOT NULL UNIQUE,
        permission_description VARCHAR(255) NOT NULL
    );

-- Tabla para 'RoleModel'  
-- Roles que agrupan permisos  
CREATE TABLE
    roles (
        role_id UUID PRIMARY KEY,
        role_name VARCHAR(255) NOT NULL UNIQUE,
        role_description VARCHAR(255) NOT NULL
    );

-- Tabla para 'ParameterModel'  
-- Parámetros de configuración de la aplicación  
CREATE TABLE
    app_parameters (
        id UUID PRIMARY KEY,
        param_key VARCHAR(255) NOT NULL UNIQUE,
        param_value VARCHAR(255) NOT NULL
    );

-- =================================================================
-- 2. TABLAS DEPENDIENTES
-- =================================================================
-- Tabla para 'AgreementModel'  
-- Convenios entre pagadores y proveedores  
CREATE TABLE
    agreements (
        agreement_id UUID PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        identifier VARCHAR(255) NOT NULL,
        payer_id UUID NOT NULL, -- FK a entities(id)
        supplier_id UUID NOT NULL -- FK a entities(id)
    );

-- Tabla para 'UserModel'  
-- Usuarios del sistema  
CREATE TABLE
    users (
        id UUID PRIMARY KEY,
        email VARCHAR(255) NOT NULL UNIQUE,
        dui VARCHAR(255) NOT NULL,
        name VARCHAR(255) NOT NULL,
        entity_id UUID NOT NULL, -- FK a entities(id)
        role_id UUID NOT NULL -- FK a roles(role_id)
    );

-- Tabla para 'DocumentModel'  
-- Documentos transaccionados  
CREATE TABLE
    documents (
        document_id UUID PRIMARY KEY,
        document_number VARCHAR(20) NOT NULL UNIQUE,
        amount DOUBLE PRECISION NOT NULL,
        issue_date VARCHAR(255) NOT NULL,
        status VARCHAR(255) NOT NULL,
        status_update_date VARCHAR(255) NOT NULL,
        disbursement_date VARCHAR(255),
        proveedor_id UUID, -- FK a entities(id)
        acuerdo_id UUID NOT NULL, -- FK a agreements(agreement_id)
        cargado_por_id UUID, -- FK a users(id)
        seleccionado_por_id UUID, -- FK a users(id)
        aprobado_por_id UUID, -- FK a users(id)
        rechazado_por_id UUID -- FK a users(id)
    );

-- Tabla intermedia para Many-to-Many entre 'roles' y 'permissions'  
CREATE TABLE
    role_permissions (
        role_id UUID NOT NULL,
        permission_id UUID NOT NULL,
        PRIMARY KEY (role_id, permission_id)
    );


-- ========= CREACIÓN DE LA TABLA DE LOGS =========

CREATE TABLE document_logs (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL REFERENCES documents(document_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL, -- Si se borra el usuario, el log no se pierde
    accion VARCHAR(50) NOT NULL,
    fecha_hora_accion TIMESTAMPTZ NOT NULL
);

-- =================================================================
-- 3. CLAVES FORÁNEAS
-- =================================================================
-- FK en agreements  
ALTER TABLE agreements ADD CONSTRAINT fk_agreements_payer FOREIGN KEY (payer_id) REFERENCES entities (id),
ADD CONSTRAINT fk_agreements_supplier FOREIGN KEY (supplier_id) REFERENCES entities (id);

-- FK en users  
ALTER TABLE users ADD CONSTRAINT fk_users_entity FOREIGN KEY (entity_id) REFERENCES entities (id),
ADD CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (role_id);

-- FK en documents  
ALTER TABLE documents ADD CONSTRAINT fk_documents_supplier FOREIGN KEY (proveedor_id) REFERENCES entities (id),
ADD CONSTRAINT fk_documents_agreement FOREIGN KEY (acuerdo_id) REFERENCES agreements (agreement_id) ON DELETE CASCADE,
ADD CONSTRAINT fk_documents_uploaded_by FOREIGN KEY (cargado_por_id) REFERENCES users (id),
ADD CONSTRAINT fk_documents_selected_by FOREIGN KEY (seleccionado_por_id) REFERENCES users (id),
ADD CONSTRAINT fk_documents_approved_by FOREIGN KEY (aprobado_por_id) REFERENCES users (id),
ADD CONSTRAINT fk_documents_rejected_by FOREIGN KEY (rechazado_por_id) REFERENCES users (id);

-- FK en role_permissions  
ALTER TABLE role_permissions ADD CONSTRAINT fk_roleperm_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
ADD CONSTRAINT fk_roleperm_permission FOREIGN KEY (permission_id) REFERENCES permissions (permission_id) ON DELETE CASCADE;


-- =================================================================
-- DROPS
-- =================================================================

DROP TABLE users CASCADE;
DROP TABLE permissions CASCADE
DROP TABLE roles CASCADE;
DROP TABLE rol_permiso CASCADE;
DROP TABLE usuario_rol CASCADE;
DROP TABLE agreements CASCADE;
DROP TABLE documents CASCADE;

