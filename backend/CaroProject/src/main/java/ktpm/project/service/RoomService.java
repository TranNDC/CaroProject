package ktpm.project.service;

import ktpm.project.dto.CreateRoomForm;
import ktpm.project.dto.RoomsDTO;
import ktpm.project.model.RoomDAO;
import ktpm.project.dto.RoomDTO;
import ktpm.project.model.UserDAO;
import ktpm.project.repository.RoomRepo;
import ktpm.project.repository.UserRepo;
import ktpm.project.utils.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoomRepo roomRepo;

    @Autowired
    UserRepo userRepo;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${number.background}")
    Integer nBackground;

    static Integer roomID = 0;

    synchronized Integer getRoomID() {
        roomID++;
        return  roomID;
    }

    public RoomDTO CreateNewRoom(CreateRoomForm roomDTO) throws Exception {
        if (roomDTO.getBetPoint() != 0) {
            UserDAO host = userRepo.findFirstByUsername(roomDTO.getHost()).orElse(null);
            if (host == null){
                throw new Exception("Not found Username");
            }
            if (host.getPoints() < roomDTO.getBetPoint()) {
                throw new IllegalArgumentException("Please pick Bet Points <= "+host.getPoints().toString());
            }
        }
        Integer id = getRoomID();
        RoomDAO roomDAO = new RoomDAO();
        roomDAO.setId(id);
        roomDAO.setBetPoint(roomDTO.getBetPoint());
        String password = (roomDTO.getPassword()==null || roomDTO.getPassword().length() != 0 )? passwordEncoder.encode(roomDTO.getPassword()) : "";
        roomDAO.setPassword(password);
        roomDAO.setHost(roomDTO.getHost());
        roomDAO.setName(roomDTO.getRoomName());
        roomDAO.setIsWaiting(true);
        roomRepo.save(roomDAO);
        RoomDTO res = new RoomDTO();
        res.setId(id);
        res.setBetPoint(roomDTO.getBetPoint());
        res.setRoomName(roomDTO.getRoomName());
        res.setHasPassword(roomDTO.getPassword()!="");
        res.setHost(roomDTO.getHost());
        res.setGuest("");
        res.setBackground(utils.randomImage(nBackground));
        return res;
    }

    public RoomsDTO getRooms(){
        ArrayList<RoomDTO> roomDTOS = new ArrayList<>();
        List<RoomDAO> rooms = (List<RoomDAO>) roomRepo.findAll();
//        List<RoomDAO> rooms = (List<RoomDAO>) roomRepo.findAllByIsWaiting(true);
        logger.error("SIZE:"+rooms.size());
        for (RoomDAO room :
                rooms) {
            if (room.getIsWaiting()== true){
                RoomDTO roomDTO = new RoomDTO();
                roomDTO.setBetPoint(room.getBetPoint());
                roomDTO.setId(room.getId());
                roomDTO.setHost(room.getHost());
                roomDTO.setRoomName(room.getName());
                roomDTO.setBetPoint(room.getBetPoint());
                roomDTO.setHasPassword(!room.getPassword().equals(""));
                roomDTOS.add(roomDTO);
            }
        }
        RoomsDTO res = new RoomsDTO();
        res.setRooms(roomDTOS);
        return res;
    }


}
