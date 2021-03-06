package com.keimi.okamoto.app.organisers;

import com.keimi.okamoto.app.items.*;
import com.keimi.okamoto.app.utils.IllegalMeetingException;
import com.keimi.okamoto.app.utils.MeetingFactory;
import com.keimi.okamoto.app.utils.UniqueNumberGeneratorUtilities;
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
    private UniqueNumberGeneratorUtilities aUniqueNumberGeneratorUtilities;
    private PastMeeting aPastMeeting;

    @Before
    public void buildUp() {
        aMeetingFactory = mock(MeetingFactory.class);
        aFutureMeeting = mock(FutureMeeting.class);
        aUniqueNumberGeneratorUtilities = mock(UniqueNumberGeneratorUtilities.class);
        aPastMeeting = mock(PastMeeting.class);

        aMeetingContainer = new MeetingContainerImpl(aMeetingFactory, aUniqueNumberGeneratorUtilities);
    }

    /*
     * Test for addFutureMeeting(Set<Contact> aSetOfContacts, Calendar date)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToAddFutureMeetingAndReturnId() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 1);

        int actualId = 5;
        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(actualId);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(aFutureMeeting);

        aMeetingContainer.addFutureMeeting(aSetOfContacts, date);

        assertEquals(actualId, 5);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfDateIsInThePast() {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -2);

        aMeetingContainer.addFutureMeeting(aSetOfContacts, date);
    }

    /*
     * Test for checkForPast(Calendar date)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToCheckForValidPastDate() {
        Calendar date = Calendar.getInstance();

        date.add(Calendar.DATE, 1);
        assertFalse(aMeetingContainer.checkForPast(date));

        date.add(Calendar.DATE, -2);
        assertTrue(aMeetingContainer.checkForPast(date));
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfParameterIsNullForPastDate() {
        aMeetingContainer.checkForPast(null);
    }

    /*
     * Test for getFutureMeeting(int id)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToCheckForValidFutureDate() {
        Calendar date = Calendar.getInstance();

        date.add(Calendar.DATE, -1);
        assertFalse(aMeetingContainer.checkForFuture(date));

        date.add(Calendar.DATE, 2);
        assertTrue(aMeetingContainer.checkForFuture(date));
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfParameterIsNullForFutureDate() {
        aMeetingContainer.checkForFuture(null);
    }

    /*
     * Test for getFutureMeeting(int id)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToGetFutureMeetingById() throws IllegalMeetingException {
        int id = 0;
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id);
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


    /*
    * Test for getMeeting(int id)
    * Starts here:
    */
    @Test
    public void shouldReturnMeetingWithTheRequestedId() throws IllegalMeetingException {
        int id = 0;
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 1);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id);
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

    /*
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

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfASetOfContactsIsNull() {
        aMeetingContainer.addPastMeeting(null, Calendar.getInstance(), "");
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfADateIsNull() {
        aMeetingContainer.addPastMeeting(new HashSet<Contact>(), null, "");
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfANoteIsNull() {
        aMeetingContainer.addPastMeeting(new HashSet<Contact>(), Calendar.getInstance(), null);
    }

    /*
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

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id);
        when(aMeetingFactory.createPastMeeting(anyInt(), anySet(), eq(date), anyString())).thenReturn(pastMeeting);
        when(pastMeeting.getId()).thenReturn(id);

        aMeetingContainer.addPastMeeting(aSetOfContacts, date, notes);
        PastMeeting actual = aMeetingContainer.getPastMeeting(id);

        assertEquals(pastMeeting, actual);
    }

    /*
     * Test for convertToPastMeeting(Meeting aMeeting, String notes)
     * Starts here:
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

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfParameterForAFutureMeetingIsNullInConvertToFutureMeeting() {
        aMeetingContainer.convertToPastMeeting(null, "");
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfParameterForNotesIsNullInConvertToFutureMeeting() {
        aMeetingContainer.convertToPastMeeting(aFutureMeeting, null);
    }

    /*
     * Test for getMeetingIdListBy(Contact)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToGetMeetingIdListByContact() throws IllegalMeetingException {
        int id0 = 2;
        int id1 = 3;
        int id2 = 4;
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);

        int contactId = 3;
        String contactName = "Adam";
        String contactNotes = "Some notes about Adam...";
        Set<Contact> contactSet = new HashSet<>();

        Contact contact1 = contactMaker(contactId, contactName, contactNotes);
        contactSet.add(contact1);

        FutureMeeting futureMeeting = futureMeetingMaker(id0, date, contactSet);
        FutureMeeting futureMeeting1 = futureMeetingMaker(id1, date, contactSet);
        FutureMeeting futureMeeting2 = futureMeetingMaker(id2, date, contactSet);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id0);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(futureMeeting);
        aMeetingContainer.addFutureMeeting(contactSet, date);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id1);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(futureMeeting1);
        aMeetingContainer.addFutureMeeting(contactSet, date);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id2);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(futureMeeting2);
        aMeetingContainer.addFutureMeeting(contactSet, date);

        Set<Integer> expected = new HashSet<>();
        expected.add(id0);
        expected.add(id1);
        expected.add(id2);

        Set<Integer> meetingIds = aMeetingContainer.getMeetingIdListBy(contact1);

        assertEquals(expected, meetingIds);
    }

    @Test
    public void shouldHaveNoDuplicateMeetings() throws IllegalMeetingException {
        int id0 = 2;

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);

        int contactId = 3;
        String contactName = "Adam";
        String contactNotes = "Some notes about Adam...";
        Set<Contact> contactSet = new HashSet<>();

        Contact contact1 = contactMaker(contactId, contactName, contactNotes);
        contactSet.add(contact1);

        FutureMeeting futureMeeting = futureMeetingMaker(id0, date, contactSet);
        FutureMeeting futureMeeting1 = futureMeetingMaker(id0, date, contactSet);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id0);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(futureMeeting);
        aMeetingContainer.addFutureMeeting(contactSet, date);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id0);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(futureMeeting1);
        aMeetingContainer.addFutureMeeting(contactSet, date);

        Set<Integer> expected = new HashSet<>();
        expected.add(id0);
        Set<Integer> meetingIds = aMeetingContainer.getMeetingIdListBy(contact1);

        assertEquals(expected, meetingIds);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfParameterForContactIsNullInGetMeetingListByContact() {
        Contact contact = null;
        aMeetingContainer.getMeetingIdListBy(contact);
    }
    /*
     * Test for getMeetingIdListBy(Calendar date).
     * Starts here:
     */
    @Test
    public void shouldBeAbleToGetMeetingIdListByDate() throws IllegalMeetingException {
        int id0 = 2;
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);
        Set<Contact> contactSet = new HashSet<>();

        FutureMeeting futureMeeting = futureMeetingMaker(id0, date, contactSet);

        when(aUniqueNumberGeneratorUtilities.getUniqueNumber()).thenReturn(id0);
        when(aMeetingFactory.createFutureMeeting(anyInt(), eq(date), anySet())).thenReturn(futureMeeting);
        aMeetingContainer.addFutureMeeting(contactSet, date);

        Set<Integer> actual = aMeetingContainer.getMeetingIdListBy(date);
        Set<Integer> expected = new HashSet<>();
        expected.add(id0);

        assertEquals(expected, actual);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfParameterForDateIsNullInGetMeetingListByDate() {
        Calendar date = null;
        aMeetingContainer.getMeetingIdListBy(date);
    }

    /*
     * Helpers for tests
     */
    private Contact contactMaker(int contactId, String contactName, String contactNotes) {
        Contact contact1 = mock(Contact.class);
        when(contact1.getId()).thenReturn(contactId);
        when(contact1.getName()).thenReturn(contactName);
        when(contact1.getNotes()).thenReturn(contactNotes);

        return contact1;
    }

    private FutureMeeting futureMeetingMaker(int meetingId, Calendar date, Set<Contact> contactSet) {
        FutureMeeting futureMeeting = mock(FutureMeeting.class);
        when(futureMeeting.getId()).thenReturn(meetingId);
        when(futureMeeting.getDate()).thenReturn(date);
        when(futureMeeting.getContacts()).thenReturn(contactSet);

        return futureMeeting;
    }
}
