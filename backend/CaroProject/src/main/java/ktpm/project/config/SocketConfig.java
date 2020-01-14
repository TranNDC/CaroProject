package ktpm.project.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import ktpm.project.controller.http.UserController;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;


@org.springframework.context.annotation.Configuration
public class SocketConfig {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);

    static SocketIOServer socketServer = null;

    public SocketIOServer getSocketServer() {
        if (socketServer==null)
            createSocketServer();
        return socketServer;
    }

    private void createSocketServer() {
        logger.warn("CREATE SERVER");
        Configuration config = getConfig();
        try{
            logger.warn(String.valueOf(config.getPort()));
            socketServer =  new SocketIOServer(config);
        } catch (Exception e){
            logger.error(e.getMessage());
            logger.error(e.toString());
            socketServer=null;
        }
    }

    private Configuration getConfig() {
        try (InputStream input = new FileInputStream("./socketio.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            Configuration configuration = new Configuration();
            configuration.setHostname(prop.getProperty("socket.io.host"));
            configuration.setPort(Integer.parseInt(prop.getProperty("socket.io.port")));
            return configuration;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
