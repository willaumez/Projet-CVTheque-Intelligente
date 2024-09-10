export interface Profile {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
  listCriteria?: CriteriaDB[] | null;
}

export interface CriteriaDB {
  id : number;
  description : string;
  createdAt : Date;
}

export interface Criteria{
  name: string;
  message: string;
}
