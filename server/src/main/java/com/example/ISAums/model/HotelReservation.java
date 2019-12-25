package com.example.ISAums.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_reservation")
@Where(clause = "is_deleted='false'")
public class HotelReservation extends BaseEntity {

    @Column(name = "price")
    @NotNull
    @Range(min = 0)
    private Double price;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "start_date")
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotNull
    private LocalDate endDate;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airplane_ticket_id")
    private AirplaneTicket airplaneTicket;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "additional_service")
    private List<HotelService> additionalServices;


    @Version
    private Integer version;
}
