package org.example.events.user;




import lombok.Builder;

@Builder
public record LoginFailEvent(
    String id,
    String username,
    String email,
    String failureReason,
    String userAgent
) {

}
