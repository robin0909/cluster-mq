package com.robin.manager.controller;

import com.robin.manager.help.action.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/core")
public class CoreController {

    @RequestMapping(value = "/check")
    public Result check() {
        return Result.newSucess();
    }

    @RequestMapping(value = "/update")
    public Result update(String data) {

        return Result.newSucess();
    }
}
