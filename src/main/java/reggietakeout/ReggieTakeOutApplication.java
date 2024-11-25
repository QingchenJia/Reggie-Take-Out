package reggietakeout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ReggieTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOutApplication.class, args);
        log.info("""
                                
                -----------------------------------------------------------------------
                \t项目 ReggieTakeOut 启动中......
                \t后台:\thttp://localhost:9110/backend/page/login/login.html
                \t客户:\thttp://localhost:9110/front/page/login.html
                \tAPI文档:\thttp://localhost:9110/doc.html
                -----------------------------------------------------------------------""");
    }

}
