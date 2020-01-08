package ktpm.project.service;

import ktpm.project.dto.CreateRoomForm;
import ktpm.project.dto.RoomsDTO;
import ktpm.project.dto.UserDTO;
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
import java.util.Optional;

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
        UserDAO host = userRepo.findFirstByUsername(roomDTO.getHost()).orElse(null);
        if (roomDTO.getBetPoint() != 0) {
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
        roomDAO.setIsWaiting(1);
        roomRepo.save(roomDAO);
        RoomDTO res = new RoomDTO();
        res.setId(String.valueOf(id));
        res.setBetPoint(roomDTO.getBetPoint());
        res.setRoomName(roomDTO.getRoomName());
        res.setHasPassword(!roomDTO.getPassword().equals(""));
        res.setHost(new UserDTO(host));
        res.setGuest(null);
        res.setBackground(utils.randomImage(nBackground));
        return res;
    }

    private RoomDTO toRoomDTO(RoomDAO room){
        UserDAO host = userRepo.findFirstByUsername(room.getHost()).orElse(null);
        UserDAO guest = userRepo.findFirstByUsername(room.getGuest()).orElse(null);

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setBetPoint(room.getBetPoint());
        roomDTO.setId(String.valueOf(room.getId()));

        if (host!=null)
            roomDTO.setHost(new UserDTO(host));
        else roomDTO.setHost(null);

        if (guest!=null)
            roomDTO.setGuest(new UserDTO(guest));
        else roomDTO.setGuest(null);

        roomDTO.setRoomName(room.getName());
        roomDTO.setBetPoint(room.getBetPoint());
        roomDTO.setHasPassword(!room.getPassword().equals(""));
        return roomDTO;
    }

    public RoomsDTO getRooms(){
        ArrayList<RoomDTO> roomDTOS = new ArrayList<>();
        List<RoomDAO> rooms = (List<RoomDAO>) roomRepo.findAll();
        for (RoomDAO room :
                rooms) {
            if (room.getIsWaiting()==1){
                RoomDTO roomDTO = toRoomDTO(room);
                roomDTOS.add(roomDTO);
            }
        }
        RoomsDTO res = new RoomsDTO();
        res.setRooms(roomDTOS);
        return res;
    }


    public RoomDTO findEmptyRoomById(int id) {
        Optional<RoomDAO> room = roomRepo.findById(id);
        if (!room.isPresent()) {
            throw new NullPointerException("Invalid room ID!");
        } else {
            RoomDAO roomDAO = room.get();
            if (roomDAO.getIsWaiting()!=1){
                throw new IllegalArgumentException("Room is full!");
            }
            return toRoomDTO(roomDAO);
        }
    }
}
