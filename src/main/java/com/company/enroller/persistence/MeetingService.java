package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {

    Session session;

    public MeetingService() {
        session = DatabaseConnector.getInstance().getSession();
    }

    public Collection<Meeting> getAll() {
        String hql = "FROM Meeting";
        Query query = this.session.createQuery(hql);
        return query.list();
    }

    public Meeting findById(long id) {
        return this.session.get(Meeting.class, id);
    }

    public Collection<Meeting> findMeetings(String title, String description, Participant participant, String sortMode) {
        String hql = "FROM Meeting as meeting WHERE title LIKE :title AND description LIKE :description ";
        if (participant != null) {
            hql += " AND :participant in elements(participants)";
        }
        if (sortMode.equals("title")) {
            hql += " ORDER BY title";
        }
        Query query = this.session.createQuery(hql);
        query.setParameter("title", "%" + title + "%").setParameter("description", "%" + description + "%");
        if (participant != null) {
            query.setParameter("participant", participant);
        }
        return query.list();
    }

    public void delete(Meeting meeting) {
        Transaction transaction = this.session.beginTransaction();
        this.session.delete(meeting);
        transaction.commit();
    }

    public void add(Meeting meeting) {
        Transaction transaction = this.session.beginTransaction();
        this.session.save(meeting);
        transaction.commit();
    }

    public void update(Meeting meeting) {
        Transaction transaction = this.session.beginTransaction();
        this.session.merge(meeting);
        transaction.commit();
    }

    public boolean alreadyExist(Meeting meeting) {
        String hql = "FROM Meeting WHERE title=:title AND date=:date";
        Query query = this.session.createQuery(hql);
        Collection resultList = query.setParameter("title", meeting.getTitle()).setParameter("date", meeting.getDate())
                .list();
        return query.list().size() != 0;
    }

    public void getParticipants(Long meetingId) {
        Meeting meeting = session.get(Meeting.class, meetingId);
        if (meeting == null) {
            throw new IllegalArgumentException("Meeting not found");
        }
        meeting.getParticipants();
    }

    public void addParticipantToMeeting(Long meetingId, String login) {
        Transaction transaction = session.beginTransaction();

        Meeting meeting = session.get(Meeting.class, meetingId);
//        Participant participant
//                = session
//                .createQuery("FROM Participant WHERE login=:login", Participant.class)
//                .setParameter("login", login)
//                .uniqueResult();

        ParticipantService participantService = new ParticipantService();
        Participant participant = participantService.findByLogin(login);

        System.out.println("Participant added " + participant+ " Meeting " + meeting);
        if (meeting == null || participant == null) {
            transaction.rollback();
            throw new IllegalArgumentException("Meeting or participant not found");
        }

        meeting.getParticipants().add(participant);
        session.merge(meeting);

        transaction.commit();
    }

    public void removeParticipantFromMeeting(Long meetingId, String login) {
        Transaction transaction = session.getSession().beginTransaction();

        Meeting meeting = session.get(Meeting.class, meetingId);
        Participant participant = session
                .createQuery("FROM Participant WHERE login = :login", Participant.class)
                .setParameter("login", login)
                .uniqueResult();

        if (meeting == null || participant == null) {
            transaction.rollback();
            throw new IllegalArgumentException("Meeting or participant not found");
        }

        meeting.getParticipants().remove(participant);
        session.merge(meeting);

        transaction.commit();
    }

}
