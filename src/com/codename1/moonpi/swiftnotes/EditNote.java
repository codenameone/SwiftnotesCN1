package com.codename1.moonpi.swiftnotes;

import com.codename1.components.InteractionDialog;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Slider;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.Style;

/**
 * Hand coded edit "form" that allows editing a specific note
 *
 * @author Shai Almog
 */
public class EditNote extends Form {
    private static final int[] COLORS = { 
        0x44a1eb, 0x77ddbb, 0xbbe535,
        0xeeee22, 0xffbb22, 0xf56545,
        0xff5997, 0xa767ff, 0xffffff
    };
    
    Command hideShowCommand;
    
    public EditNote(Note n, boolean isNew, ActivityMain parentForm) {
        super("", new BorderLayout());
        TextField title = new TextField(n.getTitle(), "Title", 20, TextArea.ANY);
        TextArea body = new TextArea(n.getBody());
        body.setHint("Note");
        title.getHintLabel().setUIID("NoteTitle");
        title.getHintLabel().getAllStyles().setFgColor(0xcccccc);
        add(BorderLayout.NORTH, title);
        add(BorderLayout.CENTER, body);
        title.setUIID("NoteTitle");
        body.setUIID("NoteBody");
        Font fnt = body.getUnselectedStyle().getFont();
        body.getAllStyles().setFont(fnt.derive(Display.getInstance().convertToPixels(n.getFontSize()), Font.STYLE_PLAIN));
        getContentPane().getUnselectedStyle().setBgTransparency(255);
        getContentPane().getUnselectedStyle().setBgColor(n.getColor());
        
        getToolbar().addMaterialCommandToRightBar("", FontImage.MATERIAL_PALETTE, e -> {
            Dialog colorPicker = new Dialog("dialog_note_colour");
            colorPicker.setDisposeWhenPointerOutOfBounds(true);
            colorPicker.setBackCommand("", null, ee -> colorPicker.dispose());
            colorPicker.setLayout(new GridLayout(3, 3));
            for(int iter = 0 ; iter < COLORS.length ; iter++) {
                Button choose = new Button("");
                Style s = choose.getAllStyles();
                s.setAlignment(Component.CENTER);
                int color = COLORS[iter];
                s.setBorder(RoundBorder.create().color(color));
                if(color == getContentPane().getUnselectedStyle().getBgColor()) {
                    FontImage.setMaterialIcon(choose, FontImage.MATERIAL_CHECK_CIRCLE, 3.5f);
                }
                choose.addActionListener(ee -> {
                    colorPicker.dispose();
                    getContentPane().getUnselectedStyle().setBgColor(color);
                    repaint();
                });
                colorPicker.add(choose);
            }
            colorPicker.showPacked(BorderLayout.CENTER, true);
        });
        
        getToolbar().setBackCommand("", e -> {
            n.setTitle(title.getText());
            n.setBody(body.getText());
            n.setColor(getContentPane().getUnselectedStyle().getBgColor());
            if(isNew) {
                if(Dialog.show("Save Changes", "", "Yes", "No")) {
                    n.saveNote();
                    parentForm.addNote(n);
                }
            } else {
                n.saveNote();
            }
            parentForm.showBack();
        });
        
        getToolbar().addMaterialCommandToRightBar("", FontImage.MATERIAL_FORMAT_SIZE, e -> {
            Slider s = new Slider();
            s.setMinValue(0);
            s.setMaxValue(50);
            s.setProgress(Math.round(n.getFontSize() * 10));
            s.setEditable(true);
            InteractionDialog id = new InteractionDialog();
            id.setUIID("Dialog");
            id.setLayout(new BorderLayout());
            id.add(BorderLayout.CENTER, s);
            s.addDataChangedListener((i, ii) -> {
                n.setFontSize(1 + ((float)s.getProgress()) / 10.0f); 
                body.getAllStyles().setFont(fnt.derive(Display.getInstance().convertToPixels(n.getFontSize()), Font.STYLE_PLAIN));
                body.repaint();
            });
            Button ok = new Button("OK");
            id.add(BorderLayout.EAST, ok);
            ok.addActionListener(ee -> id.dispose());
            id.show(getLayeredPane().getHeight() - ok.getPreferredH() * 2, 0, 0, 0);
        });
        
        addHideShowCommand(n);
    }
    
    void addHideShowCommand(Note n) {
        if(hideShowCommand != null) {
            removeCommand(hideShowCommand);
        }
        if(n.isBodyHidden()) {
            hideShowCommand = getToolbar().addMaterialCommandToRightBar("", FontImage.MATERIAL_VISIBILITY, e -> {
                    n.setBodyHidden(false);
                    addHideShowCommand(n);
                });
        } else {
            hideShowCommand = getToolbar().addMaterialCommandToRightBar("", FontImage.MATERIAL_VISIBILITY_OFF, e -> {
                    n.setBodyHidden(true);
                    addHideShowCommand(n);
                });
        }
        getToolbar().revalidate();
    }
}
