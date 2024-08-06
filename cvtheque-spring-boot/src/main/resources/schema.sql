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
