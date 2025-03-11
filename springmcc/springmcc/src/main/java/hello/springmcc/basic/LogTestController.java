package hello.springmcc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Slf4j 는 아래에 있는  private  final Logger log = LoggerFactory.getLogger(getClass()); 를 어노테이션 으로 쓰는 방법 이다.
@Slf4j
@RestController
public class LogTestController {

//    Logger sf.4j 를 사용

//    private  final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/log-test")
    private  String logTest(){
        String name="hamio";
        String name2="kSG";


        log.info("info log={},{}",name,name2);
        log.trace("trace log={},{}",name,name2);
        log.debug("debug log={},{}",name,name2);
        log.warn("warn log={},{}",name,name2);
        log.error("error log={},{}",name,name2);
        return "ok";

    }
}
