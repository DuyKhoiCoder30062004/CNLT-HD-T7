package entities;


import User;
import Receiver;
import Status;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "[Order]")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "receiverId",
            nullable = false
    )
    private Receiver receiver;

    @ManyToOne
    @JoinColumn(
            name = "userId",
            nullable = false
    )
    private User user;

    @ManyToOne
    @JoinColumn(
            name = "statusId",
            nullable = false
    )
    private Status status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderDetail> orderDetails =
            new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    private LocalDateTime editedAt;
}
