package chimeracardsplus.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.NotFoundException;
import org.clapper.util.classutil.ClassFinder;

import java.io.File;
import java.net.URISyntaxException;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = "<ctor>"
)
public class DynamicPatchTrigger {
    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
        System.out.println("Starting dynamic patches.");
        ClassFinder finder = new ClassFinder();
        finder.add(new File(Loader.STS_JAR));
        for (ModInfo modInfo : Loader.MODINFOS) {
            if (modInfo.jarURL != null) {
                try {
                    finder.add(new File(modInfo.jarURL.toURI()));
                } catch (URISyntaxException ignored) {
                }
            }
        }
        ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
        CardModifierOnDiscardPatch.PostTriggerOnManualDiscardHook.patch(finder, pool);
        System.out.println("Dynamic patches complete.");
    }
}