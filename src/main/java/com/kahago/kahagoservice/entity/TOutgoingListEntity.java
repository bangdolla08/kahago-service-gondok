package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "t_outgoing_list")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TOutgoingListEntity {
    @Id
    @GeneratedValue
    private Integer idOutgoingList;
    private String courierId;
    private String code;
    private Boolean isPickupVendor;
    private String courierName;
    private String courierPhone;
    private LocalDate processDate;
    private LocalTime processTime;
    private String processBy;
    private LocalDate createDate;
    private String createBy;
    private Integer status;
    private String imgOutgoing;
    @ManyToOne
    @JoinColumn(name = "officeCode")
    private MOfficeEntity officeCode;
    private String uploadBy;
    private LocalDateTime uploadDate;
    private LocalDateTime outgoingDate;
    @ManyToOne
    @JoinColumn(name = "switcherCode")
    private MSwitcherEntity switcherEntity;
}
