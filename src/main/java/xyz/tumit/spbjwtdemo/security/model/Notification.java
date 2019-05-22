package xyz.tumit.spbjwtdemo.security.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Notification {
    private List<String> errors = new ArrayList<>();
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
}
