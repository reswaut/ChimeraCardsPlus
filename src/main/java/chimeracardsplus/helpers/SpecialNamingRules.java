package chimeracardsplus.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SpecialNamingRules {
    public Map<String, Map<String, String>> NAME_DICT = null;

    public void addRules(SpecialNamingRules newRules) {
        if (NAME_DICT == null) {
            NAME_DICT = new HashMap<>(512);
            NAME_DICT.putAll(newRules.NAME_DICT);
            return;
        }
        for (Entry<String, Map<String, String>> entry : newRules.NAME_DICT.entrySet()) {
            Map<String, String> rule = NAME_DICT.get(entry.getKey());
            if (rule == null) {
                NAME_DICT.put(entry.getKey(), entry.getValue());
            } else {
                rule.putAll(entry.getValue());
            }
        }
    }
}
