package org.example.analyticsservice.mapper;

import org.example.analyticsservice.model.LoginMetrics;
import org.example.events.user.LoginFailEvent;
import org.example.events.user.UserLoginEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LoginMetricsMapper {

    @Mapping(source = "id", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "loginStatus", constant = "SUCCESS")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "userAgent", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    LoginMetrics fromUserLoginEvent(UserLoginEvent event);

    @Mapping(target = "loginStatus", constant = "FAILED")
    @Mapping(target = "failureReason", constant = "Login attempt failed")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "userAgent", ignore = true)
    LoginMetrics fromFailedLoginAttempt(LoginFailEvent event);

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
