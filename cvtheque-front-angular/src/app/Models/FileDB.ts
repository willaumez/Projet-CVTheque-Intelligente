// Purpose: Interface for FileDB object.
export interface FileDB {
  id: number;
  name: string;
  type: string;
  createdAt: Date;
  folderName: string;
  folderId: number;
  acceptCriteria?: Criteria[];
  rejectCriteria?: Criteria[];
}


export interface Folder {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}


interface Criteria{
  name: string;
  message: string;
}

export interface ResultCriteria{
  acceptCriteria?: Criteria[];
  rejectCriteria?: Criteria[];
}
