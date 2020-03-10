package info.karlovskiy.yaus.model.action;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class ShortenUrlAction {

    @NotEmpty
    private String longURL;

}
