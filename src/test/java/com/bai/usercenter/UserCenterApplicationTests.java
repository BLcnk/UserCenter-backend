package com.bai.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class UserCenterApplicationTests {

    @Test
    void testDigest(){
        String newPassword = DigestUtils.md5DigestAsHex(("abcd"+"a123456").getBytes());
        System.out.println(newPassword);
    }

    @Test
    void contextLoads() {

    }

}
