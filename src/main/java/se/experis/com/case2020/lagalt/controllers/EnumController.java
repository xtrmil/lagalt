package se.experis.com.case2020.lagalt.controllers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.enums.*;
import se.experis.com.case2020.lagalt.utils.Command;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/api/v1/available/{enumType}", "/api/v1/available/{enumType}/{industry}"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class EnumController {

    Map<String,EnumItem[]> enumMap;

    @PostConstruct
    private void initializeEnums(){
        enumMap = new HashMap<>()
        {{
            put (ApplicationStatus.class.getSimpleName().toLowerCase(), ApplicationStatus.values());
            put (Industry.class.getSimpleName().toLowerCase(), Industry.values());
            put (Tag.class.getSimpleName().toLowerCase(), Tag.values());
            put (ProjectStatus.class.getSimpleName().toLowerCase(), ProjectStatus.values());
        }};
    }
    @GetMapping("")
    public ResponseEntity<CommonResponse> getEnums(HttpServletRequest request, HttpServletResponse response, @PathVariable String enumType, @PathVariable(required = false) Optional<String> industry) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;
        response.addHeader("Location", "/available/"+enumType);

        if(!industry.isPresent()) {

            cr.data = Stream.of(enumMap.get(enumType.toLowerCase()))
                    .collect(Collectors.toMap(e -> e.toString(), e -> e.getLabel()));
        }else{
            cr.data = Stream.of((Tag[]) enumMap.get(enumType.toLowerCase()))
                    .filter(tag -> tag.INDUSTRY.equals(industry.get()))
                    .collect(Collectors.toMap(e -> e.name(), e -> e.getLabel()));
        }
        cr.message = "Successfully retrieved available " + enumType;
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
}
