//Evaluation
export interface EvaluationDto {
  id: number;
  createdAt: Date;
  acceptCriteria: CriteriaEval[];
  rejectCriteria: CriteriaEval[];
  existWords: string[];
  noExistWords: string[];
  scoring: ScoringDto[];
}
export interface CriteriaEval {
  id: number;
  createdAt: Date;
  status: Status;
  name: string;
  message: string;
}
export interface ScoringDto {
  profile: string;
  score: number;
  message: string;
  createdAt: Date;
}
export enum Status {
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
}
