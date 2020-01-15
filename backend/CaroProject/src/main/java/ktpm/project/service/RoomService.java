package ktpm.project.service;

import ktpm.project.dto.*;
import ktpm.project.model.RoomDAO;
import ktpm.project.model.UserDAO;
import ktpm.project.repository.RankRepo;
import ktpm.project.repository.RoomRepo;
import ktpm.project.repository.UserRepo;
import ktpm.project.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    static final int FREE = 0;
    static final int WAITING = 1;
    static final int PLAYING = 2;
    private static final String LOSE = "lose";
    private static final String WIN = "win";
    private static final String DRAW = "draw";

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoomRepo roomRepo;

    @Autowired
    RankRepo rankRepo;

    @Autowired
    UserRepo userRepo;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${number.background}")
    Integer nBackground;

    @Value("${win.points}")
    Integer WIN_POINTS;

    @Value("${lose.points}")
    Integer LOSE_POINTS;

    @Value("${draw.points}")
    Integer DRAW_POINTS;

    static Integer roomID = 0;

    synchronized Integer getRoomID() {
        roomID++;
        return  roomID;
    }

    private void CheckCanJoin(RoomDAO room, String username) throws Exception{
        if (room == null|| room.getHost()==null){
            throw new ClassNotFoundException("Not found room");
        }
        if (room.getGuest()!=null){
            throw new ClassNotFoundException("Room's full");
        }
        UserDAO userDAO = userRepo.findFirstByUsername(username).orElse(null);
        if (userDAO==null){
            throw new Exception("Not found user");
        }
        if (userDAO.getPoints() < room.getBetPoint()){
            throw new IllegalArgumentException("Make sure your points >= "+room.getBetPoint());
        }
    }

    public RoomDTO CreateNewRoom(CreateRoomForm roomDTO) throws Exception {
        logger.warn(String.valueOf(roomDTO));
        UserDAO host = userRepo.findFirstByUsername(roomDTO.getUsername()).orElse(null);
        if (roomDTO.getBetPoint() != null && roomDTO.getBetPoint() != 0) {
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
        Integer betPoints = roomDTO.getBetPoint()!=null?roomDTO.getBetPoint():0;
        roomDAO.setBetPoint(betPoints);
        String password = (roomDTO.getPassword()!=null && roomDTO.getPassword().length() != 0 )? passwordEncoder.encode(roomDTO.getPassword()) : "";
        roomDAO.setPassword(password);
        roomDAO.setHost(roomDTO.getUsername());
        roomDAO.setName(roomDTO.getRoomName());
        roomDAO.setMode(FREE);
        roomDAO.setGuestPoints(0);
        roomDAO.setIsHostPlayFirst(1);
        roomDAO.setGuestReady(0);
        roomDAO.setHostReady(1);
        roomRepo.save(roomDAO);
        RoomDTO res = new RoomDTO();
        res.setId(String.valueOf(id));
        res.setBetPoint(roomDTO.getBetPoint());
        res.setRoomName(roomDTO.getRoomName());
        res.setHasPassword(roomDTO.getPassword()!=null&&!roomDTO.getPassword().equals(""));
        res.setHost(new UserDTO(host));
        res.setGuest(null);
        res.setGuestReady(false);
        res.setHostReady(true);
        res.setBackground(Utils.randomImage(nBackground));
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
        roomDTO.setGuestPoint(room.getGuestPoints());
        roomDTO.setHostPoint(room.getHostPoints());
        roomDTO.setIsHostPlayFirst(room.getIsHostPlayFirst()==1);
        roomDTO.setGuestReady(room.getGuestReady()==1);
        roomDTO.setHostReady(room.getHostReady()==1);
        return roomDTO;
    }

    public RoomsDTO getRooms(){
        ArrayList<RoomDTO> roomDTOS = new ArrayList<>();
        List<RoomDAO> rooms = (List<RoomDAO>) roomRepo.findAll();
        for (RoomDAO room :
                rooms) {
            if (room.getMode()==FREE){
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
            if (roomDAO.getMode()!=FREE){
                throw new IllegalArgumentException("Room is full!");
            }
            return toRoomDTO(roomDAO);
        }
    }

    public RoomDTO JoinRoom(JoinFormDTO joinFormDTO) throws Exception {
        ChangeMode(joinFormDTO.getRoomId(),WAITING);
        RoomDAO room = roomRepo.findById(Integer.valueOf(joinFormDTO.getRoomId())).orElse(null);
        CheckCanJoin(room,joinFormDTO.getUsername());
        if (!room.getPassword().equals("")) {
            if (!passwordEncoder.matches(room.getPassword(),joinFormDTO.getPassword())){
                throw new IllegalAccessException("Password is incorrect");
            }
        }
        room.setGuest(joinFormDTO.getUsername());
        room.setGuestPoints(0);
        room.setGuestReady(1);
        roomRepo.save(room);
        return toRoomDTO(room);
    }

    public void ChangeMode(String roomId, int mode) throws Exception {
        if (!isAvaibleMode(mode))
            return;
        RoomDAO roomDAO = roomRepo.findById(Integer.valueOf(roomId)).orElse(null);
        if (roomDAO!=null){
            roomDAO.setMode(mode);
        } else {
            throw new ClassNotFoundException("Not found room");
        }
        roomRepo.save(roomDAO);
    }

    private boolean isAvaibleMode(int mode) {
        return mode>=0 && mode<=2;
    }

    private RoomDAO ChangeHost(RoomDAO room){
        logger.info("Change host in room "+ roomID);
        if (room.getHost()!=null){
            room.setHost(room.getGuest());
            room.setHostPoints(0);
            room.setHostReady(1);
            room.setGuest("");
            room.setGuestReady(0);
            room.setGuestPoints(0);
            room.setIsHostPlayFirst(1);
            room.setMode(FREE);
        }
        return room;
    }

    public RoomDTO Continue(String roomId, String username) throws Exception {
        RoomDAO room = roomRepo.findById(Integer.valueOf(roomId)).orElse(null);
        CheckCanJoin(room,username);
        ChangeHost(room);

        room = roomRepo.save(room);
        return toRoomDTO(room);
    }

    private boolean isNotAvailable(String name){
        return name==null || name.equals("");
    }

    public RoomDTO Exit(String roomID, String username) {
        logger.info(username + " EXIT in room "+ roomID);
        RoomDAO room = roomRepo.findById(Integer.valueOf(roomID)).orElse(null);
        if (room==null) return null;
        if (IsPlayingRoom(room)){
            HandleResult(room,username,LOSE);
        }

        room.setMode(FREE);

        if (room.getHost().equals(username)){
            room = ChangeHost(room);
        }

        room.setGuest(null);
        if (isNotAvailable(room.getHost()) && isNotAvailable(room.getGuest())){
            logger.info("DELETE ROOM");
            roomRepo.delete(room);
        } else {
            roomRepo.save(room);
        }
        return toRoomDTO(room);

    }

    public void HandleResult(String roomID, String username, String result) {
        RoomDAO room = roomRepo.findById(Integer.valueOf(roomID)).orElse(null);
        if (room == null) return;
        HandleResult(room,username,result);
    }

    private void HandleResult(RoomDAO room, String username, String result) {
        logger.info("HANDLE RESULT");
        UserDAO guest = userRepo.findFirstByUsername(room.getGuest()).orElse(null);
        UserDAO host = userRepo.findFirstByUsername(room.getHost()).orElse(null);
        if (result.equals(DRAW)){
            guest.setDrawCount(guest.getDrawCount()+1);
            host.setDrawCount(host.getDrawCount()+1);
            guest.setPoints(guest.getPoints()+DRAW_POINTS);
            host.setPoints(host.getPoints()+DRAW_POINTS);
            userRepo.save(guest);
            userRepo.save(host);
        }else {
            UserDAO winner = (result.equals(WIN))? guest.getUsername().equals(username) ?guest:host
                    : guest.getUsername().equals(username) ?host:guest;
            UserDAO loser = (result.equals(WIN))? !guest.getUsername().equals(username) ?guest:host
                    : !guest.getUsername().equals(username) ?host:guest;
            winner.setPoints(winner.getPoints()+WIN_POINTS+room.getBetPoint());
            loser.setPoints(winner.getPoints()+LOSE_POINTS-room.getBetPoint());

            winner.setWinCount(winner.getWinCount()+1);
            winner.setLoseCount(winner.getLoseCount()+1);
            userRepo.save(winner);
            userRepo.save(loser);
        }
        rankRepo.save(guest);
        rankRepo.save(host);
    }

    public boolean IsPlayingRoom(RoomDAO room) {
        return room.getMode() == PLAYING;
    }
}
