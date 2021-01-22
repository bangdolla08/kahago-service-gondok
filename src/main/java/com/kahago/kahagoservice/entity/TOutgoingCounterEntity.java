package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_outgoing_counter")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TOutgoingCounterEntity {
    @Id
    @GeneratedValue
    private Integer idOutgoingCounter;
    private String codeCounter;
    private String courierId;
    @ManyToOne
    @JoinColumn(name = "officeCode")
    private MOfficeEntity officeCode;
    private Integer status;
    private LocalDate createDate;
    private String createBy;
    private LocalDateTime updateDate;
    private String updateBy;
}
