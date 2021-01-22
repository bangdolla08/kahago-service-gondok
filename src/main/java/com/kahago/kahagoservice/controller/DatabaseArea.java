package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.model.response.AreaVersionResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author bangd ON 26/11/2019
 * @project com.kahago.kahagoservice.controller
 */
@Controller
public class DatabaseArea {
    @Value("${kahago.DataBase.version}")
    private String areaVersion;
    @Value("${kahago.DataBase.location}")
    private String areaLocation;
    @GetMapping(value = "/versionarea")
    public @ResponseBody AreaVersionResponse newversion(){
        AreaVersionResponse response=new AreaVersionResponse();
        response.setAreaVersion(areaVersion);
        response.setUrlArea("/versionarea/area.sqlite");       
        return response;
    }
    @GetMapping(value = "/versionarea/{area}")
    public @ResponseBody byte[] getNewArea(@PathVariable String area)throws IOException {
        InputStream in = new FileInputStream(new File(areaLocation));
        return IOUtils.toByteArray(in);
    }
}
