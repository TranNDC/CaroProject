package ktpm.project.controller.socketio;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import ktpm.project.config.SocketConfig;
import ktpm.project.controller.http.UserController;
import ktpm.project.dto.*;
import ktpm.project.service.SocketService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
public class SocketController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);
    private  SocketIOServer socketServer;
    @Autowired
    private SocketService socketService;

    private Map<UUID,String> sessions = new HashMap<>();
    SocketConfig socketConfig = new SocketConfig();
    public SocketController(){
        socketServer = socketConfig.getSocketServer();
    }
    public void StartSocket(){
        addConnect();
        addDisconnect();
        addSendMessage();
        addSendMove();
        addJoin();
        addCreate();
        addStart();
        addExit();
        addContinue();
        socketServer.start();
    }

    private void addCreate() {
        socketServer.addEventListener("create", CreateRoomForm.class, new DataListener<CreateRoomForm>() {
            @Override
            public void onData(SocketIOClient socketIOClient, CreateRoomForm createRoomForm, AckRequest ackRequest) throws Exception {
                JoinResDTO joinResDTO = socketService.HandleCreateRoom(createRoomForm);
                if (joinResDTO.getCode()==200){
                    socketIOClient.joinRoom(getRoomName(joinResDTO.getRoom().getId()));
                    logger.warn("JOIN ROOM: ",socketIOClient.getSessionId().toString(),getRoomName(joinResDTO.getRoom().getId()));
                    sessions.put(socketIOClient.getSessionId(),createRoomForm.getUsername());
                }
                socketIOClient.sendEvent("listen-create",joinResDTO);
            }
        });
    }

    private void addJoin() {
        socketServer.addEventListener("join", JoinFormDTO.class, new DataListener<JoinFormDTO>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JoinFormDTO joinFormDTO, AckRequest ackRequest) throws Exception {
                JoinResDTO joinResDTO = socketService.HandleJoinRoom(joinFormDTO);
                socketIOClient.sendEvent("listen-join",joinResDTO);
                if(joinResDTO.getCode()==200){
                    socketIOClient.joinRoom(getRoomName(joinFormDTO.getRoomId()));
                    socketServer.getRoomOperations(getRoomName(joinFormDTO.getRoomId())).sendEvent("listen-guest-join",socketIOClient,joinResDTO.getRoom());
                }
            }
        });
    }

    private void handleExit(SocketIOClient client, String room, String username){
        String roomID = getRoomID(room);
        RoomDTO roomDTO = socketService.HandleExit(roomID,username);
        socketServer.getRoomOperations(room).sendEvent("listen-opponent-out-room",client,roomDTO);
    }

    private void addDisconnect() {
        socketServer.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                Set<String> rooms = socketIOClient.getAllRooms();
                for (String room:rooms){
                    String username = sessions.get(socketIOClient.getSessionId());
                    handleExit(socketIOClient,room,username);
                }
                logger.info("DISCONNECT",socketIOClient.toString());
            }
        });
    }

    private void addExit() {
        socketServer.addEventListener("exit", SocketDTO.class, new DataListener<SocketDTO>() {
            @Override
            public void onData(SocketIOClient socketIOClient, SocketDTO socketDTO, AckRequest ackRequest) throws Exception {
                handleExit(socketIOClient,getRoomName(socketDTO.getRoomId()),socketDTO.getUsername());
            }
        });
    }

    private String getRoomID(String roomName) {
        return roomName.substring(1);
    }

    private void sendRoomsDTO(SocketIOClient client){
        RoomsDTO roomsDTO = socketService.getRooms();
        client.sendEvent("listen-inteval-room",roomsDTO);
    }

    private void sendRankingDTO(SocketIOClient client){
        RankingDTO rankingDTO = socketService.getRank();
        client.sendEvent("listen-inteval-rank",rankingDTO);
    }


    private void addConnect() {
        socketServer.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                logger.info("CONNECT",socketIOClient.getSessionId());
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
                executor.scheduleAtFixedRate(() -> sendRoomsDTO(socketIOClient), 10, 15, TimeUnit.SECONDS);
                executor.scheduleAtFixedRate(() -> sendRankingDTO(socketIOClient), 10, 15, TimeUnit.SECONDS);
            }
        });
    }

    private void addContinue() {
        socketServer.addEventListener("continue", SocketDTO.class, new DataListener<SocketDTO>() {
            @Override
            public void onData(SocketIOClient socketIOClient, SocketDTO socketDTO, AckRequest ackRequest) throws Exception {
                ContinueResDTO continueResDTO = socketService.HandleContinue(socketDTO);
                socketIOClient.sendEvent("listen-continue",continueResDTO);
                if (continueResDTO.getCode()!=200){
                    socketServer.getRoomOperations(getRoomName(socketDTO.getRoomId())).sendEvent("listen-opponent-out-room",socketIOClient,continueResDTO.getRoom());
                }
                else {
                    socketServer.getRoomOperations(getRoomName(socketDTO.getRoomId())).sendEvent("listen-opponent-continue",socketIOClient,continueResDTO.getRoom());
                }
            }
        });
    }


    private void addStart() {
        socketServer.addEventListener("start", SocketDTO.class, new DataListener<SocketDTO>() {
            @Override
            public void onData(SocketIOClient socketIOClient, SocketDTO socketDTO, AckRequest ackRequest) throws Exception {
                socketService.HandleStartRoom(socketDTO);
                socketServer.getRoomOperations(getRoomName(socketDTO.getRoomId())).sendEvent("listen-start","go");
            }
        });
    }

    private void addSendMove() {
        socketServer.addEventListener("send-move", MoveDTO.class, new DataListener<MoveDTO>() {
            @Override
            public void onData(SocketIOClient socketIOClient, MoveDTO moveDTO, AckRequest ackRequest) throws Exception {
                if (!moveDTO.getResult().equals("")){
                    socketService.HandleResult(moveDTO.getRoomId(),moveDTO.getUsername(),moveDTO.getResult());
                }
                socketServer.getRoomOperations(getRoomName(moveDTO.getRoomId())).sendEvent("listen-move",socketIOClient,moveDTO);
            }
        });
    }

    private String getRoomName(String roomID) {
        return "r"+roomID;
    }

    private void addSendMessage() {
        socketServer.addEventListener("send-message", MessageDTO.class, new DataListener<MessageDTO>() {
            @Override
            public void onData(SocketIOClient socketIOClient, MessageDTO messageDTO, AckRequest ackRequest) throws Exception {
                socketServer.getRoomOperations(getRoomName(messageDTO.getRoomId())).sendEvent("listen-message",socketIOClient,messageDTO);
            }
        });
    }

    public void CloseSocket(){
        logger.info("CLOSE SOCKET");
        socketServer.stop();
    }
}
