package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

import java.util.ArrayList;
import java.util.regex.Pattern;

@SpirePatch(
        clz = AbstractAugment.class,
        method = "makeMatchers"
)
public class BetterMakeMatchersPatches {
    @SpirePostfixPatch
    public static ArrayList<String> Postfix(ArrayList<String> __result, String... inputs) {
        for (String s : inputs) {
            StringBuilder sb = new StringBuilder(s.length() + 2);
            sb.append(String.join(' ' + LocalizedStrings.PERIOD, s.split(Pattern.quote(LocalizedStrings.PERIOD))));
            if (s.endsWith(LocalizedStrings.PERIOD)) {
                sb.append(' ').append(LocalizedStrings.PERIOD);
            }
            String t1 = sb.toString();
            __result.add(t1);
            __result.add((" [diffRmvS] " + t1 + " [diffRmvE] ").replace("  ", " "));
            __result.add((" [diffAddS] " + t1 + " [diffAddE] ").replace("  ", " "));

            sb.insert(0, ' ');
            String t2 = sb.toString();
            __result.add(t2);
            __result.add((" [diffRmvS]" + t2 + " [diffRmvE] ").replace("  ", " "));
            __result.add((" [diffAddS]" + t2 + " [diffAddE] ").replace("  ", " "));
        }
        return __result;
    }
}
