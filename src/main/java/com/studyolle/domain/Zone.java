package com.studyolle.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Zone
{
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String City;

    @Column(nullable = false)
    private String LocalNameOfCity;

    @Column(nullable = true)
    private String province;

    @Override
    public String toString() {
        return City + "(" + LocalNameOfCity + ")" + province;
    }
}
