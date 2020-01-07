package ktpm.project.controller;

import ktpm.project.dto.*;
import ktpm.project.jwt.JwtUtil;
import ktpm.project.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController {
    @Autowired
    RoomService roomService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping(value = "/api/room")
    public ResponseEntity<?> creatRoom(@RequestBody CreateRoomForm req) {
        try{
            RoomDTO res = roomService.CreateNewRoom(req);
            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (IllegalAccessException e){
            logger.info(e.getMessage());
            logger.info(e.toString());
            ErrorDTO errorDTO = new ErrorDTO("You don't have enough points.",e.getMessage());
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info(e.toString());
            e.printStackTrace();
            ErrorDTO errorDTO = new ErrorDTO("Something was wrong.",e.getMessage());
            return new ResponseEntity<>(errorDTO,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/api/rooms")
    public ResponseEntity<?> getAllWaitingRooms(){
        RoomsDTO res = roomService.getRooms();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}