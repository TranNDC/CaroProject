package ktpm.project.controller;

import ktpm.project.dto.*;
import ktpm.project.jwt.JwtUtil;
import ktpm.project.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        } catch (IllegalArgumentException e){
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

    @GetMapping(value = "/api/rooms/search")
    public ResponseEntity<?> searchRoom(@RequestParam String id){
        try{
            int intId = Integer.parseInt(id);
            RoomDTO res = roomService.findEmptyRoomById(intId);
            return new ResponseEntity<>(res,HttpStatus.OK);
        } catch (IllegalArgumentException e){
            ErrorDTO errorDTO = new ErrorDTO("Room is full!","Please pick another room ...");
            return new ResponseEntity<>(errorDTO,HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e){
            ErrorDTO errorDTO = new ErrorDTO("Invalid room ID!","Please check your input again ...");
            return new ResponseEntity<>(errorDTO,HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.info(e.getMessage());
            logger.info(e.toString());
            e.printStackTrace();
            ErrorDTO errorDTO = new ErrorDTO("Can't search room now.","Something was wrong. Please try again...");
            return new ResponseEntity<>(errorDTO,HttpStatus.BAD_REQUEST);
        }
    }
}