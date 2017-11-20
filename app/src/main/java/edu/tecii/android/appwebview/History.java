package edu.tecii.android.appwebview;

import java.io.Serializable;

/**
 * Created by Adrian on 19/11/2017.
 */

public class History implements Serializable {

    String pagina, enlace;

    public History(String Pagina, String Enlace){
        this.pagina = Pagina;
        this.enlace = Enlace;
    }
}
