// Purpose: Interface for FileDB object.
export interface FileDB {
  id: number;
  name: string;
  type: string;
  createdAt: Date;
  folderName: string;
  folderId: number;
  evaluation: boolean;
  pscoring: number;
  pkeywords: number;
  pcriteria: number;

  acceptCriteria?: Criteria[];
  rejectCriteria?: Criteria[];
  existWords?: string[];
  noExistWords?: string[];
  scoring?: Scoring;
}

export interface Folder {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
  fileCount?: number;
}
interface Criteria{
  name: string;
  message: string;
}
export interface ResultCriteria{
  acceptCriteria?: Criteria[];
  rejectCriteria?: Criteria[];
}
export interface ResultKeywords{
  existWords?: string[];
  noExistWords?: string[];
}
export interface Scoring{
  profile: string;
  score: number;
  message: string;
}



//Stats
export interface CVStatsDTO{
  graphData: GraphData[];
  headerData: HeaderData;
}
export interface GraphData {
  folderName: string;
  month: number;
  year: number;
  count: number;
}
export interface HeaderData{
  totalFiles: number;
  totalFolders: number;
  totalProfiles: number;
  totalEvaluated: number;
  totalNotEvaluated: number;
}

