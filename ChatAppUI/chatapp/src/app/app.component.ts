import { Component, OnDestroy, OnInit } from "@angular/core";
import { MessageService, PrimeNGConfig } from "primeng/api";
import {
  Encodable,
  IdentitySerializer,
  JsonSerializer,
  RSocketClient,
} from "rsocket-core";
import RSocketWebSocketClient from "rsocket-websocket-client";
import { Subject } from "rxjs";
import { UserService } from "src/service/User.service";
import { ChatMessage } from "./ChatMessage";
import { User } from "./User";

@Component({
  styles: [
    `
      .box {
        background-color: var(--surface-e);
        text-align: center;
        padding-top: 1rem;
        padding-bottom: 1rem;
        border-radius: 4px;
        box-shadow: 0 2px 1px -1px rgba(0, 0, 0, 0.2),
          0 1px 1px 0 rgba(0, 0, 0, 0.14), 0 1px 3px 0 rgba(0, 0, 0, 0.12);
      }

      .box-stretched {
        height: 100%;
      }

      .vertical-container {
        margin: 0;
        height: 200px;
        background: var(--surface-d);
        border-radius: 4px;
      }

      .nested-grid .p-col-4 {
        padding-bottom: 1rem;
      }
    `,
  ],
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent implements OnInit, OnDestroy {
  title = "chatapp";
  users!: User[];
  chatMessages: ChatMessage[] = [];
  selectedUser: User | undefined;
  me!: string;
  meUser!: User;
  inputMessage!: string;
  client!: RSocketClient<any, Encodable>;
  fireAndForgetClient!: RSocketClient<any, Encodable>;
  sub = new Subject();
  lastMessageId = 0;

  constructor(
    private primengConfig: PrimeNGConfig,
    private userService: UserService,
    private messageService: MessageService
  ) {
    // Create an instance of a client
    this.initRSocketClient();
  }

  ngOnInit() {
    this.primengConfig.ripple = true;
    this.userService.getUsers().subscribe((usersRes: any) => {
      console.log(usersRes);
      this.users = usersRes;
    });
  }

  private initRSocketClient() {
    this.client = new RSocketClient({
      serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer,
      },
      setup: {
        // ms btw sending keepalive to server
        keepAlive: 60000,
        // ms timeout if no keepalive response
        lifetime: 180000,
        // format of `data`
        dataMimeType: "application/json",
        // format of `metadata`
        metadataMimeType: "message/x.rsocket.routing.v0",
      },
      transport: new RSocketWebSocketClient({
        url: "ws://localhost:8181/rsocketChat",
      }),
    });
  }

  onSelectedUser() {
    console.log(this.selectedUser);
  }
  onSend() {
    if (this.selectedUser && this.me && this.inputMessage && this.meUser) {
      let chatMessage = {
        from: this.meUser,
        to: this.selectedUser,
        message: this.inputMessage,
        delivered: false,
      };

      if (this.fireAndForgetClient) {
        this.fireAndForgetClient.close();
      }

      this.fireAndForgetClient = new RSocketClient({
        serializers: {
          data: JsonSerializer,
          metadata: IdentitySerializer,
        },
        setup: {
          // ms btw sending keepalive to server
          keepAlive: 60000,
          // ms timeout if no keepalive response
          lifetime: 180000,
          // format of `data`
          dataMimeType: "application/json",
          // format of `metadata`
          metadataMimeType: "message/x.rsocket.routing.v0",
        },
        transport: new RSocketWebSocketClient({
          url: "ws://localhost:8181/rsocketChat",
        }),
      });

      this.fireAndForgetClient.connect().subscribe({
        onComplete: (socket) => {
          socket.fireAndForget({
            data: chatMessage,
            metadata:
              String.fromCharCode("sendSubscribeChatMessage".length) +
              "sendSubscribeChatMessage",
          });
        },
        onError: (error) => {
          console.log("error" + error);
        },
        onSubscribe: (cancel) => {
          /* call cancel() to abort */
        },
      });
      this.inputMessage = "";
    }
  }
  onStart() {
    if (this.me) {
      for (let user of this.users) {
        if (user.userName === this.me) {
          this.meUser = user;
          break;
        }
      }
      if (this.meUser) {
        this.client.connect().subscribe({
          onComplete: (socket) => {
            socket
              .requestStream({
                data: { userName: this.me },
                metadata:
                  String.fromCharCode("subscribeChatMessage".length) +
                  "subscribeChatMessage",
              })
              .subscribe({
                onComplete: () => console.log("requestStream done"),
                onError: (error) => {
                  console.log("got error with requestStream");
                  console.error(error);
                },
                onNext: (payload) => {
                  // console.log("got next value in requestStream..");
                  console.log(payload.data);
                  if (this.lastMessageId < payload.data.id) {
                    this.chatMessages.push(payload.data);
                    if (
                      !this.selectedUser ||
                      payload.data.from.userName !== this.selectedUser.userName
                    ) {
                      this.messageService.add({
                        severity: "success",
                        summary: payload.data.from.userName,
                        detail: payload.data.message,
                      });
                    }
                  }

                  if (this.lastMessageId < payload.data.id) {
                    this.lastMessageId = payload.data.id;
                  }
                },
                // Nothing happens until `request(n)` is called
                onSubscribe: (sub) => {
                  console.log("subscribe request Stream!");
                  sub.request(2147483647);
                },
              });
          },
          onError: (error) => {
            console.log("error" + error);
          },
          onSubscribe: (cancel) => {
            /* call cancel() to abort */
          },
        });
      }
    }
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
    if (this.client) {
      this.client.close();
    }
  }
}
