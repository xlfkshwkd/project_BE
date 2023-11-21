package org.simsimhi.commons;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;

public class Utils {
    private static ResourceBundle validationsBundle;
    private static ResourceBundle errorsBundle;

    static {
        validationsBundle = ResourceBundle.getBundle("messages.validations");
        errorsBundle = ResourceBundle.getBundle("messages.errors");
    }

    public static String getMessage(String code, String bundleType) {
        bundleType = Objects.requireNonNullElse(bundleType, "validation");
        ResourceBundle bundle = bundleType.equals("error")? errorsBundle:validationsBundle;
        try {
            return bundle.getString(code);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> getMessages(Errors errors) {
        return errors.getFieldErrors()
                .stream()
                .flatMap(f -> Arrays.stream(f.getCodes()).sorted(Comparator.reverseOrder())
                        .map(c -> getMessage(c, "validation")))
                .filter(s -> s != null && !s.isBlank()).toList();
    }

    public Map<String ,List <String>>getMessages(Errors errors,String bundleType){
        bundleType =Objects.requireNonNullElse(bundleType,"validation");
        ResourceBundle bundle =bundleType.equals("error")?errorsBundle:validationsBundle;

        Map<String,List<String>>data =new HashMap<>();
        for(FieldError error :errors.getFieldErrors()){
            String field =error.getField();
          List<String> messages = Arrays.stream(error.getCodes()).sorted(Comparator.reverseOrder())
                    .map(c->getMessage(c, "validation"))
                    .filter(c ->c !=null)
                    .toList();
                    //NotBlank email
            data.put(field,messages);
        }
        return data;
    }
}