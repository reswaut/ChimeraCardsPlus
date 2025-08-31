package chimeracardsplus.patches;

import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.Constants;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.*;
import org.clapper.util.classutil.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = SpirePatch.CONSTRUCTOR
)
public class DynamicPatchTrigger {
    private static final ClassFinder allModsFinder;
    private static final ClassFinder stsFinder;
    private static final ClassFilter abstractCardSubClassFilter;
    private static final ClassFilter nonInterfaceAbstractCardSubclassFilter;

    static {
        File sts = new File(Loader.STS_JAR);

        allModsFinder = new ClassFinder();
        allModsFinder.add(sts);
        for (ModInfo modInfo : Loader.MODINFOS) {
            if (modInfo.jarURL != null && !ChimeraCardsPlus.MOD_ID.equals(modInfo.ID)) {
                try {
                    allModsFinder.add(new File(modInfo.jarURL.toURI()));
                } catch (URISyntaxException e) {
                    ChimeraCardsPlus.logger.error("Could not load jar {}", modInfo.jarURL, e);
                }
            }
        }

        stsFinder = new ClassFinder();
        stsFinder.add(sts);

        abstractCardSubClassFilter = new SubclassClassFilter(AbstractCard.class);

        nonInterfaceAbstractCardSubclassFilter = new AndClassFilter(new NotClassFilter(new InterfaceOnlyClassFilter()), abstractCardSubClassFilter);
    }

    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
        ChimeraCardsPlus.logger.info("Dynamic patches started.");
        ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
        applyAbstractCardPatches(pool);
        applyBaseGameCardPatches(pool);
        ChimeraCardsPlus.logger.info("Dynamic patches complete.");
    }

    private static void applyAbstractCardPatches(ClassPool pool) throws NotFoundException, CannotCompileException {
        ChimeraCardsPlus.logger.info("- Dynamic AbstractCard patches started.");
        ArrayList<ClassInfo> clzList = new ArrayList<>(Constants.EXPECTED_CARDS);
        allModsFinder.findClasses(clzList, abstractCardSubClassFilter);
        ChimeraCardsPlus.logger.info("- Potential targets found for dynamic AbstractCard patches ({}).", clzList.size());
        for (ClassInfo classInfo : clzList) {
            String className = classInfo.getClassName();
            CtClass ctClass = pool.get(className);
            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if ("triggerOnManualDiscard".equals(method.getName())) {
                    ChimeraCardsPlus.logger.info("- Patching {}.triggerOnManualDiscard", ctClass.getName());
                    method.insertBefore(AbstractAugmentPlusPatches.class.getName() + ".onManualDiscard(this);");
                } else if ("onMoveToDiscard".equals(method.getName())) {
                    ChimeraCardsPlus.logger.info("- Patching {}.onMoveToDiscard", ctClass.getName());
                    method.insertBefore(AbstractAugmentPlusPatches.class.getName() + ".onMoveToDiscard(this);");
                } else if ("onRemoveFromMasterDeck".equals(method.getName())) {
                    ChimeraCardsPlus.logger.info("- Patching {}.onRemoveFromMasterDeck", ctClass.getName());
                    method.insertBefore(AbstractAugmentPlusPatches.class.getName() + ".onRemoveFromMasterDeck(this);");
                }
            }
        }
        ChimeraCardsPlus.logger.info("- Dynamic AbstractCard patches complete.");
    }

    private static void applyBaseGameCardPatches(ClassPool pool) throws NotFoundException, CannotCompileException {
        ChimeraCardsPlus.logger.info("- Dynamic base game card patches started.");
        ArrayList<ClassInfo> clzList = new ArrayList<>(Constants.EXPECTED_CARDS);
        stsFinder.findClasses(clzList, nonInterfaceAbstractCardSubclassFilter);
        ChimeraCardsPlus.logger.info("- Potential targets found for dynamic base game card patches({}).", clzList.size());
        for (ClassInfo classInfo : clzList) {
            String className = classInfo.getClassName();
            CtClass ctClass = pool.get(className);
            CtConstructor ctConstructor = ctClass.getClassInitializer();
            if (ctConstructor == null) {
                ChimeraCardsPlus.logger.info("- Class initializer of {} not found, making one.", className);
                ctConstructor = ctClass.makeClassInitializer();
            }
            ctConstructor.insertAfter(CardDescriptionPatches.class.getName() + ".rewriteDescriptions(" + className + ".class);");
        }
        ChimeraCardsPlus.logger.info("- Dynamic base game card patches complete.");
    }
}