package ktpm.project;

import com.corundumstudio.socketio.SocketIOServer;
import io.netty.util.internal.SocketUtils;
import ktpm.project.config.SocketConfig;
import ktpm.project.controller.socketio.SocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(Application.class, args);
//        SocketController socketController = new SocketController();
        SocketController socketController = context.getBean(SocketController.class);
        socketController.StartSocket();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                socketController.CloseSocket();
            }
        });
    }
}