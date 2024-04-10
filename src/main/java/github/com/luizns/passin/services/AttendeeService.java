package github.com.luizns.passin.services;

import github.com.luizns.passin.domain.attendee.Attendee;
import github.com.luizns.passin.domain.attendee.exceptions.AttendeeAlreadyRegisteredException;
import github.com.luizns.passin.domain.checkin.CheckIn;
import github.com.luizns.passin.dto.attendee.AttendeeDetails;
import github.com.luizns.passin.dto.attendee.AttendeesListResponseDTO;
import github.com.luizns.passin.repositories.AttendeeRepository;
import github.com.luizns.passin.repositories.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final CheckInRepository checkinRepository;

    public List<Attendee> getAllAttendeesFromEvent(String eventId) {
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeesListResponseDTO getEventsAttendee(String eventId) {

        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetails> attendeeDetailsList = attendeeList.stream().map(attendee -> {
            Optional<CheckIn> checkIn = this.checkinRepository.findByAttendeeId(attendee.getId());
            LocalDateTime checkedInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
            return new AttendeeDetails(attendee.getId(), attendee.getName(), attendee.getEmail(), attendee.getCreatedAt(), checkedInAt);
        }).toList();

        return new AttendeesListResponseDTO(attendeeDetailsList);
    }

    public Attendee registerAttendee(Attendee newAttenndee) {
        this.attendeeRepository.save(newAttenndee);
        return newAttenndee;
    }

    public void verifyAttendeeSubscritption(String email, String eventId) {
        Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);
        if (isAttendeeRegistered.isPresent())
            throw new AttendeeAlreadyRegisteredException("Attendee is already registered");
    }
}
