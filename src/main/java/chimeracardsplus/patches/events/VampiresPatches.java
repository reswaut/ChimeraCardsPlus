package chimeracardsplus.patches.events;


import CardAugments.patches.RolledModFieldPatches.RolledModField;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.rare.BitingMod;
import chimeracardsplus.cards.preview.BitingPreview;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.events.shrines.Transmogrifier;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;

import java.util.ArrayList;
import java.util.stream.Stream;

public class VampiresPatches {
    private static final String ID = ChimeraCardsPlus.makeID(VampiresPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex = 0;
    private static int hpAmt = 0;
    private static boolean choseVanillaOption = true;

    private static int calcHp() {
        return Math.min(AbstractDungeon.player.maxHealth - 1, MathUtils.ceil(AbstractDungeon.player.maxHealth * 0.06F));
    }

    public static void updateEvent(Vampires __instance) {
        if (!ChimeraCardsPlus.enableEventAddons()) {
            return;
        }
        if (choseVanillaOption) {
            return;
        }
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.player.masterDeck.removeCard(card);

            BitingMod augment = new BitingMod();
            ArrayList<AbstractCard> validCards = new ArrayList<>(512);
            validCards.add(new Bite());

            for (AbstractCard c : CardLibrary.getAllCards()) {
                if (c.type == CardType.ATTACK && Stream.of(CardRarity.COMMON, CardRarity.UNCOMMON, CardRarity.RARE).anyMatch(cardRarity -> c.rarity == cardRarity)) {
                    AbstractCard copy = c.makeStatEquivalentCopy();
                    if (augment.canApplyTo(copy)) {
                        validCards.add(copy);
                    }
                }
            }

            AbstractCard transformedCard = validCards.get(AbstractDungeon.miscRng.random(0, validCards.size() - 1));
            CardModifierManager.addModifier(transformedCard, augment);
            RolledModField.rolled.set(transformedCard, true);
            AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(transformedCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    @SpirePatch(
            clz = Vampires.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(Vampires __instance) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return;
            }
            __instance.imageEventText.removeDialogOption(__instance.imageEventText.optionList.size() - 1);
            myIndex = __instance.imageEventText.optionList.size();
            if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).isEmpty()) {
                __instance.imageEventText.setDialogOption(OPTIONS[2], true);
            } else {
                hpAmt = calcHp();
                __instance.imageEventText.setDialogOption(OPTIONS[0] + hpAmt + OPTIONS[1], new BitingPreview());
            }
            __instance.imageEventText.setDialogOption(Vampires.OPTIONS[2]);
        }
    }

    @SpirePatch(
            clz = Vampires.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(Vampires __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            if (___screenNum[0] != 0) {
                return SpireReturn.Continue();
            }
            if (buttonPressed[0] < myIndex) {
                return SpireReturn.Continue();
            }
            if (buttonPressed[0] > myIndex) {
                --buttonPressed[0];
                return SpireReturn.Continue();
            }

            applyModifiers();
            AbstractDungeon.player.decreaseMaxHealth(hpAmt);

            __instance.showProceedScreen(Vampires.DESCRIPTIONS[2]);
            ___screenNum[0] = 1;
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, Transmogrifier.OPTIONS[2], false, false, false, false);
            choseVanillaOption = false;
        }
    }

    @SpirePatch2(
            clz = Vampires.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class AddUpdateOverride {
        @SpireRawPatch
        public static void addMethod(CtBehavior ctMethodToPatch) throws CannotCompileException, NotFoundException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            CtClass superClass = ctClass.getSuperclass();
            CtMethod superMethod = superClass.getDeclaredMethod("update");
            CtMethod updateMethod = CtNewMethod.delegator(superMethod, ctClass);

            try {
                ctClass.addMethod(updateMethod);
            } catch (DuplicateMemberException var6) {
                updateMethod = ctClass.getDeclaredMethod("update");
            }

            updateMethod.insertAfter(VampiresPatches.class.getName() + ".updateEvent($0);");
        }
    }
}
