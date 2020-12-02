package se.experis.com.case2020.lagalt.models;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TEST_Person {

    private String name;
    private String age;
    private String location;

    public TEST_Person() {
        super();
    }

    @Override
    public String toString(){
        return name;
    }
}