package rs.ogisa.notesapp.dto;

import lombok.Getter;
import lombok.Setter;
import rs.ogisa.notesapp.models.AuthenticationDetails;

import java.io.Serializable;

@Getter
@Setter
public class CreateUserDto implements Serializable{

    private String username;

    private String password;

}
