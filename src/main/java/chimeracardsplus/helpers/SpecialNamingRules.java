package chimeracardsplus.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SpecialNamingRules {
    public Map<String, Map<String, SpecialName>> NAME_DICT = null;

    public void addRules(SpecialNamingRules newRules) {
        if (newRules == null) {
            return;
        }
        if (NAME_DICT == null) {
            NAME_DICT = new HashMap<>(Constants.EXPECTED_MODIFIERS);
            NAME_DICT.putAll(newRules.NAME_DICT);
            return;
        }
        for (Entry<String, Map<String, SpecialName>> entry : newRules.NAME_DICT.entrySet()) {
            Map<String, SpecialName> rule = NAME_DICT.get(entry.getKey());
            if (rule == null) {
                NAME_DICT.put(entry.getKey(), entry.getValue());
            } else {
                rule.putAll(entry.getValue());
            }
        }
    }

    public static class SpecialName {
        public String NAME = null;
        public String DESCRIPTION = null;
    }
}
