// Purpose: Interface for FileDB object.
export interface FileDB {
  id: number;
  name: string;
  type: string;
  data: Blob;
  createdAt: Date;
}

