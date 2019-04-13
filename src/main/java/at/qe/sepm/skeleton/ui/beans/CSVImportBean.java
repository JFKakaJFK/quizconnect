package at.qe.sepm.skeleton.ui.beans;

import com.opencsv.CSVReader;
//import org.primefaces.event.FileUploadEvent;
//import org.primefaces.model.UploadedFile;

import javax.annotation.ManagedBean;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
public class CSVImportBean {

    /**
     * Handles fileupload
     *
     * @param event
     */
    /*

    public void handleFileUpload(FileUploadEvent event){
        System.out.println("handleFileUpload");
        try {
            UploadedFile upload = event.getFile();
            addQuestionsFromCSV(upload.getInputstream());
        } catch (IOException e) {
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
            //        FacesMessage.SEVERITY_ERROR, "Error", "Die CSV Datei entspricht nicht dem verlangten Format.")
            e.printStackTrace();
            //);
        }
    }

    */

    /**
     * Uses a inputStream of a csv and converts to a String-Array
     *
     * @param inputStream
     * @return List of List of String ([[question1][correctAnswer][wrongAnswer][wrongAnswer][wrongAnswer]][[question2]...]
     */
    /*
    private List<List<String>> addQuestionsFromCSV(InputStream inputStream) {
        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
    */


}
