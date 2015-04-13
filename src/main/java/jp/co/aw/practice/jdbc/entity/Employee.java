package jp.co.aw.practice.jdbc.entity;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
@Data
public class Employee {

    @NonNull
    Long id;

    @NonNull
    String name;

    String mail;

    String tel;

    ZonedDateTime updateDate;

    @NonNull
    Boolean isDeleted;
}
