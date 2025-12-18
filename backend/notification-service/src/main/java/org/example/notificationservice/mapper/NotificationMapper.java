package org.example.notificationservice.mapper;

import org.example.notificationservice.model.Notification;
import org.example.notificationservice.dto.response.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    
    NotificationDto toDto(Notification notification);
    
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "read", ignore = true)
    Notification toEntity(NotificationDto notificationDto);
}
