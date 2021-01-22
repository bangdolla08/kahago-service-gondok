package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_permohonan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MPermohonanEntity {
    @Id
    private String nomorPermohonan;
    private Integer status;
    private String lastUser;
    private LocalDateTime lastUpdate;
    private String createUser;
    private LocalDateTime createDate;
}
