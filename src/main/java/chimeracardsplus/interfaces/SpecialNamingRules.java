package chimeracardsplus.interfaces;

import java.util.HashMap;
import java.util.Map;

public class SpecialNamingRules {
    public Map<String, Map<String, String>> NAME_DICT;

    public void addRules(SpecialNamingRules newRules) {
        if (NAME_DICT == null) {
            NAME_DICT = new HashMap<>();
            NAME_DICT.putAll(newRules.NAME_DICT);
            return;
        }
        for (Map.Entry<String, Map<String, String>> entry : newRules.NAME_DICT.entrySet()) {
            Map<String, String> rule = NAME_DICT.get(entry.getKey());
            if (rule == null) {
                NAME_DICT.put(entry.getKey(), entry.getValue());
            } else {
                rule.putAll(entry.getValue());
            }
        }
    }
}
