package jp.co.aw.practice.jdbc.entity;

import java.sql.Timestamp;

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

    Timestamp updateDate;

    @NonNull
    Boolean isDeleted;

    public Boolean setIsDeleted(Byte b) {
        isDeleted = b == null || b.byteValue() != 0;
        return isDeleted;
    }
}
