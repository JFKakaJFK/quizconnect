package at.qe.sepm.skeleton.ui.beans;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@ManagedBean
public class CSVImportBean {

    public void handleFileUpload(FileUploadEvent event){
        System.out.println("test");
        try {
            UploadedFile upload = event.getFile();
            addUsersCSV(upload.getInputstream());
        } catch (IOException e){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error", "Die CSV Datei entspricht nicht dem verlangtem Format.")
            );
        }
    }

    private void addUsersCSV(InputStream inputStream){
        try(BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            try {
                while((line = buf.readLine()) != null) {
                    String[] userData = line.split(",");

                    System.out.println("Wert 1:" + userData[0]);
                    System.out.println("Wert 2:" + userData[1]);
                    System.out.println("Wert 3:" + userData[2]);
                    System.out.println("Wert 4:" + userData[3]);
                    System.out.println("Wert 5:" + userData[4]);

                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_INFO, "Success", "Die Benutzer wurder erfolgreich hinzugefügt")
                );
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "Error", "Die CSV Datei entspricht nicht dem verlangtem Format.")
                );
            }

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error", "Fehler beim Benutzer hinzufügen")
            );
        }
    }

}
