package jp.co.aw.practice.jdbc.entity;

import static com.google.common.base.Strings.emptyToNull;
import static jp.co.aw.practice.jdbc.utils.DateUtils.format;

import java.time.ZonedDateTime;
import java.util.Optional;

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

    public String toConsoleFormat() {
        return new StringBuilder()//
                .append(id == null ? "<NoID>" : id.toString()).append(" ")//
                .append(Optional.ofNullable(emptyToNull(name)).orElse("<NoName>")).append(" ")//
                .append(Optional.ofNullable(emptyToNull(mail)).orElse("<NoMail>")).append(" ")//
                .append(Optional.ofNullable(emptyToNull(tel)).orElse("<NoTel>")).append(" ")//
                .append(updateDate == null ? "<NoUpdateDate>" : format(updateDate)).toString();
    }
}
