package com.codename1.moonpi.swiftnotes;

import com.codename1.io.Externalizable;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Simple business object representing a note
 *
 * @author Shai Almog
 */
public class Note implements Externalizable {
    private static ArrayList<Note> notes;
    
    private String title = "";
    private String body = "";
    private boolean bodyHidden;
    private boolean starred;
    private int color = 0xffffff;
    private float fontSize = 2;
    private boolean deleted;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the bodyHidden
     */
    public boolean isBodyHidden() {
        return bodyHidden;
    }

    /**
     * @param bodyHidden the bodyHidden to set
     */
    public void setBodyHidden(boolean bodyHidden) {
        this.bodyHidden = bodyHidden;
    }

    /**
     * @return the starred
     */
    public boolean isStarred() {
        return starred;
    }

    /**
     * @param starred the starred to set
     */
    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * @return the fontSize
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {
        Util.writeUTF(title, out);
        Util.writeUTF(body, out);
        out.writeBoolean(bodyHidden);
        out.writeBoolean(starred);
        out.writeInt(color);
        out.writeFloat(fontSize);
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        title = Util.readUTF(in);
        body = Util.readUTF(in);
        bodyHidden = in.readBoolean();
        starred = in.readBoolean();
        color = in.readInt();
        fontSize = in.readFloat();
    }

    @Override
    public String getObjectId() {
        return "Note";
    }
    
    public static ArrayList<Note> getNotes() {
        if(notes == null) {
            notes = (ArrayList<Note>)Storage.getInstance().readObject("notes");
            if(notes == null) {
                notes = new ArrayList<>();
            }
        }
        return notes;
    }
    
    public void saveNote() {
        if(!notes.contains(this)) {
            notes.add(this);
        }
        Storage.getInstance().writeObject("notes", notes);
    }
    
    public void delete() {
        notes.remove(this);
        deleted = true;
        Storage.getInstance().writeObject("notes", notes);
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
