--==================================================================
-- 5. INSERTS INICIALES
--==================================================================
-- 1) Habilita la extensión para generar UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO
    public.entities (
        id,
        account_bank,
        authentication_mode,
        code,
        email,
        entity_type,
        name,
        nit
    )
VALUES  
    (
        uuid_generate_v4 (),
        '075540271936',
        TRUE,
        '1240368974',
        'davivienda@gmail.com',
        TRUE,
        'DAVIVIENDA EL SALVADOR',
        '06142711011120'
    ),
    (
        uuid_generate_v4 (),
        '075540271937',
        TRUE,
        '1240368978',
        'aeroman@gmail.com',
        TRUE,
        'AEROMAN S.A. de C.V.',
        '06142711011121'
    ),
    (
        uuid_generate_v4 (),
        '075540271938',
        TRUE,
        '1240368979',
        'tigo@gmail.com',
        TRUE,
        'TIGO S.A. de C.V.',
        '06142711011131'
    );

ALTER TABLE entities ADD CONSTRAINT entities_pkey PRIMARY KEY (id);

INSERT INTO
    public.roles (role_id, role_name, role_description)
VALUES
    (
        uuid_generate_v4 (),
        'MANAGER',
        'Role with full system permissions'
    ),
    (
        uuid_generate_v4 (),
        'SUPPLIER',
        'Role for suppliers to manage their data'
    ),
    (
        uuid_generate_v4 (),
        'AUTHORIZING',
        'Role for authorizing standard operations'
    ),
    (
        uuid_generate_v4 (),
        'AUTHORIZING_TWO_MODE_AUTH',
        'Role for authorizing with two‑mode authentication'
    ),
    (
        uuid_generate_v4 (),
        'SUPPLIER_TWO_MODE_AUTH',
        'Role for suppliers with two‑mode authentication'
    ),
    (
        uuid_generate_v4 (),
        'OPERATOR',
        'Role for system operators with limited permissions'
    );

-- MANAGER
INSERT INTO
    public.users (id, dui, email, name, entity_id, role_role_id)
VALUES
    (
        uuid_generate_v4 (),
        '063062817',
        'mario@ejemplo.com',
        'Mario Antonio Perez Quintanilla',
        'cea5646d-7203-401f-a354-60f639f1a244',
        '28ec8c43-76f2-4f28-b3cc-c2ff7ad87bb9'
    );

-- AUTHORIZING
INSERT INTO
    public.users (id, dui, email, name, entity_id, role_role_id)
VALUES
    (
        uuid_generate_v4 (),
        '063062811',
        'carlos@ejemplo.com',
        'Carlos Alberto Hernández Guerra',
        'cea5646d-7203-401f-a354-60f639f1a244',
        '28ec8c43-76f2-4f28-b3cc-c2ff7ad87bb9'
    );

-- AUTHORIZING_TWO_MODE_AUTH
INSERT INTO
    public.users (id, dui, email, name, entity_id, role_role_id)
VALUES
    (
        uuid_generate_v4 (),
        '063062812',
        'paola@ejemplo.com',
        'Paola Maricela Linares López',
        'cea5646d-7203-401f-a354-60f639f1a244',
        '28ec8c43-76f2-4f28-b3cc-c2ff7ad87bb9'
    );

-- SUPPLIER
INSERT INTO
    public.users (id, dui, email, name, entity_id, role_role_id)
VALUES
    (
        uuid_generate_v4 (),
        '063062813',
        'jose@ejemplo.com',
        'José Walberto Guerra González',
        'cea5646d-7203-401f-a354-60f639f1a244',
        '28ec8c43-76f2-4f28-b3cc-c2ff7ad87bb9'
    );

-- SUPPLIER_TWO_MODE_AUTH
INSERT INTO
    public.users (id, dui, email, name, entity_id, role_role_id)
VALUES
    (
        uuid_generate_v4 (),
        '063062814',
        'roberto@ejemplo.com',
        'Roberto Daniel Cerrato Bulnes',
        'cea5646d-7203-401f-a354-60f639f1a244',
        '28ec8c43-76f2-4f28-b3cc-c2ff7ad87bb9'
    );

-- OPERATOR
INSERT INTO
    public.users (id, dui, email, name, entity_id, role_role_id)
VALUES
    (
        uuid_generate_v4 (),
        '06306285',
        'marvin@ejemplo.com',
        'Marvin Antonio Parada Fuentes',
        'cea5646d-7203-401f-a354-60f639f1a244',
        '28ec8c43-76f2-4f28-b3cc-c2ff7ad87bb9'
    );

-- PERMISOS
INSERT INTO public.permissions (id, permission_name, permission_description)
VALUES
    (uuid_generate_v4(), 'dashboard_view', 'Permission to view the administration dashboard'),
    (uuid_generate_v4(), 'update_parameters', 'Permission to update system parameters'),
    (uuid_generate_v4(), 'delete_documents', 'Permission to delete documents'),
    (uuid_generate_v4(), 'link_user_view', 'Permission to view user management'),
    (uuid_generate_v4(), 'agreement_management_view', 'Permission to view agreement management'),
    (uuid_generate_v4(), 'payer_management_view', 'Permission to view payer management'),
    (uuid_generate_v4(), 'supplier_management_view', 'Permission to view supplier management'),
    (uuid_generate_v4(), 'view_upload_file_view', 'Permission to view the file upload page'),
    (uuid_generate_v4(), 'select_documents_view', 'Permission to view the document selection page'),
    (uuid_generate_v4(), 'documents_selected_view', 'Permission to view the selected documents page'),
    (uuid_generate_v4(), 'approve_documents_view', 'Permission to view the document approval page'),
    (uuid_generate_v4(), 'documents_approved_view', 'Permission to view the approved documents page'),
    -- execution permissions
    (uuid_generate_v4(), 'create_payer_execute', 'Permission to create a payer'),
    (uuid_generate_v4(), 'link_user_execute', 'Permission to link a user to a role'),
    (uuid_generate_v4(), 'upload_documents_execute', 'Permission to upload documents'),
    (uuid_generate_v4(), 'approve_documents_execute', 'Permission to approve documents'),
    (uuid_generate_v4(), 'select_documents_execute', 'Permission to select documents'),
    (uuid_generate_v4(), 'select_agreement_execute', 'Permission to select an agreement'),
    (uuid_generate_v4(), 'select_documents_execute_two_mode_auth', 'Permission to execute documents selection two mode auth');
    -- two‐mode auth permissions
    (uuid_generate_v4(), 'approve_documents_two_mode_auth_execute', 'Permission to execute document approval with two-mode authentication'),
    (uuid_generate_v4(), 'approve_documents_two_mode_auth_view', 'Permission to view document approval page with two-mode authentication')
;

-- ASIGNACIÓN DE PERMISOS A ROLES

INSERT INTO public.role_permissions (role_id, permission_id)
VALUES
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'dashboard_view')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'update_parameters')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'delete_documents')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'link_user_view')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'agreement_management_view')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'payer_management_view')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'view_upload_file_view')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'create_payer_execute')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'link_user_execute')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'MANAGER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'upload_documents_execute')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'SUPPLIER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'select_documents_view')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'SUPPLIER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'select_documents_execute')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'SUPPLIER_TWO_MODE_AUTH'),
        (SELECT id FROM public.permissions WHERE permission_name = 'select_documents_execute_two_mode_auth')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'SUPPLIER'),
        (SELECT id FROM public.permissions WHERE permission_name = 'select_agreement_execute')
    ), -- Agregar permisos para SUPPLIER_TWO_MODE_AUTH Y agregar permiso para borrar documentos
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'AUTHORIZING'),
        (SELECT id FROM public.permissions WHERE permission_name = 'approve_documents_view')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'AUTHORIZING'),
        (SELECT id FROM public.permissions WHERE permission_name = 'approve_documents_execute')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'AUTHORIZING_TWO_MODE_AUTH'),
        (SELECT id FROM public.permissions WHERE permission_name = 'approve_documents_two_mode_auth_execute')
    ),
    (
        (SELECT role_id FROM public.roles WHERE role_name = 'AUTHORIZING_TWO_MODE_AUTH'),
        (SELECT id FROM public.permissions WHERE permission_name = 'approve_documents_two_mode_auth_view')
    );   





ALTER TABLE public.entities
ADD COLUMN district varchar(255);

ALTER TABLE public.entities
ADD COLUMN municipality varchar(255);

ALTER TABLE public.entities
ADD COLUMN department varchar(255);

 select * from public.documents d 
 where d.status = 'APPROVED'