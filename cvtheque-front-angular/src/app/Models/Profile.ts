export interface Profile {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
  listCriteria?: Criteria[];
}

export interface Criteria{
  name: string;
  message: string;
}
