package com.keimi.okamoto.app.organisers;

import com.keimi.okamoto.app.items.*;
import com.keimi.okamoto.app.utils.DiskWriter;
import com.keimi.okamoto.app.utils.IllegalMeetingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Test for ContactManager
 *
 * Using the mocking framework test is made simpler
 * first verify, verifies the number of times the
 * getContacts() is being called 4 times ensuring that
 * 4 elements have been added.
 */
public class ContactManagerTest {
    private String notes;
    private String name;
    private ContactsContainer aContactContainer;
    private ContactManager aContactManager;
    private Contact aContact;
    private MeetingContainer aMeetingContainer;
    private FutureMeeting aFutureMeeting;
    private Meeting aMeeting;
    private PastMeeting aPastMeeting;
    private DiskWriter aDiskWriter;

    /**
     * Builds up a new ContactsContainerImpl
     */
    @Before
    public void buildUp() {
        aMeetingContainer = mock(MeetingContainer.class);
        aContactContainer = mock(ContactsContainer.class);
        aFutureMeeting = mock(FutureMeeting.class);
        aContact = mock(Contact.class);
        aMeeting = mock(Meeting.class);
        aPastMeeting = mock(PastMeeting.class);
        aDiskWriter = mock(DiskWriter.class);

        aContactManager = new ContactManagerImpl(aContactContainer, aMeetingContainer, aDiskWriter);
        notes = "Some notes go here";
        name = "Adam";
    }

    /*
     * Test for addNewContact()
     * Starts here:
     */
    @Test
    public void shouldBeAbleToAddNewContact() {
        aContactManager.addNewContact(name, notes);
        verify(aContactContainer).addContact(name, notes);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfNameIsEmpty() throws NullPointerException {
        aContactManager.addNewContact(null, notes);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfNotesIsNull() throws NullPointerException {
        aContactManager.addNewContact(name, null);
    }

    /*
     * Test for getContacts(int... ids)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToGetSetOfContactsDependingOnVariableNumberOdIds() {
        when(aContactContainer.checkForValidId(Matchers.<int[]>anyVararg())).thenReturn(true);
        when(aContactContainer.getContact(anyInt())).thenReturn(mock(Contact.class), mock(Contact.class), mock(Contact.class), mock(Contact.class));

        Set<Contact> contactSet = aContactManager.getContacts(1, 2, 3, 4);
        verify(aContactContainer, times(4)).getContact(anyInt());

        assertEquals(contactSet.size(), 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfIdIsNotValid() {
        Integer num = 26;
        when(aContactContainer.checkForValidId(anyInt())).thenReturn(false);
        aContactManager.getContacts(num);
    }

    /*
     * Test for getContacts(String name)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToGetSetOfContactsByName() {
        when(aContactContainer.checkForValidName(anyString())).thenReturn(true);

        Set<Contact> expected = new HashSet<>();
        when(aContactManager.getContacts(anyString())).thenReturn(expected);
        Set<Contact> actual = aContactManager.getContacts(name);
        verify(aContactContainer).getContacts(name);

        assertEquals(expected, actual);
    }


    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfParameterIsNull() {
        String name = null;
        when(aContactContainer.checkForValidName(anyString())).thenReturn(false);
        aContactManager.getContacts(name);
    }

    /*
    * Test for addFutureMeeting(Set contacts, Calendar date)
    * Starts here:
    */
    @Test
    public void shouldBeAbleToAddFutureMeeting() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();
        when(aMeetingContainer.checkForFuture(date)).thenReturn(true);
        when(aContactContainer.checkForValidSetOfContacts(anySet())).thenReturn(true);

        aContactManager.addFutureMeeting(aSetOfContacts, date);
        verify(aMeetingContainer).addFutureMeeting(aSetOfContacts, date);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfDateIsInThePast() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -1);

        when(aMeetingContainer.checkForFuture(date)).thenReturn(false);
        aContactManager.addFutureMeeting(aSetOfContacts, date);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfContactIsUnknownInSet() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();

        when(aContactContainer.checkForValidSetOfContacts(aSetOfContacts)).thenReturn(false);
        aContactManager.addFutureMeeting(aSetOfContacts, date);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfContactDoesNotExistInSet() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();

        when(aContactContainer.checkForValidSetOfContacts(aSetOfContacts)).thenReturn(false);
        aContactManager.addFutureMeeting(null, date);
    }

    /*
     * Test for FutureMeeting getFutureMeeting(int id)
     * Starts here:
     */
    @Test
    public void shouldReturnTheFutureMeetingWithTheRequestedId() {
        int id = 1;
        Calendar date = Calendar.getInstance();

        when(aMeetingContainer.getFutureMeeting(anyInt())).thenReturn(aFutureMeeting);
        when(aMeetingContainer.checkForPast(date)).thenReturn(false);

        FutureMeeting actualFutureMeeting = aContactManager.getFutureMeeting(id);

        assertEquals(aFutureMeeting, actualFutureMeeting);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfThereIsAMeetingWithThatIdHappeningInThePast() {
        Calendar date = Calendar.getInstance();

        when(aFutureMeeting.getDate()).thenReturn(date);
        when(aMeetingContainer.getFutureMeeting(anyInt())).thenReturn(aFutureMeeting);
        when(aMeetingContainer.checkForPast(date)).thenReturn(true);
        aContactManager.getFutureMeeting(1);
    }

    @Test
    public void shouldReturnNullIfIdDoesNotCorrespondWithFutureMeeting() {
        int id = 1;

        when(aMeetingContainer.getFutureMeeting(anyInt())).thenReturn(null);
        FutureMeeting actualFutureMeeting = aContactManager.getFutureMeeting(id);

        assertEquals(null, actualFutureMeeting);

    }

    /*
     * Test for getMeeting(int id)
     * Starts here:
     */
    @Test
    public void shouldReturnMeetingWithTheRequestedId() {
        int id = 1;
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(aMeeting);
        Meeting actualMeeting = aContactManager.getMeeting(id);
        verify(aMeetingContainer).getMeeting(id);

        assertEquals(aMeeting, actualMeeting);
    }

    @Test
    public void shouldReturnNullIfIdDoesNotCorrespondWithMeeting() {
        int id = 1;
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(null);
        Meeting actualMeeting = aContactManager.getMeeting(id);
        verify(aMeetingContainer).getMeeting(id);

        assertEquals(null, actualMeeting);
    }

    /*
     * Tests For addNewPastMeeting()
     * Starts here:
     */
    @Test
    public void shouldBeAbleToAddNewPastMeeting() throws IllegalMeetingException {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -1);

        aSetOfContacts.add(aContact);
        when(aMeetingContainer.checkForPast(date)).thenReturn(true);
        when(aContactContainer.checkForValidSetOfContacts(aSetOfContacts)).thenReturn(true);

        aContactManager.addNewPastMeeting(aSetOfContacts, date, notes);
        verify(aMeetingContainer).addPastMeeting(anySet(), eq(date), anyString());
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfListOfContactsIsEmpty() {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();

        aContactManager.addNewPastMeeting(aSetOfContacts, date , notes);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfContactDoesNotExist() {
        Set<Contact> aSetOfContacts = new HashSet<>();
        Calendar date = Calendar.getInstance();

        when(aContactContainer.checkForValidSetOfContacts(anySet())).thenReturn(false);
        aContactManager.addNewPastMeeting(aSetOfContacts, date, notes);
    }

    @Test (expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfSetOfContactsArgumentIsNull () {
        Calendar date = Calendar.getInstance();

        when(aContactContainer.checkForValidName(anyString())).thenReturn(false);
        aContactManager.addNewPastMeeting(null, date, notes);
    }

    @Test (expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfDateArgumentIsNull () {
        Set<Contact> aSetOfContacts = new HashSet<>();

        when(aContactContainer.checkForValidName(anyString())).thenReturn(false);
        aContactManager.addNewPastMeeting(aSetOfContacts, null, notes);
    }

    @Test (expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfNotesArgumentIsNull () {
        Calendar date = Calendar.getInstance();
        Set<Contact> aSetOfContacts = new HashSet<>();

        when(aContactContainer.checkForValidName(anyString())).thenReturn(false);
        aContactManager.addNewPastMeeting(aSetOfContacts, date, null);
    }

    /*
     * Test for getPastMeeting() starts here
     */
    @Test
    public void shouldReturnPastMeetingWithTheRequestedId() {
        int id = 1;
        when(aMeetingContainer.getPastMeeting(anyInt())).thenReturn(aPastMeeting);

        PastMeeting actualPastMeeting = aContactManager.getPastMeeting(id);
        verify(aMeetingContainer).getPastMeeting(id);

        assertEquals(aPastMeeting, actualPastMeeting);
    }

    @Test
    public void shouldReturnNullIfIdDoesNotCorrespondAPastMeeting() {
        int id = 1;
        when(aMeetingContainer.getPastMeeting(anyInt())).thenReturn(null);

        PastMeeting actualPastMeeting = aContactManager.getPastMeeting(id);
        verify(aMeetingContainer).getPastMeeting(id);

        assertEquals(null, actualPastMeeting);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfThereIsAMeetingWithThatIdHappeningInTheFuture() {
        int id = 0;
        Calendar date = Calendar.getInstance();

        when(aPastMeeting.getDate()).thenReturn(date);
        when(aMeetingContainer.getPastMeeting(anyInt())).thenReturn(aPastMeeting);
        when(aMeetingContainer.checkForFuture(eq(date))).thenReturn(true);
        aContactManager.getPastMeeting(id);
    }

    /*
     * Test for addMeetingNotes()
     * Starts here:
     */
    @Test
    public void shouldBeAbleToAddMeetingNotes() {
        Calendar date = Calendar.getInstance();
        String notes = "Some notes...";

        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(aFutureMeeting);
        when(aMeetingContainer.checkForFuture(eq(date))).thenReturn(false);
        when(aFutureMeeting.getDate()).thenReturn(date);

        aContactManager.addMeetingNotes(0, notes);
        verify(aMeetingContainer).convertToPastMeeting(aFutureMeeting, notes);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTheMeetingDoesNotExist() {
        String notes = "Some notes...";

        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(null);
        aContactManager.addMeetingNotes(0, notes);
    }

    @Test (expected = IllegalStateException.class)
    public void  IllegalStateExceptionIfTheMeetingIsSetForADateInTheFuture() {
        Calendar date = Calendar.getInstance();
        String notes = "Some notes...";

        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(aFutureMeeting);
        when(aMeetingContainer.checkForFuture(eq(date))).thenReturn(true);
        when(aFutureMeeting.getDate()).thenReturn(date);

        aContactManager.addMeetingNotes(0, notes);
    }

    @Test (expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfTheNotesAreNull() {
        aContactManager.addMeetingNotes(0, null);
    }

    /*
     * Test for getFutureMeetingList(Contact contact)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToReturnListOfFutureMeetingsScheduledWithThisContact() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);
        Set<Contact> contactSet = new HashSet<>();
        Set<Integer> meetingIds = new HashSet<>();
        meetingIds.add(1);
        meetingIds.add(2);
        meetingIds.add(3);
        meetingIds.add(4);

        FutureMeeting fm1 = futureMeetingMaker(1, date, contactSet);
        FutureMeeting fm2 = futureMeetingMaker(2, date, contactSet);
        FutureMeeting fm3 = futureMeetingMaker(3, date, contactSet);
        FutureMeeting fm4 = futureMeetingMaker(4, date, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1,fm2,fm3,fm4);

        List<FutureMeeting> expected = Arrays.asList(fm1,fm2, fm3, fm4);

        List<FutureMeeting> actual = (List<FutureMeeting>) (List<?>) aContactManager.getFutureMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,times(4)).getMeeting(anyInt());
    }

    @Test
    public void shouldBeAbleToReturnListOfOnlyFutureMeetingsScheduledWithThisContact() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);

        Set<Integer> meetingIds = new HashSet<>();
        meetingIds.add(1);
        meetingIds.add(2);
        meetingIds.add(3);
        meetingIds.add(4);

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);
        Set<Contact> contactSet = new HashSet<>();
        FutureMeeting fm1 = futureMeetingMaker(1, date, contactSet);
        FutureMeeting fm2 = futureMeetingMaker(2, date, contactSet);
        FutureMeeting fm4 = futureMeetingMaker(3, date, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1,fm2,null,fm4);

        List<FutureMeeting> expected = Arrays.asList(fm1,fm2,fm4);

        List<FutureMeeting> actual = (List<FutureMeeting>) (List<?>) aContactManager.getFutureMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,times(4)).getMeeting(anyInt());
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTheContactDoesNotExist() throws IllegalMeetingException {
        when(aContactContainer.checkForValidName(anyString())).thenReturn(false);
        aContactManager.getFutureMeetingList(aContact);
    }

    @Test
    public void shouldReturnListOfFutureMeetingsInChronologicalOrder() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);

        Set<Integer> meetingIds = new HashSet<>();
        meetingIds.add(1);
        meetingIds.add(2);
        meetingIds.add(3);
        meetingIds.add(4);
        meetingIds.add(5);

        Calendar date1 = Calendar.getInstance();
        Calendar date2= Calendar.getInstance();
        Calendar date3 = Calendar.getInstance();
        Calendar date4 = Calendar.getInstance();
        Calendar date5 = Calendar.getInstance();
        date1.add(Calendar.DATE, 3);
        date2.add(Calendar.DATE, 1);
        date3.add(Calendar.DATE, 2);
        date4.add(Calendar.DATE, 5);
        date5.add(Calendar.DATE, 4);

        Set<Contact> contactSet = new HashSet<>();

        FutureMeeting fm1 = futureMeetingMaker(1, date1, contactSet);
        FutureMeeting fm2 = futureMeetingMaker(1, date2, contactSet);
        FutureMeeting fm3 = futureMeetingMaker(1, date3, contactSet);
        FutureMeeting fm4 = futureMeetingMaker(1, date4, contactSet);
        FutureMeeting fm5 = futureMeetingMaker(1, date5, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1,fm2,fm3,fm4,fm5);

        List<FutureMeeting> expected = Arrays.asList(fm2,fm3,fm1,fm5,fm4);
        List<FutureMeeting> actual = (List<FutureMeeting>) (List<?>) aContactManager.getFutureMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,times(5)).getMeeting(anyInt());
    }

    @Test
    public void shouldReturnAnEmptyListIfMeetingIdListIsEmptyForFutureMeeting() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);

        Set<Integer> meetingIds = null;

        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, -1);
        Set<Contact> contactSet = new HashSet<>();
        FutureMeeting fm1 = futureMeetingMaker(1, date1, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1);

        List<FutureMeeting> expected = Arrays.asList();
        List<FutureMeeting> actual = (List<FutureMeeting>) (List<?>) aContactManager.getFutureMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,never()).getMeeting(anyInt());
    }

    /*
     * Test for getPastMeetingList(Contact contact)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToReturnListPastMeetingsScheduledWithThisContact() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);
        String notes = "Some notes go here...";
        Set<Contact> contactSet = new HashSet<>();

        Set<Integer> meetingIds = new HashSet<>();
        meetingIds.add(1);
        meetingIds.add(2);
        meetingIds.add(3);
        meetingIds.add(4);

        int id1 = 1;
        int id2 = 2;
        int id3 = 3;
        int id4 = 4;
        PastMeeting pm1 = pastMeetingMaker(id1, date, notes, contactSet);
        PastMeeting pm2 = pastMeetingMaker(id2, date, notes, contactSet);
        PastMeeting pm3 = pastMeetingMaker(id3, date, notes, contactSet);
        PastMeeting pm4 = pastMeetingMaker(id4, date, notes, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(pm1,pm2,pm3,pm4);

        List<PastMeeting> expected = Arrays.asList(pm1,pm2, pm3, pm4);

        List<PastMeeting> actual = (List<PastMeeting>) (List<?>) aContactManager.getPastMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,times(4)).getMeeting(anyInt());
    }

    @Test
    public void shouldBeAbleToReturnListOfOnlyPastMeetingsScheduledWithThisContact() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);

        Set<Integer> meetingIds = new HashSet<>();
        meetingIds.add(1);
        meetingIds.add(2);
        meetingIds.add(3);
        meetingIds.add(4);

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);
        String notes = "Some notes go here...";
        Set<Contact> contactSet = new HashSet<>();
        PastMeeting fm1 = pastMeetingMaker(1, date, notes, contactSet);
        PastMeeting fm2 = pastMeetingMaker(2, date, notes, contactSet);
        PastMeeting fm4 = pastMeetingMaker(3, date, notes, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1,fm2,null,fm4);

        List<PastMeeting> expected = Arrays.asList(fm1,fm2,fm4);
        List<PastMeeting> actual = aContactManager.getPastMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,times(4)).getMeeting(anyInt());
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTheContactDoesNotExistInPastMeeting() throws IllegalMeetingException {
        when(aContactContainer.checkForValidName(anyString())).thenReturn(false);
        aContactManager.getPastMeetingList(aContact);
    }

    /*
     * Test for getPastMeetingList(Contact contact)
     * Starts here:
     */
    @Test
    public void shouldReturnListOfPastMeetingsInChronologicalOrder() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);

        Set<Integer> meetingIds = new HashSet<>();
        meetingIds.add(1);
        meetingIds.add(2);
        meetingIds.add(3);
        meetingIds.add(4);
        meetingIds.add(5);

        Calendar date1 = Calendar.getInstance();
        Calendar date2= Calendar.getInstance();
        Calendar date3 = Calendar.getInstance();
        Calendar date4 = Calendar.getInstance();
        Calendar date5 = Calendar.getInstance();
        date1.add(Calendar.DATE, -3);
        date2.add(Calendar.DATE, -1);
        date3.add(Calendar.DATE, -2);
        date4.add(Calendar.DATE, -5);
        date5.add(Calendar.DATE, -4);

        Set<Contact> contactSet = new HashSet<>();
        String notes = "Some notes go here...";

        PastMeeting pm1 = pastMeetingMaker(1, date1, notes, contactSet);
        PastMeeting pm2 = pastMeetingMaker(2, date2, notes, contactSet);
        PastMeeting pm3 = pastMeetingMaker(3, date3, notes, contactSet);
        PastMeeting pm4 = pastMeetingMaker(4, date4, notes, contactSet);
        PastMeeting pm5 = pastMeetingMaker(5, date5, notes, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(pm1,pm2,pm3,pm4,pm5);

        List<PastMeeting> expected = Arrays.asList(pm4,pm5,pm1,pm3,pm2);
        List<PastMeeting> actual = aContactManager.getPastMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,times(5)).getMeeting(anyInt());
    }

    @Test
    public void shouldReturnAnEmptyListIfMeetingIdListIsEmptyForPastMeeting() {
        when(aContactContainer.checkForValidName(aContact.getName())).thenReturn(true);

        Set<Integer> meetingIds = null;

        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, -1);
        Set<Contact> contactSet = new HashSet<>();
        String notes = "Some notes...";
        PastMeeting fm1 = pastMeetingMaker(1, date1, notes, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(aContact))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1);

        List<PastMeeting> expected = Arrays.asList();
        List<PastMeeting> actual = aContactManager.getPastMeetingList(aContact);

        assertEquals(expected, actual);
        verify(aMeetingContainer,never()).getMeeting(anyInt());
    }

    /*
     * Test for getFutureMeetingList(Date aDate)
     * Starts here:
     */
    @Test
    public void shouldBeAbleToReturnListOfOnlyFutureMeetingsAccordingToDate() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 2);
        Set<Contact> contactSet = new HashSet<>();
        Set<Integer> meetingIds = new HashSet<>();
        meetingIds.add(1);
        meetingIds.add(2);
        meetingIds.add(3);
        meetingIds.add(4);

        FutureMeeting fm1 = futureMeetingMaker(1, date, contactSet);
        FutureMeeting fm2 = futureMeetingMaker(2, date, contactSet);
        FutureMeeting fm3 = futureMeetingMaker(3, date, contactSet);
        FutureMeeting fm4 = futureMeetingMaker(4, date, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(date))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1,fm2,fm3,fm4);

        List<FutureMeeting> expected = Arrays.asList(fm1,fm2, fm3, fm4);
        List<FutureMeeting> actual = (List<FutureMeeting>) (List<?>) aContactManager.getFutureMeetingList(date);

        assertEquals(expected, actual);
        verify(aMeetingContainer,times(4)).getMeeting(anyInt());
    }

    @Test
    public void shouldReturnAnEmptyListIfMeetingIdListIsEmptyForFutureMeetingAccordingToDate() {

        Set<Integer> meetingIds = null;

        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, -1);
        Set<Contact> contactSet = new HashSet<>();
        FutureMeeting fm1 = futureMeetingMaker(1, date1, contactSet);

        when(aMeetingContainer.getMeetingIdListBy(eq(date1))).thenReturn(meetingIds);
        when(aMeetingContainer.getMeeting(anyInt())).thenReturn(fm1);

        List<FutureMeeting> expected = Arrays.asList();
        List<FutureMeeting> actual = (List<FutureMeeting>) (List<?>) aContactManager.getFutureMeetingList(date1);

        assertEquals(expected, actual);
        verify(aMeetingContainer,never()).getMeeting(anyInt());
    }

    /*
     * Test for flush()
     * Starts here:
     */
    @Test
    public void shouldBeAbleToWriteToDisk() {
        aContactManager.flush();
        verify(aDiskWriter).writeToDisk(eq(aContactContainer), eq(aMeetingContainer));
    }

    /*
     * Helper for test.
     * Makes a past meeting.
     */
    private PastMeeting pastMeetingMaker(int id, Calendar date, String notes, Set<Contact> contactSet) {
        PastMeeting pastMeeting = mock(PastMeeting.class);
        when(pastMeeting.getId()).thenReturn(id);
        when(pastMeeting.getContacts()).thenReturn(contactSet);
        when(pastMeeting.getNotes()).thenReturn(notes);
        when(pastMeeting.getDate()).thenReturn(date);

        return pastMeeting;
    }

    /*
     * Helper for test.
     * Makes a future meeting.
     */
    private FutureMeeting futureMeetingMaker(int meetingId, Calendar date, Set<Contact> contactSet) {
        FutureMeeting futureMeeting = mock(FutureMeeting.class);
        when(futureMeeting.getId()).thenReturn(meetingId);
        when(futureMeeting.getDate()).thenReturn(date);
        when(futureMeeting.getContacts()).thenReturn(contactSet);

        return futureMeeting;
    }
}
