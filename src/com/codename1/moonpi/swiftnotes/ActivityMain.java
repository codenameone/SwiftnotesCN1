package com.codename1.moonpi.swiftnotes;

import com.codename1.components.FloatingActionButton;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.util.ArrayList;


public class ActivityMain extends Form  {
    public ActivityMain(com.codename1.ui.util.Resources resourceObjectInstance) {
        super("app_name");
        initGuiBuilderComponents(resourceObjectInstance);
        setBackCommand(new Command("") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Display.getInstance().minimizeApplication();
            }
        });
        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.bindFabToContainer(getContentPane());
        fab.addActionListener(e -> {
            Note n = new Note();
            new EditNote(n, true, ActivityMain.this).show();
        });
        
        ArrayList<Note> notes = Note.getNotes();
        if(notes.size() > 0) {
            removeAll();
            for(Note n : notes) {
                addNote(n);
            }
        }
        
        getToolbar().addSearchCommand(e -> search((String)e.getSource()));
        /*getToolbar().addCommandToOverflowMenu("Backup Notes", null, e -> Log.p("Todo"));
        getToolbar().addCommandToOverflowMenu("Restore Notes", null, e -> Log.p("Todo"));
        getToolbar().addCommandToOverflowMenu("Rate App", null, e -> Log.p("Todo"));*/
    }

    void search(String text) {
        if(text == null || text.length() == 0) {
            for(Component c : getContentPane()) {
                c.setHidden(false);
                c.setVisible(true);
            }
        } else {
            for(Component c : getContentPane()) {
                Note n = (Note)c.getClientProperty("note");
                text = text.toLowerCase();
                boolean show = n.getTitle().toLowerCase().indexOf(text) > -1 || n.getBody().toLowerCase().indexOf(text) > -1;
                c.setHidden(!show);
                c.setVisible(show);
            }
        }
        getContentPane().animateLayout(200);
    }
    
    /**
     * This is invoked from the edit note form to add a new note to the UI
     */
    public void addNote(Note n) {
        // if this is the first note ever added we need to remove the placeholder
        gui_noNotes.remove();
        add(createNoteCnt(n));
    }

    /**
     * This is used internally to update the note after it was edited with a new widget
     */
    private Container createNoteCnt(Note n) {
        Button title = new Button(n.getTitle());
        title.setUIID("NoteTitle");
        CheckBox star = CheckBox.createToggle("");
        star.setUIID("NoteTitle");
        FontImage.setMaterialIcon(star, FontImage.MATERIAL_STAR_BORDER, 4);
        star.setPressedIcon(FontImage.createMaterial(FontImage.MATERIAL_STAR, "NoteTitle", 4));
        star.setSelected(n.isStarred());
        star.setBlockLead(true);
        star.addActionListener(e -> {
            n.setStarred(star.isSelected());
            n.saveNote();
        });
        Container cnt;
        if(!n.isBodyHidden()) {
            TextArea body = new TextArea(n.getBody());
            body.getAllStyles().setBgColor(n.getColor());
            body.setUIID("NoteBody");
            body.setEditable(false);
            Font fnt = body.getUnselectedStyle().getFont();
            body.getAllStyles().setFont(fnt.derive(Display.getInstance().convertToPixels(n.getFontSize()), Font.STYLE_PLAIN));
            cnt = BorderLayout.center(
                        BoxLayout.encloseY(title, body)
                    ).add(BorderLayout.EAST, star);
        } else {
            cnt = BorderLayout.center(title).
                            add(BorderLayout.EAST, star);
        }        
        cnt.setLeadComponent(title);
        cnt.getAllStyles().setBgTransparency(255);
        cnt.getAllStyles().setBgColor(n.getColor());
        Button delete = new Button("");
        FontImage.setMaterialIcon(delete, FontImage.MATERIAL_DELETE, 4);
        SwipeableContainer sc = new SwipeableContainer(delete, cnt);
        delete.addActionListener(e -> {
            n.delete();
            sc.close();
            sc.remove();
            getContentPane().animateLayout(800);            
        });
        title.addActionListener(e -> {
            if(!n.isDeleted()) {
                new EditNote(n, false, this).show();
                addShowListener(ee -> {
                    getContentPane().replace(sc, createNoteCnt(n), null);
                    removeAllShowListeners();
                });
            }
        });
        sc.putClientProperty("note", n);
        return sc;
    }
    
//-- DON'T EDIT BELOW THIS LINE!!!
    private com.codename1.ui.Label gui_noNotes = new com.codename1.ui.Label();


// <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initGuiBuilderComponents(com.codename1.ui.util.Resources resourceObjectInstance) {
        setLayout(new com.codename1.ui.layouts.BoxLayout(com.codename1.ui.layouts.BoxLayout.Y_AXIS));
        setInlineStylesTheme(resourceObjectInstance);
                setInlineStylesTheme(resourceObjectInstance);
        setName("ActivityMain");
        addComponent(gui_noNotes);
        gui_noNotes.setText("no_notes_text");
        gui_noNotes.setUIID("AndroidLabel12");
                gui_noNotes.setInlineStylesTheme(resourceObjectInstance);
        gui_noNotes.setName("noNotes");
    }// </editor-fold>

//-- DON'T EDIT ABOVE THIS LINE!!!
}
