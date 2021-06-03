package com.pro.myapp;

import java.io.IOException;
import java.util.Map;

public class ShRunnerTest {
    ShRunner shRunner = new ShRunner();

  	
    public void name() throws IOException, InterruptedException {
        String cmds = "sh /Users/Kyeongrok/hello.sh";
        String[] callCmd = {"/bin/bash", "-c", cmds};
        Map map = shRunner.execCommand(callCmd);

        System.out.println(map);
    }
}
