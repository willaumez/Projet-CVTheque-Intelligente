// Définir l'énumération pour les types de sender
export enum Sender {
  USER = 'USER',
  BOT = 'BOT'
}

// Définir l'interface Message utilisant l'énumération Sender
export interface Message {
  sender: Sender;
  message: string;
  timestamp: string;
}

// Définir l'interface Conversation
export interface Conversation {
  messages: Message[];
}
