package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.ProcessCancelPickMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@ProcessCancelPickMustMatch
public class OnCancelPickingRequest implements ProcessCancelPickMustMatch.OnCancel {
    @NotNull
    private Integer id;

    @NotEmpty
    private String bookId;

    @NotEmpty
    private String courierId;

    @NotEmpty
    private String status;

    private String reason;

}
