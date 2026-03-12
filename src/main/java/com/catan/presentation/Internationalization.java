package com.catan.presentation;

import java.util.Locale;
import java.util.ResourceBundle;

public class Internationalization {

    private Locale locale;
    private ResourceBundle resourceBundle;

    public Internationalization() {
        locale = Locale.getDefault();
        //locale  = new Locale("es", "MX");

        resourceBundle = ResourceBundle.getBundle("gameText.gameText", locale);
    }

    public String getText(String string){
        return resourceBundle.getString(string);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        resourceBundle = ResourceBundle.getBundle("gameText.gameText", locale);
    }


}
