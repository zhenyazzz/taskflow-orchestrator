package org.example.analyticsservice.mapper;

import org.example.analyticsservice.model.UserMetrics;
import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMetricsMapper {

    @Mapping(source = "id", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "eventType", constant = "REGISTERED")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserMetrics fromUserCreatedEvent(UserCreatedEvent event);

    @Mapping(source = "id", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "eventType", constant = "UPDATED")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserMetrics fromUserProfileUpdatedEvent(UserProfileUpdatedEvent event);

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
