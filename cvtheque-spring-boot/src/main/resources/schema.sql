/*
create table if not exists SPRING_AI_VECTOR (
	id        varchar2(36) default sys_guid() primary key,
	content   clob not null,
    folder VARCHAR2(20),
    filepdf VARCHAR2(20),
	metadata  json not null,
	embedding VECTOR annotations(Distance 'COSINE', IndexType 'IVF')
)


-- Création de la table avec le type VECTOR
CREATE TABLE SPRING_AI_VECTORS (
    id        VARCHAR2(36) DEFAULT SYS_GUID() PRIMARY KEY,
    content   CLOB NOT NULL,
    metadata  CLOB NOT NULL, -- Notez que JSON peut être stocké en CLOB dans Oracle
    embedding VECTOR(4096, FLOAT64)
        ANNOTATIONS (Distance 'COSINE', IndexType 'IVF')
);

-- Création de l'index vectoriel
CREATE INDEX vector_index_SPRING_AI_VECTORS
ON SPRING_AI_VECTORS (embedding)
    ORGANIZATION NEIGHBOR PARTITIONS
    DISTANCE COSINE
    WITH TARGET ACCURACY 95
    PARAMETERS (TYPE IVF, NEIGHBOR PARTITIONS 10);


CREATE USER VECTOR_USER IDENTIFIED BY <YOUR_PASSWORD> QUOTA UNLIMITED ON USERS;
GRANT DB_DEVELOPER_ROLE TO VECTOR_USER;
GRANT CREATE SESSION TO VECTOR_USER;

GRANT SELECT ANY TABLE ON SCHEMA VECTOR_USER TO VECTOR_USER;
GRANT SELECT ANY TABLE ON SCHEMA VECTOR_USER TO VECTOR_USER;
GRANT SELECT ANY TABLE ON SCHEMA VECTOR_USER TO VECTOR_USER;
GRANT SELECT ANY TABLE ON SCHEMA VECTOR_USER TO VECTOR_USER;
ALTER SESSION SET CURRENT_SCHEMA = VECTOR_USER;
CREATE TABLE VECTOR_USER.ORACLE_AI_VECTOR_SEARCH_DEMO (ID NUMBER PRIMARY KEY, VECTOR_DATA VECTOR(3, FLOAT64));
COMMIT;


/--------------------------------------------------------------------------------
create table SPRING_AI_VECTORS
(
    ID        VARCHAR2(36) default sys_guid() not null
        primary key,
    CONTENT   CLOB                            not null,
    METADATA  JSON                            not null,
    EMBEDDING VECTOR
);

create table VECTOR$VECTOR_INDEX_SPRING_AI_VECTORS$73117_73125_0$IVF_FLAT_CENTROID_PARTITIONS
(
    BASE_TABLE_ROWID ROWID  not null
        primary key,
    CENTROID_ID      NUMBER not null,
    DATA_VECTOR      VECTOR
);

create table VECTOR$VECTOR_INDEX_SPRING_AI_VECTORS$73117_73125_0$IVF_FLAT_CENTROIDS
(
    CENTROID_ID     NUMBER not null
        primary key,
    CENTROID_VECTOR VECTOR
);
/--------------------------------------------------------------------------------

 */
