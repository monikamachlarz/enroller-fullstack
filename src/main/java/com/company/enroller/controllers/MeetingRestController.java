package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> findMeetings(@RequestParam(value = "title", defaultValue = "") String title,
                                          @RequestParam(value = "description", defaultValue = "") String description,
                                          @RequestParam(value = "sort", defaultValue = "") String sortMode,
                                          @RequestParam(value = "participantLogin", defaultValue = "") String participantLogin) {

        Participant foundParticipant = null;
        if (!participantLogin.isEmpty()) {
            foundParticipant = participantService.findByLogin(participantLogin);
        }
        Collection<Meeting> meetings = meetingService.findMeetings(title, description, foundParticipant, sortMode);
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (meeting.getParticipants().isEmpty()) {
            meetingService.delete(meeting);
            return new ResponseEntity<Meeting>(meeting, HttpStatus.NO_CONTENT);
        }else {
            return new ResponseEntity<Meeting>(meeting, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        if (meetingService.alreadyExist(meeting)) {
            return new ResponseEntity<String>("Unable to add. A meeting with title " + meeting.getTitle() + " and date "
                    + meeting.getDate() + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
        Meeting currentMeeting = meetingService.findById(id);
        if (currentMeeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meeting.setId(currentMeeting.getId());
        meetingService.update(meeting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<Set<Participant>> getParticipants(@PathVariable Long id) {
        meetingService.getParticipants(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") Long id, @RequestBody Participant participant) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Participant foundParticipant = participantService.findByLogin(participant.getLogin());
        if (foundParticipant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        meetingService.addParticipantToMeeting(id, foundParticipant);
        meetingService.update(meeting);
        return new ResponseEntity<Participant>(participant, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") Long id, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Participant foundParticipant = participantService.findByLogin(login);
        if (foundParticipant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            meetingService.removeParticipantFromMeeting(id, foundParticipant);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}