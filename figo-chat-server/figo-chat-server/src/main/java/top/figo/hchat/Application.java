package top.figo.hchat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import top.figo.hchat.utils.IdWorker;

/**
 * @Author Figo
 * @Date 2020/11/18 17:37
 */
@SpringBootApplication
@MapperScan(basePackages = "top.figo.hchat.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }
}