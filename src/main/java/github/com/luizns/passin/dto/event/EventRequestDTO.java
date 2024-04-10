package github.com.luizns.passin.dto.event;

public record EventRequestDTO(
        String title,
        String details,
        Integer maximumAttendees
) {
}
