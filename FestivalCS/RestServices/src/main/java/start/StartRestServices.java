package start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@ComponentScan({"restServices","repository"})

@SpringBootApplication
public class StartRestServices {

    public static void main(String[] args) {

        SpringApplication.run(StartRestServices.class, args);
    }

    @Bean(name="props")
    public static Properties getBdProperties(){
        Properties props = new Properties();
        try {
            System.out.println("Searching bd.config in directory "+((new File(".")).getAbsolutePath()));
            //props.load(new FileReader("bd.config"));
            props.load(StartRestServices.class.getResourceAsStream("/bd.config"));
        } catch (IOException e) {
            System.err.println("Configuration file bd.config not found" + e);

        }
        return props;
    }
}
