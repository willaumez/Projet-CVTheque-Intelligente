import {Component, ViewChild} from '@angular/core';
import {Conversation, Message, Sender} from "../../Models/Conversation";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {AiChatService} from "../../Services/AiServices/ai-chat.service";

@Component({
  selector: 'app-chat-ai',
  templateUrl: './chat-ai.component.html',
  styleUrl: './chat-ai.component.scss'
})

export class ChatAIComponent{
  @ViewChild('autosize') autosize: CdkTextareaAutosize | undefined;

  // Définir la conversation utilisant le modèle
  conversation: Conversation = { messages: [] };
  userMessage: string = '';

  // Méthode pour ajouter un message à la conversation
  addMessage(sender: Sender, message: string) {
    const newMessage: Message = {
      sender: sender,
      message: message,
      timestamp: new Date().toISOString()
    };
    this.conversation.messages.push(newMessage);
  }

  constructor(private aiChatService: AiChatService) {
  }

  // Méthode pour envoyer un message
  sendMessage() {
    console.log("user message", this.userMessage);
    if (this.userMessage.trim()) {
      // Ajouter le message de l'utilisateur à la conversation
      this.addMessage(Sender.USER, this.userMessage);

      // Envoyer le message au backend et obtenir la réponse
      this.aiChatService.askQuestion(this.userMessage).subscribe(
        (response: string) => {
          // Ajouter la réponse de l'IA à la conversation
          this.addMessage(Sender.BOT, response);
        },
        error => console.error('Error sending message', error)
      );
      // Réinitialiser le champ de saisie
      this.userMessage = '';
    }
  }



}
