package com.keimi.okamoto.meetingItem;

public class ContactImpl implements Contact {
    private int id;
    private String name;
    private String note;

    public ContactImpl(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Returns the ID of the contact.
     *
     * @return the ID of the contact.
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the contact.
     *
     * @return the name of the contact.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns our notes about the contact, if any.
     * If we have not written anything about the contact, the empty
     * string is returned. *
     *
     * @return a string with notes about the contact, maybe empty.
     */
    @Override
    public String getNotes() {
        return note;
    }

    /**
     * Add notes about the contact.
     *
     * @param note the notes to be added
     */
    @Override
    public void addNotes(String note) {
        this.note = note;
    }
}