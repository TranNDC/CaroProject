package ktpm.project.service;

import ktpm.project.dto.*;
import ktpm.project.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SocketService {


    @Autowired
    RoomService roomService;

    @Autowired
    RankService rankService;

    public JoinResDTO HandleJoinRoom(JoinFormDTO joinFormDTO) {
        JoinResDTO response = new JoinResDTO();
        try{
            RoomDTO res = roomService.JoinRoom(joinFormDTO);
            response.setCode(200);
            response.setRoom(res);
            return response;
        } catch (IllegalArgumentException e){
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO("You don't have enough points.",e.getMessage());
            response.setError(errorDTO);
        } catch (IllegalAccessException e){
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO(e.getMessage(),"Please check your input again.");
            response.setError(errorDTO);
        } catch (ClassNotFoundException e){
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO("Room's not available!.",e.getMessage());
            response.setError(errorDTO);
        }
        catch (Exception e) {
            e.printStackTrace();
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO("Something was wrong.",e.getMessage());
            response.setError(errorDTO);
        }
        return response;
    }

    public JoinResDTO HandleCreateRoom(CreateRoomForm createRoomForm) {
        JoinResDTO response = new JoinResDTO();
        try{
            RoomDTO res = roomService.CreateNewRoom(createRoomForm);
            response.setCode(200);
            response.setRoom(res);
            return response;

        } catch (IllegalArgumentException e){
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO("You don't have enough points.",e.getMessage());
            response.setError(errorDTO);
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO("Something was wrong.",e.getMessage());
            response.setError(errorDTO);
        }
        return response;
    }

    public void HandleStartRoom(SocketDTO socketDTO) {
        try {
            roomService.ChangeMode(socketDTO.getRoomId(),RoomService.PLAYING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //waiting -> playing
    }

    public ContinueResDTO HandleContinue(SocketDTO socketDTO) {
        ContinueResDTO response = new ContinueResDTO();
        try{
            RoomDTO res = roomService.Continue(socketDTO.getRoomId(),socketDTO.getUsername());
            response.setCode(200);
            response.setRoom(res);
            return response;
        } catch (IllegalArgumentException e){
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO("You don't have enough points.",e.getMessage());
            response.setError(errorDTO);
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(400);
            ErrorDTO errorDTO = new ErrorDTO("Something was wrong.",e.getMessage());
            response.setError(errorDTO);
        }
        return response;
    }

    public RoomDTO HandleExit(String roomID, String username) {
        RoomDTO roomDTO =  roomService.Exit(roomID,username);
        return roomDTO;
    }

    public void HandleResult(String roomId, String username, String result) {
        roomService.HandleResult(roomId,username,result);
    }

    public RoomsDTO getRooms() {
        return roomService.getRooms();
    }

    public  RankingDTO getRank(){
        return rankService.getRanking();
    }
}
