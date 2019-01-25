package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Map;

public class TextFieldListener implements ChangeListener<String> {

    private final TextField textField ;
    private final String regex;
    private final boolean obligatory;
    @FXML
    private Button save_button;
    private static final String STYLE_ONE = "-fx-border-color: red ; -fx-border-width: 1px ;";
    private static final String STYLE_TWO = "-fx-border-color: none ;";
    private Map<TextField,Boolean> map;

    public TextFieldListener(TextField textField, String regex, boolean obligatory, Map<TextField, Boolean> map, Button save) {
        this.textField = textField;
        this.regex=regex;
        this.obligatory=obligatory;
        this.map = map;
        this.save_button = save;
        if (obligatory) {
            map.put(textField, false);
        }
    }
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if(newValue!=null){
            if(!newValue.matches(regex) || (obligatory && newValue.trim().isEmpty())){
                textField.setStyle(STYLE_ONE);
                map.put(textField, false);
            }else{
                textField.setStyle(STYLE_TWO);
                map.put(textField, true);
            }
            if (map.containsValue(false)){
                save_button.setDisable(true);
            }else {
                save_button.setDisable(false);
            }
        }
    }
}