package com.example.ecommerce.dtos.receiver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverDTO {

    private Integer id;

    private String name;

    private String phone;

    private String address;

    private String wardName;

    private String provinceName;
}
