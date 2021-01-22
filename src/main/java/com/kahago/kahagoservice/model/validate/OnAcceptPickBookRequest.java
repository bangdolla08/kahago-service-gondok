package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.ProcessAcceptPickBookMustMatch;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@ProcessAcceptPickBookMustMatch
public class OnAcceptPickBookRequest implements ProcessAcceptPickBookMustMatch.OnAccept {

    @NotNull
    private Integer id;

    @NotEmpty
    private String bookId;

    @NotEmpty
    private String courierId;

    private Integer partId;

    @NotEmpty
    private String qrCode;

    @NotNull
    private MultipartFile image;
}
