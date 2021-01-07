package se.experis.com.case2020.lagalt.controllers;

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
            put ("applicationstatuses", ApplicationStatus.values());
            put ("industries", Industry.values());
            put ("tags", Tag.values());
            put ("projectstatuses", ProjectStatus.values());
        }};
    }
    @GetMapping("")
    public ResponseEntity<CommonResponse> getEnums(HttpServletRequest request, HttpServletResponse response, @PathVariable String enumType, @PathVariable(required = false) Optional<String> industry) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        response.addHeader("Location", "/available/" + enumType);

        try {
            if(!industry.isPresent()) {
                cr.data = Stream.of(enumMap.get(enumType.toLowerCase()))
                .collect(Collectors.toMap(e -> e.toString(), e -> e.getLabel()));
            } else {
                cr.data = Stream.of((Tag[]) enumMap.get(enumType.toLowerCase()))
                .filter(tag -> tag.INDUSTRY.equalsIgnoreCase(industry.get()))
                .collect(Collectors.toMap(e -> e.name(), e -> e.getLabel()));
            }
            resp = HttpStatus.OK;
            cr.message = "Successfully retrieved available " + enumType;
        } catch(NullPointerException e) {
            resp = HttpStatus.NOT_FOUND;
            cr.message = "Path does not exist";
        } catch(Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
}
