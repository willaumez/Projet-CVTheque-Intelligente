// Purpose: Interface for FileDB object.
export interface FileDB {
  id: number;
  name: string;
  type: string;
  createdAt: Date;
  folderName: string;
  folderId: number;
}


export interface Folder {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}

