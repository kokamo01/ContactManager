package com.keimi.okamoto.app.organisers;

import com.keimi.okamoto.app.items.*;
import com.keimi.okamoto.app.utils.MeetingFactory;
import com.keimi.okamoto.app.utils.UniqueNumberGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class MeetingContainerTest {
    private MeetingContainer aMeetingContainer;
    private FutureMeeting aFutureMeeting;
    private MeetingFactory aMeetingFactory;
    private UniqueNumberGenerator aUniqueNumberGenerator;
    private PastMeeting aPastMeeting;

    @Before
    public void buildUp() {
        aMeetingFactory = mock(MeetingFactory.class);
        aFutureMeeting = mock(FutureMeeting.class);
        aUniqueNumberGenerator = mock(UniqueNumberGenerator.class);
        aPastMeeting = mock(PastMeeting.class);

        aMeetingContainer = new MeetingContainerImpl(aMeetingFactory, aUniqueNumberGenerator);
    }

    @Test
    public void shouldBeAbleToAddFutureMeetingAndReturnId() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();

        int actualId = 5;
        when(aUniqueNumberGenerator.getUniqueNumber()).thenReturn(actualId);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(aFutureMeeting);

        aMeetingContainer.addFutureMeeting(aSetOfContacts, date);

        assertEquals(actualId, 5);
    }

    @Test
    public void shouldBeAbleToCheckForValidDate() {
        Calendar date = Calendar.getInstance();

        date.add(Calendar.DATE, -1);
        assertFalse(aMeetingContainer.checkForFuture(date));

        date.add(Calendar.DATE, 2);
        assertTrue(aMeetingContainer.checkForFuture(date));
    }

    @Test
    public void shouldBeAbleToGetFutureMeetingById() throws IllegalMeetingException {
        int id = 0;
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);

        when(aUniqueNumberGenerator.getUniqueNumber()).thenReturn(id);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(aFutureMeeting);

        aMeetingContainer.addFutureMeeting(new HashSet<Contact>(), date);
        FutureMeeting actualFutureMeeting = aMeetingContainer.getFutureMeeting(id);

        assertEquals(aFutureMeeting, actualFutureMeeting);
    }

    @Test
    public void shouldReturnNullIfIdDoesNotCorrespondWithFutureMeeting() {
        int id = 1;
        FutureMeeting actualFutureMeeting = aMeetingContainer.getFutureMeeting(id);

        assertEquals(null, actualFutureMeeting);
    }

    @Test
    public void shouldReturnMeetingWithTheRequestedId() throws IllegalMeetingException {
        int id = 0;
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 1);

        when(aUniqueNumberGenerator.getUniqueNumber()).thenReturn(id);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(aFutureMeeting);

        aMeetingContainer.addFutureMeeting(new HashSet<Contact>(), date);
        Meeting actualMeeting = aMeetingContainer.getMeeting(id);

        assertEquals(aFutureMeeting, actualMeeting);
    }

    @Test
    public void shouldReturnNullIfIdDoesNotCorrespondWithMeeting() throws IllegalMeetingException {
        int id = 0;
        Meeting actualMeeting = aMeetingContainer.getMeeting(id);

        assertEquals(null, actualMeeting);
    }

    /**
     * Test for addPastMeeting() implementation
     * Starts here.
     */
    @Test
    public void shouldBeAbleToAddPastMeeting() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -1);
        String notes = "Some notes go here..";
        PastMeeting pastMeeting = mock(PastMeeting.class);

        when(aMeetingFactory.createPastMeeting(anyInt(), anySet(), eq(date), anyString())).thenReturn(pastMeeting);
        aMeetingContainer.addPastMeeting(aSetOfContacts, date, notes);
        verify(aMeetingFactory).createPastMeeting(anyInt(), anySet(), eq(date), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfDateEnteredIsNotInThePast() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 1);
        String notes = "Some notes go here..";
        aMeetingContainer.addPastMeeting(aSetOfContacts, date, notes);
    }

    /**
     * Test for getPastMeeting() implementation
     * Starts here.
     */
    @Test
    public void shouldBeAbleToGetPastMeetingWithTheRequestedId() throws IllegalMeetingException {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -2);
        int id = 3;
        String notes = "Some notes go here..";
        PastMeeting pastMeeting = mock(PastMeeting.class);
        Set<Contact> aSetOfContacts = new HashSet<>();

        when(aUniqueNumberGenerator.getUniqueNumber()).thenReturn(id);
        when(aMeetingFactory.createPastMeeting(anyInt(), anySet(), eq(date), anyString())).thenReturn(pastMeeting);
        when(pastMeeting.getId()).thenReturn(id);

        aMeetingContainer.addPastMeeting(aSetOfContacts, date, notes);
        PastMeeting actual = aMeetingContainer.getPastMeeting(id);

        assertEquals(pastMeeting, actual);
    }

    /**
     * Test for addMeetingNote()
     * Starts here
     */
    @Test
    public void shouldBeAbleToConvertFutureMeetingToPastMeeting() throws IllegalMeetingException {
        String notes = "Some notes...";
        Calendar date = Calendar.getInstance();
        Set<Contact> contactSet = new HashSet<>();
        when(aFutureMeeting.getDate()).thenReturn(date);
        when(aFutureMeeting.getId()).thenReturn(4);
        when(aFutureMeeting.getContacts()).thenReturn(contactSet);

        when(aMeetingFactory.createPastMeeting(anyInt(), anySet(), eq(date), anyString())).thenReturn(aPastMeeting);
        aMeetingContainer.convertToPastMeeting(aFutureMeeting, notes);
        PastMeeting actualPastMeeting = aMeetingContainer.getPastMeeting(4);

        assertEquals(aPastMeeting, actualPastMeeting);
    }
}
