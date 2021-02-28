import { User } from "./User";

export interface ChatMessage {
    id: number;
    to: User;
    from: User;
    message: string;
    createdDate: string;
    delivered:boolean;
}